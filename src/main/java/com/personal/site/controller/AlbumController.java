package com.personal.site.controller;

import com.personal.site.common.ApiResponse;
import com.personal.site.dto.AlbumImageResponse;
import com.personal.site.entity.User;
import com.personal.site.service.AlbumService;
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
@RequestMapping("/api/album")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<AlbumImageResponse> upload(@AuthenticationPrincipal User user,
                                                  @RequestParam("title") String title,
                                                  @RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(albumService.upload(user.getId(), title, file));
    }

    @GetMapping
    public ApiResponse<List<AlbumImageResponse>> myAlbum(@AuthenticationPrincipal User user) {
        return ApiResponse.ok(albumService.listByUser(user.getId()));
    }
}
