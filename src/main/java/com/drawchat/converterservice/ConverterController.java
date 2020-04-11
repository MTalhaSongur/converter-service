package com.drawchat.converterservice;

import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@RestController
public class ConverterController {

    private final Path rootLocation = Paths.get("filestorage");

    @RequestMapping("/convert")
    public String convert( @RequestBody String req) throws Exception {
        if(req == null || req == "")
            return "ERROR : No body received!! Cannot Convert Image";

        req = req.split("=")[1];

        String decodedBody = getBase64Decoded(req);
        FileConverter converter = new FileConverter();
        try {
            //Generate folders based on randomID
            //Uncomment this if you want to use the PPTX2PDF func.
            //generateOutputFolder(rootLocation.resolve("outputs/pdf/").toString(), Long.toString(converter.getID()));
            generateOutputFolder(rootLocation.resolve("outputs/images/").toString(), Long.toString(converter.getID()));
            //--
            //Uncomment if you want to convert to PDF or convert to PNG from PDF
//            converter.PPTX2PDF(new FileInputStream(decodedBody), new FileOutputStream(rootLocation.resolve("outputs/pdf/" + converter.getID() + "/out.pdf").toString()));
//            converter.PDF2PNG(rootLocation.resolve("outputs/pdf/" + converter.getID() + "/out.pdf").toString(), rootLocation.resolve("outputs/images/" + converter.getID()).toString());
            converter.PPTX2PNG(new FileInputStream(decodedBody),rootLocation.resolve("outputs/images/" + converter.getID()).toAbsolutePath().toString());

        }catch (Exception e){
            return e.toString();
        }

        return "path:" + rootLocation.resolve("outputs/images").toAbsolutePath() + "/" + Long.toString(converter.getID()) + ",size:" + converter.getPageSize() + ",width:" + converter.getWidth() + ",height:" + converter.getHeight();
    }

    @GetMapping("/convertdefault")
    public String convertDefault() throws Exception {
        FileConverter converter = new FileConverter();
        try {
            //Generate folders based on randomID
            //Uncomment this if you want to use the PPTX2PDF func.
            //generateOutputFolder(rootLocation.resolve("outputs/pdf/").toString(), Long.toString(converter.getID()));
            generateOutputFolder(rootLocation.resolve("outputs/images/").toString(), Long.toString(converter.getID()));
            //--
            //Uncomment if you want to convert to PDF or convert to PNG from PDF
//            converter.PPTX2PDF(new FileInputStream(rootLocation.resolve("inputs/deneme.pptx").toString()), new FileOutputStream(rootLocation.resolve("outputs/pdf/" + converter.getID() + "/out.pdf").toString()));
//            converter.PDF2PNG(rootLocation.resolve("outputs/pdf/" + converter.getID() + "/out.pdf").toString(), rootLocation.resolve("outputs/images/" + converter.getID()).toString());
            converter.PPTX2PNG(new FileInputStream(rootLocation.resolve("inputs/deneme.pptx").toString()),rootLocation.resolve("outputs/images/" + converter.getID()).toAbsolutePath().toString());
        }catch (Exception e){
            return e.toString();
        }

        return "path:" + rootLocation.resolve("outputs/images").toAbsolutePath() + "/" + Long.toString(converter.getID()) + ",size:" + converter.getPageSize() + ",width:" + converter.getWidth() + ",height:" + converter.getHeight();
    }

    //Utilities-------------------------------------------------------------------

    private boolean generateOutputFolder(String folder, String nameOfFolder) {
        try {
            File file = new File(folder + "/" + nameOfFolder);
            file.mkdir();
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    private String getBase64Decoded(String encodedString) {
        //Decode the given string encoded in Base64
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        return new String(decodedBytes);
    }

    private byte[] getBase64Decoded(byte[] encodedString) {
        return Base64.getDecoder().decode(encodedString);
    }
}
