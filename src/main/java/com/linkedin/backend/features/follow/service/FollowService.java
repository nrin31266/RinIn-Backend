package com.linkedin.backend.features.follow.service;

import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.follow.model.Follow;

import java.util.List;
import java.util.concurrent.Flow;

public interface FollowService {
    // Define methods for follow service operations, such as:
    // - Follow a user
    // - Unfollow a user
    // - Get followers of a user
    // - Get users followed by a user
    // - Check if a user is following another user

    // Example method signatures:
    void followUser(User authenticatedUser, Long followingId);
    void unfollowUser(User authenticatedUser, Long followingId);
    void followUser(Long followerId, Long followingId);
    void unfollowUser(Long followerId, Long followingId);
    List<Follow> getFollowers(User authenticatedUser);
    List<Follow> getFollowing(User authenticatedUser);
    boolean isFollowing(Long followerId, Long followingId);
}
