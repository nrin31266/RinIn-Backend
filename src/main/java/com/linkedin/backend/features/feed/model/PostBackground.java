package com.linkedin.backend.features.feed.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class PostBackground {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;


    String bgImgUrl;
    String bgColor;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)

    POST_BG_TYPE type;
    String textColor;
}
