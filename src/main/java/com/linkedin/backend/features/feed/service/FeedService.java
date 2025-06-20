package com.linkedin.backend.features.feed.service;

import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.feed.dto.PostDto;
import com.linkedin.backend.features.feed.dto.request.PostRequest;
import com.linkedin.backend.features.feed.dto.request.ReactRequest;
import com.linkedin.backend.features.feed.model.PostBackground;

import java.util.List;

public interface FeedService {
    PostDto createPost(PostRequest request, User author);
    List<PostDto> getAllPosts(int page, int size);
    PostDto getPostById(Long postId);
    void reactToPost(Long postId, ReactRequest request, User user);
    void commentOnPost(Long postId, String comment, User user);
    void deletePost(Long postId, User user);
    void updatePost(Long postId, PostRequest request, User user);
    List<PostDto> getMyPosts(User user);
    List<PostDto> getPostsByConnection(User user);
    public List<PostBackground> getAllPostBg();
}
