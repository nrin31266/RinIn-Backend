package com.linkedin.backend.features.follow.service;

import com.linkedin.backend.exception.AppException;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.authentication.service.AuthenticationUserService;
import com.linkedin.backend.features.follow.model.Follow;
import com.linkedin.backend.features.follow.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class FollowServiceImpl implements FollowService {
    AuthenticationUserService authenticationUserService;
    FollowRepository followRepository;

    @Override
    public void followUser(User authenticatedUser, Long followingId) {
        User followingUser = authenticationUserService.getUserById(followingId);
        followRepository.findByFollowerIdAndFollowingId(authenticatedUser.getId(), followingUser.getId())
                .ifPresentOrElse(
                        follow -> {
                            // User is already following, you might want to throw an exception or log this
                            log.warn("User is already following this user.");
                        },
                        () -> {
                            // Create a new follow relationship
                            if(followingUser.getId().equals(authenticatedUser.getId())) {
                                throw new AppException("You cannot follow yourself.");
                            }
                            followRepository.save(new Follow(null, authenticatedUser, followingUser, null));
                        }
                );
    }

    @Override
    public void unfollowUser(User authenticatedUser, Long followingId) {
        User followingUser = authenticationUserService.getUserById(followingId);
        followRepository.findByFollowerIdAndFollowingId(authenticatedUser.getId(), followingUser.getId())
                .ifPresentOrElse(
                        follow -> {
                            // Delete the follow relationship
                            followRepository.deleteByFollowerIdAndFollowingId(authenticatedUser.getId(), followingUser.getId());
                        },
                        () -> {
                            // User is not following, you might want to throw an exception or log this
                            // Don't throw an exception here, just log it
                            log.warn("User is not following this user.");
                        }
                );

    }

    @Override
    public void followUser(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) {
            throw new AppException("Follower ID and Following ID cannot be null.");
        }
        User follower = authenticationUserService.getUserById(followerId);
        followUser(follower, followingId);
    }

    @Override
    public void unfollowUser(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) {
            throw new AppException("Follower ID and Following ID cannot be null.");
        }
        User follower = authenticationUserService.getUserById(followerId);
        unfollowUser(follower, followingId);
    }

    @Override
    public List<Follow> getFollowers(User authenticatedUser) {

        return followRepository.findByFollowingId(authenticatedUser.getId());
    }

    @Override
    public List<Follow> getFollowing(User authenticatedUser) {
        return followRepository.findByFollowerId(authenticatedUser.getId());
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) {
            throw new AppException("Follower ID and Following ID cannot be null.");
        }
        return followRepository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }
}
