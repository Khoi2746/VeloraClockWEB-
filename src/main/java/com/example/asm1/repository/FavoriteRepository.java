package com.example.asm1.repository;

import com.example.asm1.Entity.Favorite;
import com.example.asm1.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    List<Favorite> findByUser(User user);

    // --- SỬA DÒNG NÀY ---
    // Từ: Favorite findByUserAndProduct_Id(...)
    // Thành: List<Favorite> findByUserAndProduct_Id(...)
    List<Favorite> findByUserAndProduct_Id(User user, Long productId);
}