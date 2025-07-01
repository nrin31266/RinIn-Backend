package com.linkedin.backend.features.feed.service;

import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.feed.dto.CommentDto;
import com.linkedin.backend.features.feed.dto.PostDto;
import com.linkedin.backend.features.feed.dto.request.CommentRequest;
import com.linkedin.backend.features.feed.dto.request.PostRequest;
import com.linkedin.backend.features.feed.dto.request.ReactRequest;
import com.linkedin.backend.features.feed.dto.request.TARGET_ACTION;
import com.linkedin.backend.features.feed.model.Comment;
import com.linkedin.backend.features.feed.model.PostBackground;

import java.util.List;

public interface FeedService {
    PostDto createPost(PostRequest request, User author);
    PostDto getPost(Long postId);
    void react(ReactRequest request, User authenticatedUser);
    void unReact(ReactRequest request ,User authenticatedUser);
    CommentDto comment(CommentRequest request, User authenticatedUser);
    void deleteComment(Long commentId, User authenticatedUser);
    Comment updateComment(CommentRequest request, Long commentId, User authenticatedUser);
    List<CommentDto> getComments(TARGET_ACTION targetAction, Long targetId, User authenticatedUser);
    void deletePost(Long postId, User user);
    void updatePost(Long postId, PostRequest request, User user);
    List<PostDto> getMyPosts(User user);
    List<PostDto> getPosts(User user);
    List<PostBackground> getAllPostBg();
}
