package com.drawchat.converterservice;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.util.FileSystemUtils;

import javax.imageio.ImageIO;

//This class is responsible for getting pptx file from sender and returning the .png file instead.
public class FileConverter {

    private InputStream inStream;
    private OutputStream outStream;
    private XSLFSlide[] slides;
    private final long ID;

    public FileConverter() {
        ID = getRandomNumber(100000, 999999);
    }

    public long getID() {
        return ID;
    }

    public void PPTX2PDF(InputStream inStream, OutputStream outStream) throws Exception {
        //TODO:Remnant from constructor. Remove the variables.
        this.inStream = inStream;
        this.outStream = outStream;

        Dimension pgsize = processSlides();

        double zoom = 2; // magnify it by 2 as typical slides are low res
        AffineTransform at = new AffineTransform();
        at.setToScale(zoom, zoom);

        Document document = new Document();

        PdfWriter writer = PdfWriter.getInstance(document, outStream);
        document.open();

        for (int i = 0; i < getNumSlides(); i++) {

            BufferedImage bufImg = new BufferedImage((int)Math.ceil(pgsize.width*zoom), (int)Math.ceil(pgsize.height*zoom), BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = bufImg.createGraphics();
            graphics.setTransform(at);
            //clear the drawing area
            graphics.setPaint(getSlideBGColor(i));
            graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));
            try{
                drawOntoThisGraphic(i, graphics);
            } catch(Exception e){
                //Just ignore
            }

            Image image = Image.getInstance(bufImg, null);
            document.setPageSize(new Rectangle(image.getScaledWidth(), image.getScaledHeight()));
            document.newPage();
            image.setAbsolutePosition(0, 0);
            document.add(image);
        }
        document.close();
        writer.close();
    }

    public void PDF2PNG(String sourceFilePath, String targetFolder) throws Exception {
        File sourceFile = new File(sourceFilePath);
        File destinationFile = new File(targetFolder);
        if(!destinationFile.exists()) {
            System.out.println("No folder detected at given path. Creating a new folder");
            destinationFile.mkdir();
            System.out.println("Folder Created  at-> "+ destinationFile.getAbsolutePath());
        }
        if(!sourceFile.exists()) {
            System.err.println("ERROR : Source folder does not exists!!!");
            return;
        }
        System.out.println("Images copied to Folder: "+ destinationFile.getName());
        PDDocument document = PDDocument.load(sourceFilePath);
        List<PDPage> list = document.getDocumentCatalog().getAllPages();
        System.out.println("Total files to be converted -> "+ list.size());

        String fileName = sourceFile.getName().replace(".pdf", "");
        int pageNumber = 1;
        for (PDPage page : list) {
            BufferedImage image = page.convertToImage();
            File outputfile = new File(targetFolder + "/" + pageNumber +".png");
            System.out.println("Image Created -> "+ outputfile.getName());
            ImageIO.write(image, "png", outputfile);
            pageNumber++;
        }
        document.close();
        System.out.println("Converted Images are saved at -> "+ destinationFile.getAbsolutePath());
    }

    public boolean resetFiles() {
        final Path filesLoc = Paths.get("outputs");
        boolean status = true;
        FileSystemUtils.deleteRecursively(filesLoc.toFile());
        File file = new File(filesLoc.toString() + "/outputs/images");
        status = file.mkdirs();
        file = new File(filesLoc.toString() + "/outputs/pdf");
        status = file.mkdirs();
        return status;
    }

    private Dimension processSlides() throws IOException{
        InputStream iStream = inStream;
        XMLSlideShow ppt = new XMLSlideShow(iStream);
        Dimension dimension = ppt.getPageSize();
        slides = ppt.getSlides();
        return dimension;
    }

    private int getNumSlides(){
        return slides.length;
    }

    private void drawOntoThisGraphic(int index, Graphics2D graphics){
        slides[index].draw(graphics);
    }

    private Color getSlideBGColor(int index){
        return slides[index].getBackground().getFillColor();
    }

    private long getRandomNumber(long sIndex, long fIndex) {
        return (long)(Math.random()*((fIndex-sIndex)+1))+sIndex;
    }

}