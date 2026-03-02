package com.personal.site.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "album_images")
public class AlbumImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 120)
    private String title;

    @Column(name = "file_name", nullable = false, length = 200)
    private String fileName;

    @Column(name = "file_url", nullable = false, length = 255)
    private String fileUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
