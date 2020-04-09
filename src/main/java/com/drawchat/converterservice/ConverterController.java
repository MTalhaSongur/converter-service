package com.drawchat.converterservice;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.io.*;
import java.nio.file.Files;

@RestController
public class ConverterController {
//    @GetMapping(
//            value = "/getConvertedImage",
//            produces = MediaType.IMAGE_PNG_VALUE
//    )
//    public @ResponseBody byte[] getConvertedImage() throws Exception{
//
//        String resourceDir = System.getProperty("user.dir") + "\\filestorage";
//
//        FileConverter service = new FileConverter(new FileInputStream(resourceDir + "\\inputs\\deneme.pptx"), new FileOutputStream(resourceDir + "\\outputs\\pdf\\current.pdf"));
//        service.PPTX2PDF();
//        service.PDF2PNG(resourceDir + "\\outputs\\pdf\\current.pdf", resourceDir + "\\outputs\\images\\");
//
//        File fi = new File(resourceDir + "\\inputs\\Alliswell.png");
//        byte[] fileContent = Files.readAllBytes(fi.toPath());
//        return fileContent;
//    }
}
