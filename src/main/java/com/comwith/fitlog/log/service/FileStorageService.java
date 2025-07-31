package com.comwith.fitlog.log.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;


@Service
public class FileStorageService {

    @Value("${file.upload.dir:./uploads}")
    private String uploadDir;

    public String storeFile(MultipartFile file) {
        try {
            File directory = new File(uploadDir);
            // 오류 해결을 위한 폴더 경로 추적위한 코드 (임시 디버깅용 추가)
            System.out.println("Upload directory: " + directory.getAbsolutePath());

            if (!directory.exists()) {
                directory.mkdirs();
            }

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            File targetFile = new File(directory, fileName);

            file.transferTo(targetFile);

            return "/uploads/" + fileName;

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
        }
    }

    public void deleteFile(String photoUrl) {
        // 파일 삭제 로직
        try {
            File file = new File(uploadDir + photoUrl.replace("/uploads/", "/"));
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            // 로그 출력
        }
    }
}
