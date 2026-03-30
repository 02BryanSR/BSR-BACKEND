package com.project.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String storeProductImage(MultipartFile file);

    String storeCategoryImage(MultipartFile file);

    void deleteIfManaged(String publicPath);
}
