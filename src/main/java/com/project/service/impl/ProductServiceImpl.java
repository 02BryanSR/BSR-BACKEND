package com.project.service.impl;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.project.dto.ProductAdminForm;
import com.project.dto.ProductDTO;
import com.project.entity.CategoryEntity;
import com.project.entity.ProductEntity;
import com.project.mapper.ProductMapper;
import com.project.repository.CategoryRepository;
import com.project.repository.ProductRepository;
import com.project.service.ProductService;
import com.project.service.StorageService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final int DEFAULT_STOCK = 50;

    private final ProductMapper mapper;
    private final ProductRepository repo;
    private final CategoryRepository categoryRepo;
    private final StorageService storageService;

    public ProductServiceImpl(ProductMapper mapper, ProductRepository repo, CategoryRepository categoryRepo,
			StorageService storageService) {
		super();
		this.mapper = mapper;
		this.repo = repo;
		this.categoryRepo = categoryRepo;
		this.storageService = storageService;
	}

	@Override
    public List<ProductDTO> findAll() {
        List<ProductEntity> products = repo.findAll();
        return mapper.toListDtos(products);
    }

    @Override
    public List<ProductDTO> findByCategoryId(Long categoryId) {
        List<ProductEntity> products = repo.findByCategory_Id(categoryId);
        return mapper.toListDtos(products);
    }

    @Override
    public ProductDTO findById(Long id) {
        ProductEntity product = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        return mapper.toDto(product);
    }

    @Override
    public List<ProductDTO> findByName(String name) {
        if (name == null || name.isBlank()) {
            throw new RuntimeException("Name is required");
        }

        List<ProductEntity> products = repo.findByNameContainingIgnoreCase(name.trim());
        return mapper.toListDtos(products);
    }


    @Override
    public ProductDTO createProduct(ProductAdminForm form) {
        validateForm(form);

        String normalizedName = form.getName().trim();
        if (repo.existsByNameIgnoreCase(normalizedName)) {
            throw new IllegalArgumentException("A product with that name already exists");
        }

        CategoryEntity category = categoryRepo.findById(form.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        String normalizedSku = resolveSku(form.getSku(), category, null);

        if (repo.existsBySkuIgnoreCase(normalizedSku)) {
            throw new IllegalArgumentException("A product with that sku already exists");
        }

        ProductEntity product = new ProductEntity();
        product.setName(normalizedName);
        product.setSku(normalizedSku);
        product.setDescription(form.getDescription().trim());
        product.setPrice(form.getPrice());
        product.setStock(resolveStock(form.getStock(), DEFAULT_STOCK));
        product.setCategory(category);
        product.setImageUrl(resolveImageUrl(form.getImageUrl(), form.getImage(), null));

        return mapper.toDto(repo.save(product));
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductAdminForm form) {
        validateForm(form);

        ProductEntity product = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        String normalizedName = form.getName().trim();
        if (!product.getName().equalsIgnoreCase(normalizedName)
                && repo.existsByNameIgnoreCaseAndIdNot(normalizedName, id)) {
            throw new IllegalArgumentException("A product with that name already exists");
        }

        CategoryEntity category = categoryRepo.findById(form.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        String normalizedSku = resolveSku(form.getSku(), category, product);

        if (!product.getSku().equalsIgnoreCase(normalizedSku)
                && repo.existsBySkuIgnoreCaseAndIdNot(normalizedSku, id)) {
            throw new IllegalArgumentException("A product with that sku already exists");
        }

        product.setName(normalizedName);
        product.setSku(normalizedSku);
        product.setDescription(form.getDescription().trim());
        product.setPrice(form.getPrice());
        product.setStock(
                resolveStock(form.getStock(), product.getStock() == null ? DEFAULT_STOCK : product.getStock()));
        product.setCategory(category);
        product.setImageUrl(resolveImageUrl(form.getImageUrl(), form.getImage(), product.getImageUrl()));

        return mapper.toDto(repo.save(product));
    }

    @Override
    public void deleteProduct(Long id) {
        ProductEntity product = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));

        storageService.deleteIfManaged(product.getImageUrl());
        repo.delete(product);
    }

    private void validateForm(ProductAdminForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Product data is required");
        }
        if (form.getName() == null || form.getName().isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (form.getDescription() == null || form.getDescription().isBlank()) {
            throw new IllegalArgumentException("Product description is required");
        }
        if (form.getPrice() == null || form.getPrice().signum() <= 0) {
            throw new IllegalArgumentException("Price is invalid");
        }
        if (form.getStock() != null && form.getStock() < 0) {
            throw new IllegalArgumentException("Stock is invalid");
        }
        if (form.getCategoryId() == null) {
            throw new IllegalArgumentException("Category is required");
        }
    }

    private String resolveImageUrl(String requestedImageUrl, org.springframework.web.multipart.MultipartFile image,
            String currentImageUrl) {
        if (image != null && !image.isEmpty()) {
            storageService.deleteIfManaged(currentImageUrl);
            return storageService.storeProductImage(image);
        }

        if (requestedImageUrl != null && !requestedImageUrl.isBlank()) {
            String normalized = requestedImageUrl.trim();

            if (!normalized.equals(currentImageUrl)) {
                storageService.deleteIfManaged(currentImageUrl);
            }
            return normalized;
        }

        return currentImageUrl;
    }

    private String normalizeSku(String sku) {
        return sku.trim().toUpperCase();
    }

    private String resolveSku(String requestedSku, CategoryEntity category, ProductEntity currentProduct) {
        if (requestedSku != null && !requestedSku.isBlank()) {
            return normalizeSku(requestedSku);
        }

        if (currentProduct != null && currentProduct.getSku() != null && !currentProduct.getSku().isBlank()) {
            return currentProduct.getSku().trim().toUpperCase(Locale.ROOT);
        }

        return generateSku(category);
    }

    private String generateSku(CategoryEntity category) {
        long sequence = repo.findTopByOrderByIdDesc()
                .map(ProductEntity::getId)
                .orElse(0L) + 1L;

        String categoryCode = resolveCategoryCode(category == null ? null : category.getName());
        String candidate = formatSku(categoryCode, sequence);

        while (repo.existsBySkuIgnoreCase(candidate)) {
            sequence += 1L;
            candidate = formatSku(categoryCode, sequence);
        }

        return candidate;
    }

    private String formatSku(String categoryCode, long sequence) {
        return "BSR-" + categoryCode + "-" + String.format(Locale.ROOT, "%04d", sequence);
    }

    private String resolveCategoryCode(String categoryName) {
        String normalized = normalizeToken(categoryName);

        if (normalized.isBlank()) {
            return "GEN";
        }

        if (normalized.startsWith("WOM")) {
            return "WOM";
        }
        if (normalized.startsWith("MEN") || normalized.startsWith("HOM")) {
            return "MEN";
        }
        if (normalized.startsWith("KID") || normalized.startsWith("NIN") || normalized.startsWith("CHI")) {
            return "KID";
        }
        if (normalized.startsWith("ACC") || normalized.startsWith("COM")) {
            return "ACC";
        }

        return padCategoryCode(normalized);
    }

    private String padCategoryCode(String value) {
        StringBuilder builder = new StringBuilder(value.replaceAll("[^A-Z0-9]", ""));

        while (builder.length() < 3) {
            builder.append('X');
        }

        return builder.substring(0, 3);
    }

    private String normalizeToken(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return Normalizer.normalize(value.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^\\p{Alnum}\\s]", "")
                .toUpperCase(Locale.ROOT);
    }

    private Integer resolveStock(Integer requestedStock, Integer fallbackStock) {
        if (requestedStock == null) {
            return fallbackStock;
        }

        return requestedStock;
    }


   
    @Override
    public boolean hasStock(Long productId, Integer requiredQuantity) {
        if (productId == null) {
            throw new RuntimeException("productId is required");
        }
        if (requiredQuantity == null || requiredQuantity < 0) {
            throw new RuntimeException("requiredQuantity is invalid");
        }

        ProductEntity product = repo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        return product.getStock() != null && product.getStock() >= requiredQuantity;
    }

    @Override
    public ProductDTO increaseStock(Long productId, Integer amount) {
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        ProductEntity productFound = repo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (productFound.getStock() == null) {
            productFound.setStock(0);
        }

        productFound.setStock(productFound.getStock() + amount);

        ProductEntity saved = repo.save(productFound);
        return mapper.toDto(saved);
    }

    @Override
    public ProductDTO decreaseStock(Long productId, Integer amount) {
        if (amount == null || amount <= 0) {
            throw new RuntimeException("Invalid amount");
        }

        ProductEntity product = repo.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!hasStock(productId, amount)) {
            throw new RuntimeException("Not enough stock");
        }

        product.setStock(product.getStock() - amount);

        ProductEntity saved = repo.save(product);
        return mapper.toDto(saved);
    }

    @Override
    public Integer getProductStock(Long productId) {
        if (productId == null) {
            throw new RuntimeException("productId is required");
        }

        ProductEntity product = repo.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        return product.getStock();
    }

    private void validateProduct(ProductDTO dto) {
        if (dto == null) {
            throw new RuntimeException("ProductDTO is required");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new RuntimeException("Product name is required");
        }
        if (dto.getSku() == null || dto.getSku().isBlank()) {
            throw new RuntimeException("Product sku is required");
        }
        if (dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new RuntimeException("Product description is required");
        }
        if (dto.getCategoryId() == null) {
            throw new RuntimeException("categoryId is required");
        }
        if (dto.getPrice() == null || dto.getPrice() <= 0) {
            throw new RuntimeException("Price is invalid");
        }
        if (dto.getStock() != null && dto.getStock() < 0) {
            throw new RuntimeException("Stock cannot be negative");
        }
    }
}
