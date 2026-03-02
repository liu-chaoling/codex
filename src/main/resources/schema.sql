CREATE TABLE IF NOT EXISTS users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    display_name VARCHAR(80) NOT NULL,
    avatar_url VARCHAR(255),
    bio VARCHAR(255),
    created_at DATETIME NOT NULL
);

CREATE TABLE IF NOT EXISTS posts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    content VARCHAR(2000) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_posts_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS post_images (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    file_name VARCHAR(200) NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_post_images_post FOREIGN KEY (post_id) REFERENCES posts(id)
);

CREATE TABLE IF NOT EXISTS album_images (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(120) NOT NULL,
    file_name VARCHAR(200) NOT NULL,
    file_url VARCHAR(255) NOT NULL,
    created_at DATETIME NOT NULL,
    CONSTRAINT fk_album_images_user FOREIGN KEY (user_id) REFERENCES users(id)
);
