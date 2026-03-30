package com.project.service;

import java.util.List;

import com.project.dto.FavoriteDTO;

public interface FavoriteService {

    List<FavoriteDTO> findMyFavorites(String email);

    FavoriteDTO addMyFavorite(Long productId, String email);

    void deleteMyFavorite(Long productId, String email);

    boolean isMyFavorite(Long productId, String email);
}
