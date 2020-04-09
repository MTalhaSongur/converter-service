package com.drawchat.converterservice;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@RestController
public class FileDownloadController {

    private final Path rootLocation = Paths.get("filestorage");

    @GetMapping("/files")
    public String getListFiles(Model model) {
        List<FileInfo> fileInfos = loadFiles().map(
                path ->  {
                    String filename = path.getFileName().toString();
                    String url = MvcUriComponentsBuilder.fromMethodName(FileDownloadController.class,
                            "downloadFile", path.getFileName().toString()).build().toString();
                    return new FileInfo(filename, url);
                }
        )
                .collect(Collectors.toList());

        model.addAttribute("files", fileInfos);
        return "No list found";
    }

    @GetMapping(
            value = "/files/{filename}",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public byte[] downloadFile(@PathVariable String filename) throws Exception {
//        //Basic conversion and view example
//        //TODO:Remove this after showing example.
//        //TODO:Reset Files after each file
//        //TODO:Every time user wants to get a new page whole pptx file converts again. Get pages from temp if its already rendered.
//        FileConverter converter = new FileConverter();
//        converter.PPTX2PDF(new FileInputStream(rootLocation.resolve("inputs/deneme.pptx").toString()), new FileOutputStream(rootLocation.resolve("outputs/pdf/current.pdf").toString()));
//        converter.PDF2PNG(rootLocation.resolve("outputs/pdf/current.pdf").toString(), rootLocation.resolve("outputs/images").toString());


        try {
            Resource fileR = loadFile(filename, ".png");
            //NOTE:IF you want to download the file instead of showing on the browser screen, uncomment the code below and change returning type to Response Entity.
//            File file = new File(
//                    getClass().getClassLoader().getResource(fileR.toString()).getFile()
//            );
//            return ResponseEntity.ok()
//                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileR.getFilename() + "\"")
//                    .body(fileR);
            byte[] byteFile = Files.readAllBytes(fileR.getFile().toPath());
            return byteFile;
        }catch (RuntimeException re) {
            return null;
        }
    }

    //--------------------------------------------------------------------------------------------------------------------
    //-----------------------------------------------------UTILITIES------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------------------

    public Resource loadFile(String filename, String fileExtension) {
        try {
            Path file = rootLocation.resolve("outputs/images/" + filename + fileExtension);
            Resource resource = new UrlResource(file.toUri());
            if(resource.exists() || resource.isReadable()) {
                return resource;
            }else{
                System.out.println("Wanted file : " + filename + " not found.");
                throw new RuntimeException("FAIL!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error! -> message = " + e.getMessage());
        }
    }

    public Stream<Path> loadFiles() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        }
        catch (IOException e) {
            throw new RuntimeException("\"Failed to read stored file");
        }
    }
}
