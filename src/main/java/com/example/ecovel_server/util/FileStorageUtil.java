package com.example.ecovel_server.util;

//이미지 파일을 로컬 디스크에 저장하는 유틸리티 클래스

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Component
public class FileStorageUtil {

    private static final String BASE_UPLOAD_DIR = "/tmp/uploads";
    private static final String BASE_DOWNLOAD_DIR = "/tmp/downloads";

    // Save user uploaded images
    public File saveImageToFile(MultipartFile file, Long planId, int day, String placeId) {
        String dirPath = String.format("%s/%d/day%d", BASE_UPLOAD_DIR, planId, day);
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = placeId + "_" + System.currentTimeMillis() + ".jpg";
        File dest = new File(dir, fileName);
        try {
            if (file == null || file.isEmpty()) {
                throw new RuntimeException("The uploaded image is empty.");
            }
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save images", e);
        }

        return dest;
    }

    // Download registered face image from URL
    public File downloadImageFromUrl(String url, Long planId, int day, String placeId) {
        try (InputStream in = new URL(url).openStream()) {
            String dirPath = String.format("%s/%d/day%d", BASE_DOWNLOAD_DIR, planId, day);
            File dir = new File(dirPath);
            if (!dir.exists()) dir.mkdirs();

            String fileName = "registered_" + placeId + ".jpg";
            File file = new File(dir, fileName);

            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return file;
        } catch (IOException e) {
            throw new RuntimeException("Failed to download registered face image", e);
        }
    }
}
