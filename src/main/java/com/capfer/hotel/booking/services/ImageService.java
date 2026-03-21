package com.capfer.hotel.booking.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class ImageService {
    private final Path root = Path.of("uploads");

    public String saveImageV1(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new IOException("Failed to store empty file.");
            }
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path destinationFile = root.resolve(Path.of(filename)).normalize().toAbsolutePath();
            file.transferTo(destinationFile);
            return filename;
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    public String saveImageV2(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new IOException("Failed to store empty file.");
            }

            // Use Java 25 modern Path API
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Files.copy(file.getInputStream(), this.root.resolve(filename));

            return filename; // Save this string in your DB
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }
}
