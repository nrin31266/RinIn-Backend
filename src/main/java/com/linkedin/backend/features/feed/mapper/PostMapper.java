package com.linkedin.backend.features.feed.mapper;

import com.linkedin.backend.features.feed.dto.request.PostRequest;
import com.linkedin.backend.features.feed.dto.response.PostResponse;
import com.linkedin.backend.features.feed.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PostMapper {
    Post toPost(PostRequest postRequest);
    void updatePost(@MappingTarget Post post, PostRequest postRequest);
    PostResponse toPostResponse(Post post);
}
