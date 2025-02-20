package com.example.onlineshoesstoreprm392.utils;

import com.example.onlineshoesstoreprm392.exception.OnlineStoreAPIException;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FileUtility {

    public static boolean isValidImage(String contentType) {
        return contentType.equals("image/jpeg") || contentType.equals("image/png") || contentType.equals("image/jpg");
    }

    @Async("taskExecutor")
    public void saveFile(MultipartFile file, String filePath){
        try {
            File convertFile = new File(filePath);
            convertFile.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(convertFile);
            fileOutputStream.write(file.getBytes());
            fileOutputStream.close();
        }catch (IOException ex){
            throw new OnlineStoreAPIException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Some error occur when processing file");
        }
    }

    public static void checkImages(List<MultipartFile> images){
        if(images.isEmpty()){
            throw new OnlineStoreAPIException(HttpStatus.BAD_REQUEST,
                    "No product's image added yet.");
        }

        for(MultipartFile img : images){
            String contentType = img.getContentType();
            if(!isValidImage(contentType)){
                throw new OnlineStoreAPIException(HttpStatus.BAD_REQUEST,
                        "Invalid file type. Only JPG, JPEG and PNG allowed.");
            }
        }
    }
}
