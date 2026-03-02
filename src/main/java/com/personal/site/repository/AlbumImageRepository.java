package com.personal.site.repository;

import com.personal.site.entity.AlbumImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlbumImageRepository extends JpaRepository<AlbumImage, Long> {
    List<AlbumImage> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
