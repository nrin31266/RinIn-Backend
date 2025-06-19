package com.linkedin.backend.features.feed.service;

import com.linkedin.backend.exception.AppException;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.authentication.repository.AuthenticationUserRepository;
import com.linkedin.backend.features.feed.dto.PostDto;
import com.linkedin.backend.features.feed.dto.request.PostRequest;
import com.linkedin.backend.features.feed.dto.request.ReactRequest;
import com.linkedin.backend.features.feed.mapper.CommentMapper;
import com.linkedin.backend.features.feed.mapper.PostMapper;
import com.linkedin.backend.features.feed.model.POST_TYPE;
import com.linkedin.backend.features.feed.model.Post;
import com.linkedin.backend.features.feed.model.PostBackground;
import com.linkedin.backend.features.feed.repository.CommentRepository;
import com.linkedin.backend.features.feed.repository.PostBackgroundRepository;
import com.linkedin.backend.features.feed.repository.PostRepository;
import com.linkedin.backend.features.notifications.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                if (request.getPostBgId() == null) {
                    throw new AppException("Post background ID is required for BACKGROUND post type");
                }
                PostBackground postBackground = getPostBackground(request.getPostBgId());
                post.setPostBg(postBackground);
                break;
            default:
                throw new AppException("Invalid post type");
        }

        post = postRepository.save(post);

        PostDto postDto = PostDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .author(post.getAuthor())
                .creationDate(post.getCreationDate())
                .updateDate(post.getUpdateDate())
                .postMedia(post.getPostMedia())
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
        Map<Long, Map<String, Integer>> reactionCountsMap = getReactionCounts(posts);
        return posts.stream()
                .map(post -> {
                    Map<String, Integer> reactCounts = reactionCountsMap.getOrDefault(post.getId(), new HashMap<>());
                    PostDto postDto = postMapper.toPostDto(post);
                    postDto.setReactCounts(reactCounts);
                    postDto.setCommentCount(post.getComments().size());
                    return postDto;
                })
                .toList();
    }

    private Map<Long, Map<String, Integer>> getReactionCounts(List<Post> posts) {
        List<Long> postIds = posts.stream().map(Post::getId).toList();
        List<Object[]> reactionCountsRaw = postRepository.countReactionsByTypeForPosts(postIds);
        Map<Long, Map<String, Integer>> reactionCountsMap = new HashMap<>();
        for (Object[] row : reactionCountsRaw) {
            Long postId = (Long) row[0];
            String type = (String) row[1];
            Integer count = ((Number) row[2]).intValue();
            reactionCountsMap.computeIfAbsent(postId, k -> new HashMap<>()).put(type, count);
        }
        return reactionCountsMap;
    }

    @Override
    public List<PostDto> getPostsByConnection(User user) {
        List<Post> posts = postRepository.findPostsByConnection(user.getId());
        Map<Long, Map<String, Integer>> reactionCountsMap = getReactionCounts(posts);
        return posts.stream()
                .map(post -> {
                    Map<String, Integer> reactCounts = reactionCountsMap.getOrDefault(post.getId(), new HashMap<>());
                    PostDto postDto = postMapper.toPostDto(post);
                    postDto.setReactCounts(reactCounts);
                    postDto.setCommentCount(post.getComments().size());
                    return postDto;
                })
                .toList();

    }


    @Override
    public List<PostDto> getAllPosts(int page, int size) {
        return List.of();
    }

    @Override
    public PostDto getPostById(Long postId) {
        return null;
    }

    @Override
    public void reactToPost(Long postId, ReactRequest request, User user) {

    }

    @Override
    public void commentOnPost(Long postId, String comment, User user) {

    }

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
//    public List<PostBackground> getAllPostBg(){
//        return postBackgroundRepository.findAll();
//    }

}
