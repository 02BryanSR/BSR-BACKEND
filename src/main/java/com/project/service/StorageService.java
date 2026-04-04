package com.project.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String storeProductImage(MultipartFile file);

    String storeCategoryImage(MultipartFile file);

    String normalizeManagedPublicPath(String publicPath);

    void deleteIfManaged(String publicPath);
}
