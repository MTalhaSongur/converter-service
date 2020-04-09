package com.drawchat.converterservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@RestController
public class ConverterController {

    private final Path rootLocation = Paths.get("filestorage");
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

    @GetMapping("/convert")
    public String convert( @RequestBody String req) throws Exception {
        if(req == null || req == "")
            return "ERROR : No body received!! Cannot Convert Image";

        String decodedBody = getBase64Decoded(req);
        File file = new File(decodedBody);
        FileConverter converter = new FileConverter();
        try {
            converter.PPTX2PDF(new FileInputStream(decodedBody), new FileOutputStream(file.getParent() + "/current.pdf"));
            converter.PDF2PNG(file.getParent() + "/current.pdf", rootLocation.resolve("outputs/images").toString());
        }catch (Exception e){
            return e.toString();
        }
        return "Completed.";
    }

    @GetMapping("/convertdefault")
    public String convertDefault() throws Exception {
        try {
            FileConverter converter = new FileConverter();
             converter.PPTX2PDF(new FileInputStream(rootLocation.resolve("inputs/deneme.pptx").toString()), new FileOutputStream(rootLocation.resolve("outputs/pdf/current.pdf").toString()));
             converter.PDF2PNG(rootLocation.resolve("outputs/pdf/current.pdf").toString(), rootLocation.resolve("outputs/images").toString());
        }catch (Exception e){
            return e.toString();
        }
        return "Completed.";
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
