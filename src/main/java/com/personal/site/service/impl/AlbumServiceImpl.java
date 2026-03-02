package com.personal.site.service.impl;

import com.personal.site.dto.AlbumImageResponse;
import com.personal.site.entity.AlbumImage;
import com.personal.site.entity.User;
import com.personal.site.repository.AlbumImageRepository;
import com.personal.site.repository.UserRepository;
import com.personal.site.service.AlbumService;
import com.personal.site.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlbumServiceImpl implements AlbumService {

    private final AlbumImageRepository albumImageRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public AlbumServiceImpl(AlbumImageRepository albumImageRepository,
                            UserRepository userRepository,
                            FileStorageService fileStorageService) {
        this.albumImageRepository = albumImageRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public AlbumImageResponse upload(Long userId, String title, MultipartFile file) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("用户不存在"));
        String path = fileStorageService.store(file, "album");

        AlbumImage image = new AlbumImage();
        image.setUser(user);
        image.setTitle(title);
        image.setFileName(file.getOriginalFilename());
        image.setFileUrl(path);
        image.setCreatedAt(LocalDateTime.now());

        return map(albumImageRepository.save(image));
    }

    @Override
    public List<AlbumImageResponse> listByUser(Long userId) {
        return albumImageRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::map)
                .collect(Collectors.toList());
    }

    private AlbumImageResponse map(AlbumImage image) {
        return AlbumImageResponse.builder()
                .id(image.getId())
                .userId(image.getUser().getId())
                .title(image.getTitle())
                .fileName(image.getFileName())
                .fileUrl(image.getFileUrl())
                .createdAt(image.getCreatedAt())
                .build();
    }
}
