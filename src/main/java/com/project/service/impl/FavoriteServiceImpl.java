package com.project.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.dto.FavoriteDTO;
import com.project.entity.CustomerEntity;
import com.project.entity.FavoriteEntity;
import com.project.entity.ProductEntity;
import com.project.repository.CustomerRepository;
import com.project.repository.FavoriteRepository;
import com.project.repository.ProductRepository;
import com.project.service.FavoriteService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository repo;
    private final CustomerRepository customerRepo;
    private final ProductRepository productRepo;

    public FavoriteServiceImpl(FavoriteRepository repo, CustomerRepository customerRepo, ProductRepository productRepo) {
        this.repo = repo;
        this.customerRepo = customerRepo;
        this.productRepo = productRepo;
    }

    @Override
    public List<FavoriteDTO> findMyFavorites(String email) {
        CustomerEntity customer = getCustomerByEmail(email);
        return repo.findByCustomerIdOrderByCreateDateDesc(customer.getId()).stream().map(this::toDto).toList();
    }

    @Override
    public FavoriteDTO addMyFavorite(Long productId, String email) {
        if (productId == null) {
            throw new IllegalArgumentException("productId is required");
        }

        CustomerEntity customer = getCustomerByEmail(email);

        if (repo.existsByCustomerIdAndProductId(customer.getId(), productId)) {
            return repo.findByCustomerIdAndProductId(customer.getId(), productId)
                    .map(this::toDto)
                    .orElseThrow(() -> new EntityNotFoundException("Favorite not found"));
        }

        ProductEntity product = getProductById(productId);

        FavoriteEntity favorite = new FavoriteEntity();
        favorite.setCustomer(customer);
        favorite.setProduct(product);

        return toDto(repo.save(favorite));
    }

    @Override
    public void deleteMyFavorite(Long productId, String email) {
        if (productId == null) {
            throw new IllegalArgumentException("productId is required");
        }

        CustomerEntity customer = getCustomerByEmail(email);

        if (!repo.existsByCustomerIdAndProductId(customer.getId(), productId)) {
            throw new EntityNotFoundException("Favorite not found");
        }

        repo.deleteByCustomerIdAndProductId(customer.getId(), productId);
    }

    @Override
    public boolean isMyFavorite(Long productId, String email) {
        if (productId == null) {
            throw new IllegalArgumentException("productId is required");
        }

        CustomerEntity customer = getCustomerByEmail(email);
        return repo.existsByCustomerIdAndProductId(customer.getId(), productId);
    }

    private CustomerEntity getCustomerByEmail(String email) {
        return customerRepo.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
    }

    private ProductEntity getProductById(Long productId) {
        return productRepo.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
    }

    private FavoriteDTO toDto(FavoriteEntity entity) {
        ProductEntity product = entity.getProduct();

        return new FavoriteDTO(
                entity.getId(),
                entity.getCustomer().getId(),
                product.getId(),
                product.getName(),
                product.getSku(),
                product.getImageUrl(),
                product.getPrice(),
                product.getStock(),
                entity.getCreateDate()
        );
    }
}
