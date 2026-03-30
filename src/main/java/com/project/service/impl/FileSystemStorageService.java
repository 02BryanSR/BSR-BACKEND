package com.project.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.project.service.StorageService;

@Service
public class FileSystemStorageService implements StorageService {

    private static final String PRODUCT_PUBLIC_PREFIX = "/uploads/products/";
    private static final String CATEGORY_PUBLIC_PREFIX = "/uploads/categories/";
    private final Path productsDir;
    private final Path categoriesDir;

    public FileSystemStorageService(@Value("${app.upload.root:uploads}") String uploadRoot) {
        try {
            this.productsDir = Path.of(uploadRoot, "products").toAbsolutePath().normalize();
            this.categoriesDir = Path.of(uploadRoot, "categories").toAbsolutePath().normalize();
            Files.createDirectories(this.productsDir);
            Files.createDirectories(this.categoriesDir);
        } catch (IOException ex) {
            throw new RuntimeException("Could not initialize storage directory", ex);
        }
    }

    @Override
    public String storeProductImage(MultipartFile file) {
        return storeImage(file, productsDir, PRODUCT_PUBLIC_PREFIX);
    }

    @Override
    public String storeCategoryImage(MultipartFile file) {
        return storeImage(file, categoriesDir, CATEGORY_PUBLIC_PREFIX);
    }

    private String storeImage(MultipartFile file, Path targetDir, String publicPrefix) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        String ext = StringUtils.getFilenameExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + (StringUtils.hasText(ext) ? "." + ext.toLowerCase() : "");

        Path target = targetDir.resolve(fileName).normalize();
        if (!target.startsWith(targetDir)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        try {
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return publicPrefix + fileName;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file", ex);
        }
    }

    @Override
    public void deleteIfManaged(String publicPath) {
        if (!StringUtils.hasText(publicPath)) {
            return;
        }

        String normalized = publicPath.trim();
        Path baseDir;
        String fileName;

        if (normalized.startsWith(PRODUCT_PUBLIC_PREFIX)) {
            baseDir = productsDir;
            fileName = normalized.substring(PRODUCT_PUBLIC_PREFIX.length());
        } else if (normalized.startsWith(CATEGORY_PUBLIC_PREFIX)) {
            baseDir = categoriesDir;
            fileName = normalized.substring(CATEGORY_PUBLIC_PREFIX.length());
        } else {
            return;
        }

        Path target = baseDir.resolve(fileName).normalize();
        if (!target.startsWith(baseDir)) {
            return;
        }

        try {
            Files.deleteIfExists(target);
        } catch (IOException ex) {
            throw new RuntimeException("Could not delete file", ex);
        }
    }
}
