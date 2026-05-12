package com.livehealth.news;

import com.livehealth.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "news")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    UUID id;

    @Column(nullable = false)
    String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    String content;

    String thumbnailUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    NewsCategory category;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "news_tag_mapping", joinColumns = @JoinColumn(name = "news_id"), inverseJoinColumns = @JoinColumn(name = "tag_id"))
    List<NewsTag> tags;

    @Column(name = "image_url_1")
    String imageUrl1;

    @Column(name = "image_url_2")
    String imageUrl2;

    @OneToMany(mappedBy = "news", cascade = CascadeType.ALL, orphanRemoval = true)
    List<NewsComment> comments;

    @Column(nullable = false)
    LocalDateTime createdAt;

    @Column(nullable = false)
    LocalDateTime updatedAt;
}
