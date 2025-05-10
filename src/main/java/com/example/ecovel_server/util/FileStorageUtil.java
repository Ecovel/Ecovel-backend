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

    private static final String BASE_UPLOAD_DIR = "uploads"; // 상대경로 or 절대경로

    // 사용자가 업로드한 이미지
    public String saveImageAndReturnUrl(MultipartFile file, Long planId, int day, String placeId) {
        String dirPath = String.format("%s/%d/day%d", BASE_UPLOAD_DIR, planId, day);
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = placeId + "_" + System.currentTimeMillis() + ".jpg";
        File dest = new File(dir, fileName);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }

        // 클라이언트 접근 가능한 URL 반환
        return String.format("/uploads/%d/day%d/%s", planId, day, fileName);
    }


    // 프론트에서 받는 등록된 얼굴 이미지
    public File downloadImageFromUrl(String url, Long planId, int day, String placeId) {
        try (InputStream in = new URL(url).openStream()) {
            String dirPath = String.format("downloads/%d/day%d", planId, day);
            File dir = new File(dirPath);
            if (!dir.exists()) dir.mkdirs();

            String fileName = "registered_" + placeId + ".jpg";
            File file = new File(dir, fileName);

            Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return file;
        } catch (IOException e) {
            throw new RuntimeException("등록된 얼굴 이미지 다운로드 실패", e);
        }
    }
}
