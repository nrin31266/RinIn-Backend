package com.linkedin.backend.features.feed.service;

import com.linkedin.backend.exception.AppException;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.authentication.repository.AuthenticationUserRepository;
import com.linkedin.backend.features.feed.dto.PostDto;
import com.linkedin.backend.features.feed.dto.request.CommentRequest;
import com.linkedin.backend.features.feed.dto.request.PostRequest;
import com.linkedin.backend.features.feed.dto.request.ReactRequest;
import com.linkedin.backend.features.feed.mapper.CommentMapper;
import com.linkedin.backend.features.feed.mapper.PostMapper;
import com.linkedin.backend.features.feed.model.*;
import com.linkedin.backend.features.feed.repository.*;
import com.linkedin.backend.features.notifications.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedServiceImpl implements FeedService {
    PostRepository postRepository;
    AuthenticationUserRepository authenticationUserRepository;
    PostMapper postMapper;
    CommentRepository commentRepository;
    CommentMapper commentMapper;
    NotificationService notificationService;
    PostBackgroundRepository postBackgroundRepository;
    ReactRepository reactRepository;
    PostMediaRepository postMediaRepository;

    @Override
    public PostDto createPost(PostRequest request, User author) {
        Post post = postMapper.toPost(request);
        post.setAuthor(author);

        switch (request.getPostType()) {
            case NORMAL:
                post.setPostType(POST_TYPE.NORMAL);
                break;
            case BACKGROUND:
                post.setPostType(POST_TYPE.BACKGROUND);
                post.setPostMedias(new ArrayList<>());
                if (request.getPostBgId() == null) {
                    throw new AppException("Post background ID is required for BACKGROUND post type");
                }
                PostBackground postBackground = getPostBackground(request.getPostBgId());
                post.setPostBg(postBackground);
                break;
            default:
                throw new AppException("Invalid post type");
        }
        if (post.getPostMedias() != null) {
            for (PostMedia media : post.getPostMedias()) {
                media.setPost(post);
            }
        }

        post = postRepository.save(post);

        PostDto postDto = PostDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .author(post.getAuthor())
                .creationDate(post.getCreationDate())
                .updateDate(post.getUpdateDate())
                .postMedias(post.getPostMedias())
                .commentCount(0)
                .postBg(post.getPostBg())
                .postType(post.getPostType())
                .build();

        return postDto;
    }

    private PostBackground getPostBackground(Long postBgId) {
        return postBackgroundRepository.findById(postBgId)
                .orElseThrow(() -> new AppException("Post background not found"));
    }



    @Override
    public List<PostDto> getMyPosts(User user) {
        List<Post> posts = postRepository.findAllByAuthorIdOrderByCreationDate(user.getId());
        return getPostDtos(posts, user.getId());
    }

    private List<PostDto> getPostDtos(List<Post> posts, Long userId) {
        List<Long> postIds = posts.stream().map(Post::getId).toList();

        Map<Long, PostDto> postDtoMap = posts.stream()
                .collect(Collectors.toMap(
                        Post::getId,
                        post -> {
                            PostDto dto = postMapper.toPostDto(post);
                            dto.setReactCounts(new HashMap<>());
                            return dto;
                        }
                ));

        // Query dữ liệu react
        List<Object[]> reactionCounts = reactRepository.countReactionsByTypeForPosts(postIds, userId);
        List<Object[]> countComments = commentRepository.countCommentsByPostIds(postIds);
//        log.info("Reaction counts: {}", reactionCounts);
//        log.info("Comment counts: {}", countComments);

        for (Object[] row : reactionCounts) {
            Long postId = (Long) row[0];
            REACT_TYPE type = (REACT_TYPE) row[1];
            Integer count = ((Number) row[2]).intValue();
            Boolean isReacted = (Boolean) row[3];
            REACT_TYPE myReactType = (REACT_TYPE) row[4];

            PostDto postDto = postDtoMap.get(postId);
            if (postDto == null) continue;

            postDto.getReactCounts().put(type, count);

            // Ghi nhận phản ứng của người dùng (nếu có)
            if (Boolean.TRUE.equals(isReacted)) {
                postDto.setIsReacted(true);
                postDto.setMyReactType(myReactType);
            }
        }
        // Ghi nhận số lượng bình luận
        for (Object[] row : countComments) {
            Long postId = (Long) row[0];
            Integer count = ((Number) row[1]).intValue();

            PostDto postDto = postDtoMap.get(postId);
            if (postDto != null) {
                postDto.setCommentCount(count);
            }
        }

        // Duyệt lại danh sách gốc để trả ra danh sách theo đúng thứ tự
        return posts.stream()
                .map(post -> postDtoMap.get(post.getId()))
                .toList();
    }



    @Override
    public List<PostDto> getPosts(User user) {
        List<Post> posts = postRepository.findPostsByConnection(user.getId());
        return getPostDtos(posts, user.getId());

    }


    @Override
    public PostDto getPost(Long postId) {
        return null;
    }

    private PostMedia getPostMediaById(Long postMediaId) {
        return postMediaRepository.findById(postMediaId)
                .orElseThrow(() -> new AppException("Post media not found with ID: " + postMediaId));
    }
    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException("Comment not found with ID: " + commentId));
    }

    @Override
    public void react(ReactRequest request, User authenticatedUser) {
        Long targetId = request.getTargetId();
        switch (request.getTargetAction()) {
            case POST -> {
                Post post = getPostById(targetId);
                reactRepository.findByPostIdAndAuthorId(post.getId(), authenticatedUser.getId())
                        .ifPresentOrElse(react -> {
                            // If the user has already reacted, update the reaction type
                            react.setReactType(request.getReactType());
                            reactRepository.save(react);
                        }, () -> {
                            // If the user has not reacted, create a new reaction
                            React react = new React();
                            react.setPost(post);
                            react.setAuthor(authenticatedUser);
                            react.setReactType(request.getReactType());
                            reactRepository.save(react);
                            // Notify the post author about the new reaction
                        });
            }
            case POST_MEDIA -> {
                PostMedia postMedia = getPostMediaById(targetId);
                reactRepository.findByPostMediaIdAndAuthorId(postMedia.getId(), authenticatedUser.getId())
                        .ifPresentOrElse(react -> {
                            // If the user has already reacted, update the reaction type
                            react.setReactType(request.getReactType());
                            reactRepository.save(react);
                        }, () -> {
                            // If the user has not reacted, create a new reaction
                            React react = new React();
                            react.setPostMedia(postMedia);
                            react.setAuthor(authenticatedUser);
                            react.setReactType(request.getReactType());
                            reactRepository.save(react);
                            // Notify the post media author about the new reaction
                        });
            }
            case COMMENT -> {
                Comment comment = getCommentById(targetId);
                reactRepository.findByCommentIdAndAuthorId(comment.getId(), authenticatedUser.getId())
                        .ifPresentOrElse(react -> {
                            // If the user has already reacted, update the reaction type
                            react.setReactType(request.getReactType());
                            reactRepository.save(react);
                        }, () -> {
                            // If the user has not reacted, create a new reaction
                            React react = new React();
                            react.setComment(comment);
                            react.setAuthor(authenticatedUser);
                            react.setReactType(request.getReactType());
                            reactRepository.save(react);
                            // Notify the comment author about the new reaction
                        });
            }
            default -> {
                throw new AppException("Not supported target action: " + request.getTargetAction());
            }
        }
    }

    @Override
    public void unReact(ReactRequest request, User authenticatedUser) {
        Long targetId = request.getTargetId();
        switch (request.getTargetAction()) {
            case POST -> {
                Post post = getPostById(targetId);
                React react = reactRepository.findByPostIdAndAuthorId(post.getId(), authenticatedUser.getId())
                        .orElseThrow(() -> new AppException("Reaction not found for user on this post"));
                reactRepository.delete(react);
                // Optionally notify the post author about the reaction removal
            }
            case POST_MEDIA -> {
                PostMedia postMedia = getPostMediaById(targetId);
                React react = reactRepository.findByPostMediaIdAndAuthorId(postMedia.getId(), authenticatedUser.getId())
                        .orElseThrow(() -> new AppException("Reaction not found for user on this post media"));
                reactRepository.delete(react);
                // Optionally notify the post media author about the reaction removal
            }
            case COMMENT -> {
                Comment comment = getCommentById(targetId);
                React react = reactRepository.findByCommentIdAndAuthorId(comment.getId(), authenticatedUser.getId())
                        .orElseThrow(() -> new AppException("Reaction not found for user on this comment"));
                reactRepository.delete(react);
                // Optionally notify the comment author about the reaction removal
            }
            default -> throw new AppException("Not supported target action: " + request.getTargetAction());
        }
    }

    @Override
    public void comment(CommentRequest request, User authenticatedUser) {
        Long targetId = request.getTargetId();
        switch (request.getTargetAction()) {
            case POST -> {
                Post post = getPostById(targetId);
                Comment newComment = commentMapper.toComment(request);
                newComment.setPost(post);
                newComment.setAuthor(authenticatedUser);
                newComment.setParentComment(null); // No parent comment for top-level comments
                commentRepository.save(newComment);
                // Notify the post author about the new comment
            }
            case POST_MEDIA -> {
                PostMedia postMedia = getPostMediaById(targetId);
                Comment newComment = commentMapper.toComment(request);
                newComment.setPostMedia(postMedia);
                newComment.setAuthor(authenticatedUser);
                commentRepository.save(newComment);
                // Notify the post media author about the new comment
            }
            case COMMENT -> {
                Comment parentComment = getCommentById(targetId);
                Comment newComment = commentMapper.toComment(request);
                newComment.setParentComment(parentComment);
                newComment.setAuthor(authenticatedUser);
                commentRepository.save(newComment);
                // Notify the parent comment author about the new reply
            }
            default -> throw new AppException("Not supported target action: " + request.getTargetAction());
        }
    }

    @Override
    public void deleteComment(Long commentId, User authenticatedUser) {
        Comment comment = getCommentById(commentId);
        if (!comment.getAuthor().getId().equals(authenticatedUser.getId())) {
            throw new AppException("You are not authorized to delete this comment");
        }
        commentRepository.delete(comment);
        // Optionally notify the post author about the comment deletion
    }

    @Override
    public void updateComment(CommentRequest request, Long commentId, User authenticatedUser) {
        Comment existingComment = getCommentById(commentId);
        if (!existingComment.getAuthor().getId().equals(authenticatedUser.getId())) {
            throw new AppException("You are not authorized to update this comment");
        }
        existingComment.setContent(request.getContent());
        existingComment.setUpdateDate(null); // Reset update date to current time
        commentRepository.save(existingComment);
        // Optionally notify the post author about the comment update
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new AppException("Post not found with ID: " + postId));
    }


