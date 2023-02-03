package edu.servidor.objects.Objects.utils;

import edu.servidor.objects.Objects.models.FileData;
import edu.servidor.objects.Objects.models.ObjectFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


public class FileUtils {

    public static ResponseEntity<byte[]> fileBuilder(ObjectFile objectFile, FileData fileData) {
        byte[] fileContent = fileData.getBody();
        String name = getFileName(objectFile);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(objectFile.getContentType()));
        headers.setContentLength(fileContent.length);
        headers.set("Content-disposition", "attachment;filename=" + name);
        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    public static String getFileName(ObjectFile objectFile) {
        String[] objectFileUriSplit = objectFile.getUri().split("/");
        return objectFileUriSplit[objectFileUriSplit.length - 1];
    }
}
