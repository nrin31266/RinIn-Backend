package com.linkedin.backend.features.feed.mapper;

import com.linkedin.backend.features.feed.dto.request.CommentRequest;
import com.linkedin.backend.features.feed.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    void updateComment(@MappingTarget Comment comment, CommentRequest commentRequest);

    Comment toComment(CommentRequest commentRequest);
}
