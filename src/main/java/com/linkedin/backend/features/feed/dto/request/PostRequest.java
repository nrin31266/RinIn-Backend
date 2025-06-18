package com.linkedin.backend.features.feed.dto.request;

import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.feed.model.POST_TYPE;
import com.linkedin.backend.features.feed.model.PostMedia;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.print.attribute.standard.Media;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PostRequest {
    @NotBlank(message = "POST_CONTENT_IS_REQUIRED")
    String content;
    Long postBgId;
    POST_TYPE postType;
    List<PostMedia> postMedias;
}
