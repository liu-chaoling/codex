package com.personal.site.service.impl;

import com.personal.site.dto.PostResponse;
import com.personal.site.entity.Post;
import com.personal.site.entity.PostImage;
import com.personal.site.entity.User;
import com.personal.site.repository.PostRepository;
import com.personal.site.repository.UserRepository;
import com.personal.site.service.FileStorageService;
import com.personal.site.service.PostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public PostServiceImpl(PostRepository postRepository,
                           UserRepository userRepository,
                           FileStorageService fileStorageService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    @Transactional
    public PostResponse create(Long userId, String content, List<MultipartFile> images) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        Post post = new Post();
        post.setUser(user);
        post.setContent(content);
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());

        if (images != null) {
            for (MultipartFile image : images) {
                String path = fileStorageService.store(image, "post");
                PostImage postImage = new PostImage();
                postImage.setPost(post);
                postImage.setFileName(image.getOriginalFilename());
                postImage.setFileUrl(path);
                postImage.setCreatedAt(LocalDateTime.now());
                post.getImages().add(postImage);
            }
        }

        Post saved = postRepository.save(post);
        return map(saved);
    }

    @Override
    public List<PostResponse> list() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream().map(this::map).collect(Collectors.toList());
    }

    private PostResponse map(Post post) {
        List<String> images = post.getImages() == null ? Collections.emptyList()
                : post.getImages().stream().map(PostImage::getFileUrl).collect(Collectors.toList());

        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUser().getId())
                .username(post.getUser().getUsername())
                .displayName(post.getUser().getDisplayName())
                .avatarUrl(post.getUser().getAvatarUrl())
                .content(post.getContent())
                .imageUrls(images)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