//    @Override
//    public void reactToPost(Long postId, ReactRequest request, User authenticatedUser) {
//        Post post = getPostById(postId);
//        reactRepository.findByPostIdAndAuthorId(postId, authenticatedUser.getId())
//                .ifPresentOrElse(react -> {
//                    // If the user has already reacted, update the reaction type
//                    react.setReactType(request.getReactType());
//                    reactRepository.save(react);
//                }, () -> {
//                    // If the user has not reacted, create a new reaction
//                    React react = new React();
//                    react.setPost(post);
//                    react.setAuthor(authenticatedUser);
//                    react.setReactType(request.getReactType());
//                    reactRepository.save(react);
//                    // Notify the post author about the new reaction
//                });
//    }
//
//    @Override
//    public void unReactToPost(Long postId, User user) {
//        Post post = getPostById(postId);
//        React react = reactRepository.findByPostIdAndAuthorId(postId, user.getId())
//                .orElseThrow(() -> new AppException("Reaction not found for user on this post"));
//        reactRepository.delete(react);
//        // Optionally notify the post author about the reaction removal
//    }


    @Override
    public void deletePost(Long postId, User user) {

    }

    @Override
    public void updatePost(Long postId, PostRequest request, User user) {

    }


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
    @Override
    public List<PostBackground> getAllPostBg() {
        return postBackgroundRepository.findAll();
    }

}
