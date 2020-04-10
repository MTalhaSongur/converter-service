package com.drawchat.converterservice;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
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

    @GetMapping("/fileinfo")
    public Map<String, String> getPaths() throws IOException {
        HashMap<String, String> map = new HashMap<>();
        map.put("PageSize", Long.toString(getNumberOfFilesInFolder(rootLocation.toString() + "/outputs/images/")));
        String[] files = getFilesInFolder(rootLocation.toString() + "/outputs/images/");
        Arrays.sort(files);
        for(int i = 0; i < files.length; i++) {
            map.put("Page" + Paths.get(files[i]).getFileName().toString().replace(".png", ""), files[i]);
        }
        return map;
    }

    //--------------------------------------------------------------------------------------------------------------------
    //-----------------------------------------------------UTILITIES------------------------------------------------------
    //--------------------------------------------------------------------------------------------------------------------

    private String[] getFilesInFolder(String path) throws IOException {
        ArrayList<String> filesInFolder = new ArrayList<>();
        Files.walk(Paths.get(path))
                .filter(p -> p.toString().endsWith(".png"))
                .forEach(p -> {
                    try {
                        filesInFolder.add(p.toAbsolutePath().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        return filesInFolder.toArray(new String[filesInFolder.size()]);
    }

    private long getNumberOfFilesInFolder(String path) throws IOException {
        long count = 0;
        try (Stream<Path> files = Files.list(Paths.get(path))) {
            count = files.count();
        }
        return count;
    }

    private Resource loadFile(String filename, String fileExtension) {
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

    private Stream<Path> loadFiles() {
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
