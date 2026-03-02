package com.personal.site.controller;

import com.personal.site.common.ApiResponse;
import com.personal.site.dto.PostResponse;
import com.personal.site.entity.User;
import com.personal.site.service.PostService;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<PostResponse> create(@AuthenticationPrincipal User user,
                                            @RequestParam("content") String content,
                                            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        return ApiResponse.ok(postService.create(user.getId(), content, images));
    }

    @GetMapping
    public ApiResponse<List<PostResponse>> list() {
        return ApiResponse.ok(postService.list());
    }
}
