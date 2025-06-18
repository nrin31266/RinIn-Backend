package com.linkedin.backend.features.feed.service;

import com.linkedin.backend.features.authentication.repository.AuthenticationUserRepository;
import com.linkedin.backend.features.feed.mapper.CommentMapper;
import com.linkedin.backend.features.feed.mapper.PostMapper;
import com.linkedin.backend.features.feed.repository.CommentRepository;
import com.linkedin.backend.features.feed.repository.PostBackgroundRepository;
import com.linkedin.backend.features.feed.repository.PostRepository;
import com.linkedin.backend.features.notifications.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedServiceImpl {
    PostRepository postRepository;
    AuthenticationUserRepository authenticationUserRepository;
    PostMapper postMapper;
    CommentRepository commentRepository;
    CommentMapper commentMapper;
    NotificationService notificationService;
    PostBackgroundRepository postBackgroundRepository;

//    public Post createPost(PostRequest postRequest, Long authorId) {
//        User user = authenticationUserRepository.findById(authorId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
//        Post post = postMapper.toPost(postRequest);
//        post.setAuthor(user);
//        if(postRequest.getPostBgId()!=null){
//            PostBackground postBackground = postBackgroundRepository.findById(postRequest.getPostBgId()).get();
//            post.setPostBg(postBackground);
//        }
//        return postRepository.save(post);
//    }
//
//    public Post updatePost(Long postId, PostRequest postRequest, Long authorId) {
//        User user = authenticationUserRepository.findById(authorId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
//        Post post = postRepository.findById(postId).orElseThrow(()-> new AppException(ErrorCode.POST_NOT_FOUND));
//
//        if(!post.getAuthor().getId().equals(user.getId())) {
//            throw new AppException(ErrorCode.UNAUTHORIZED);
//        }
//
//        postMapper.updatePost(post, postRequest);
//        return postRepository.save(post);
//    }
//
//
//    public List<Post> getFeedPosts(Long authenticatedUserId) {
//        List<Post> posts = postRepository.findByAuthorIdOrderByCreationDateDesc(authenticatedUserId);
//        return posts;
//    }
//
//    public Post getPost(Long postId) {
//        return postRepository.findById(postId).orElseThrow(()-> new AppException(ErrorCode.POST_NOT_FOUND));
//    }
//
//
//    public List<Post> getAllPosts() {
//        return postRepository.findAllByOrderByCreationDateDesc();
//    }
//
//    public List<Post> getPostsByUserId(Long userId) {
//        return postRepository.findByAuthorId(userId);
//    }
//
//    public void deletePost(Long postId, Long authenticatedUserId) {
//        Post post = postRepository.findById(postId).orElseThrow(()-> new AppException(ErrorCode.POST_NOT_FOUND));
//        User user = authenticationUserRepository.findById(authenticatedUserId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
//        if(!post.getAuthor().getId().equals(user.getId())) {
//            throw new AppException(ErrorCode.UNAUTHORIZED);
//        }
//        postRepository.delete(post);
//    }
//
//    @Transactional
//    public Post likePost(Long postId, Long userId) {
//
//        User user = authenticationUserRepository.findById(userId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
//        Post post = postRepository.findById(postId).orElseThrow(()-> new AppException(ErrorCode.POST_NOT_FOUND));
//
//        if(post.getLikes().contains(user)) {
//            post.getLikes().remove(user);
//
//        }else{
//
//            post.getLikes().add(user);
//            notificationService.sendLikeNotification(user, post.getAuthor(), post.getId());
//        }
//
//        Post savedPost = postRepository.save(post);
//        notificationService.sendLikeToPost(postId, savedPost.getLikes());
//
//
//        return savedPost;
//    }
//
//    public Comment addComment(Long postId, CommentRequest req, Long authenticatedUserId) {
//        User user = authenticationUserRepository.findById(authenticatedUserId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
//        Post post = postRepository.findById(postId).orElseThrow(()-> new AppException(ErrorCode.POST_NOT_FOUND));
//
//        Comment comment = commentMapper.toComment(req);
//
//        comment.setPost(post);
//
//        comment.setAuthor(user);
//
//        Comment savedComment = commentRepository.save(comment);
//
//        notificationService.sendCommentNotification(user, post.getAuthor(), post.getId());
//
//        notificationService.sendCommentToPost(postId, comment);
//
//        return savedComment;
//    }
//
//    public Comment updateComment(Long commentId, CommentRequest req, Long authenticatedUserId) {
//        User user = authenticationUserRepository.findById(authenticatedUserId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
//        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new AppException(ErrorCode.COMMENT_NOT_FOUND));
//
//        if(!comment.getAuthor().getId().equals(user.getId())) {
//            throw new AppException(ErrorCode.UNAUTHORIZED);
//        }
//        commentMapper.updateComment(comment, req);
//
//        return commentRepository.save(comment);
//    }
//
//    public void removeComment(Long commentId, Long authenticatedUserId) {
//        User user = authenticationUserRepository.findById(authenticatedUserId).orElseThrow(()-> new AppException(ErrorCode.USER_NOT_FOUND));
//        Comment comment = commentRepository.findById(commentId).orElseThrow(()-> new AppException(ErrorCode.COMMENT_NOT_FOUND));
//
//        if(!comment.getAuthor().getId().equals(user.getId())) {
//            throw new AppException(ErrorCode.UNAUTHORIZED);
//        }
//        commentRepository.delete(comment);
//
//    }
//
//    public List<Comment> getComments(Long postId) {
//        Post post = postRepository.findByIdWithComments(postId).orElseThrow(()-> new AppException(ErrorCode.POST_NOT_FOUND));
//        return post.getComments().stream().sorted(Comparator.comparing(Comment::getCreationDate).reversed()).toList();
//    }
//
//
//    public Set<User> getPostLike(Long postId) {
//        Post post = postRepository.findByIdWithLikes(postId).orElseThrow(()-> new AppException(ErrorCode.POST_NOT_FOUND));
//        return post.getLikes();
//    }
//
//    public List<PostBackground> getAllPostBg(){
//        return postBackgroundRepository.findAll();
//    }

}
