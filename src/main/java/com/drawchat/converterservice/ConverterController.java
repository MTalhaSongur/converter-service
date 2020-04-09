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

    //
//    @GetMapping("/convert")
//    public String convert( @RequestBody String req) throws Exception {
//        if(req == null || req == "")
//            return "ERROR : No body received!! Cannot Convert Image";
//
//        String decodedBody = getBase64Decoded(req);
//        File file = new File(decodedBody);
//
//        try {
//            FileConverter converter = new FileConverter();
//            //Generate folders based on randomID
//            generateOutputFolder(rootLocation.resolve("outputs/pdf/").toString(), Long.toString(converter.getID()));
//            generateOutputFolder(rootLocation.resolve("outputs/images/").toString(), Long.toString(converter.getID()));
//            //--
//            converter.PPTX2PDF(new FileInputStream(decodedBody), new FileOutputStream(rootLocation.resolve("outputs/pdf/" + converter.getID() + "/out.pdf").toString()));
//            converter.PDF2PNG(rootLocation.resolve("outputs/pdf/" + converter.getID() + "/out.pdf").toString(), rootLocation.resolve("outputs/images/" + converter.getID()).toString());
//        }catch (Exception e){
//            return e.toString();
//        }
//        return "Completed.";
//    }
//
//    @GetMapping("/convertdefault")
//    public String convertDefault() throws Exception {
//        try {
//            FileConverter converter = new FileConverter();
//            //Generate folders based on randomID
//            generateOutputFolder(rootLocation.resolve("outputs/pdf/").toString(), Long.toString(converter.getID()));
//            generateOutputFolder(rootLocation.resolve("outputs/images/").toString(), Long.toString(converter.getID()));
//            //--
//            converter.PPTX2PDF(new FileInputStream(rootLocation.resolve("inputs/deneme.pptx").toString()), new FileOutputStream(rootLocation.resolve("outputs/pdf/" + converter.getID() + "/out.pdf").toString()));
//            converter.PDF2PNG(rootLocation.resolve("outputs/pdf/" + converter.getID() + "/out.pdf").toString(), rootLocation.resolve("outputs/images/" + converter.getID()).toString());
//        }catch (Exception e){
//            return e.toString();
//        }
//        return "Completed.";
//    }

    @GetMapping("/convert")
    public String convert( @RequestBody String req) throws Exception {
        if(req == null || req == "")
            return "ERROR : No body received!! Cannot Convert Image";

        String decodedBody = getBase64Decoded(req);
        File file = new File(decodedBody);
        FileConverter converter = new FileConverter();
        try {
            converter.PPTX2PDF(new FileInputStream(decodedBody), new FileOutputStream(rootLocation.resolve("outputs/pdf/current.pdf").toString()));
            converter.PDF2PNG(rootLocation.resolve("outputs/pdf/current.pdf").toString(), rootLocation.resolve("outputs/images").toString());
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
