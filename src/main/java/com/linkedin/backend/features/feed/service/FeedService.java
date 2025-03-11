package com.linkedin.backend.features.feed.service;

import com.linkedin.backend.exception.AppException;
import com.linkedin.backend.exception.ErrorCode;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.authentication.repository.AuthenticationUserRepository;
import com.linkedin.backend.features.feed.dto.request.CommentRequest;
import com.linkedin.backend.features.feed.dto.request.PostRequest;
import com.linkedin.backend.features.feed.dto.response.PostResponse;
import com.linkedin.backend.features.feed.mapper.CommentMapper;
import com.linkedin.backend.features.feed.mapper.PostMapper;
import com.linkedin.backend.features.feed.model.Comment;
import com.linkedin.backend.features.feed.model.Post;
import com.linkedin.backend.features.feed.repository.CommentRepository;
import com.linkedin.backend.features.feed.repository.PostRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedService {
    PostRepository postRepository;
    AuthenticationUserRepository authenticationUserRepository;
    PostMapper postMapper;
    CommentRepository commentRepository;
    CommentMapper commentMapper;

    public Post createPost(PostRequest postRequest, Long authorId) {
        User user = authenticationUserRepository.findById(authorId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        Post post = postMapper.toPost(postRequest);
        post.setAuthor(user);
        return postRepository.save(post);
    }

    public Post updatePost(Long postId, PostRequest postRequest, Long authorId) {
        User user = authenticationUserRepository.findById(authorId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findById(postId).orElseThrow(()-> new AppException(ErrorCode.POST_NOT_FOUND));

        if(!post.getAuthor().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        postMapper.updatePost(post, postRequest);
        return postRepository.save(post);
    }

//    @Transactional
    public List<Post> getFeedPosts(Long authenticatedUserId) {
        List<Post> posts = postRepository.findByAuthorIdOrderByCreationDateDesc(authenticatedUserId);
        return posts;
    }

    public Post getPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(()-> new AppException(ErrorCode.POST_NOT_FOUND));
    }


//    @Transactional
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByCreationDateDesc();
    }

    public List<Post> getPostsByUserId(Long userId) {
        return postRepository.findByAuthorId(userId);
    }

    public void deletePost(Long postId, Long authenticatedUserId) {
        Post post = postRepository.findById(postId).orElseThrow(()-> new AppException(ErrorCode.POST_NOT_FOUND));
        User user = authenticationUserRepository.findById(authenticatedUserId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        if(!post.getAuthor().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        postRepository.delete(post);
    }

    @Transactional
    public Post likePost(Long postId, Long userId) {

        User user = authenticationUserRepository.findById(userId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findById(postId).orElseThrow(()-> new AppException(ErrorCode.POST_NOT_FOUND));

        if(post.getLikes().contains(user)) {
            post.getLikes().remove(user);
        }else{
            post.getLikes().add(user);
        }

        return postRepository.save(post);
    }

    public Comment addComment(Long postId, CommentRequest req, Long authenticatedUserId) {
        User user = authenticationUserRepository.findById(authenticatedUserId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        Post post = postRepository.findById(postId).orElseThrow(()-> new AppException(ErrorCode.POST_NOT_FOUND));

        Comment comment = commentMapper.toComment(req);
        comment.setPost(post);
        comment.setAuthor(user);

        return commentRepository.save(comment);
    }

    public Comment updateComment(Long commentId, CommentRequest req, Long authenticatedUserId) {
        User user = authenticationUserRepository.findById(authenticatedUserId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        if(!comment.getAuthor().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        commentMapper.updateComment(comment, req);

        return commentRepository.save(comment);
    }
    public void removeComment(Long commentId, Long authenticatedUserId) {
        User user = authenticationUserRepository.findById(authenticatedUserId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        if(!comment.getAuthor().getId().equals(user.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        System.out.println("🗑️ Deleting comment: " + comment.getId());
        commentRepository.delete(comment);

        System.out.println("✅ Comment deleted.");
    }
}
