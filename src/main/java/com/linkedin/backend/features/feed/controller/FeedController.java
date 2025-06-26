package com.linkedin.backend.features.feed.controller;

import com.linkedin.backend.dto.ApiResponse;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.feed.dto.PostDto;
import com.linkedin.backend.features.feed.dto.request.CommentRequest;
import com.linkedin.backend.features.feed.dto.request.PostRequest;
import com.linkedin.backend.features.feed.dto.request.ReactRequest;
import com.linkedin.backend.features.feed.model.PostBackground;
import com.linkedin.backend.features.feed.service.FeedServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedController {
    FeedServiceImpl feedService;

    @PostMapping("/posts")
    public ApiResponse<PostDto> createPost(@RequestBody PostRequest postRequest, @RequestAttribute("authenticatedUser") User user) {
        return ApiResponse.<PostDto>builder()
                .data(feedService.createPost(postRequest, user))
                .build();
    }
//
//    @PutMapping("/posts/{postId}")
//    public ApiResponse<Post> updatePost(@PathVariable("postId") Long postId, @RequestBody PostRequest postRequest, @RequestAttribute("authenticatedUser") User user) {
//        return ApiResponse.<Post>builder()
//                .data(feedService.updatePost(postId, postRequest, user.getId()))
//                .build();
//    }
//
//    @GetMapping("/posts/{postId}")
//    public ApiResponse<Post> getPost(@PathVariable("postId") Long postId) {
//        return ApiResponse.<Post>builder()
//                .data(feedService.getPost(postId))
//                .build();
//    }
//
    @GetMapping("/posts")
    public ApiResponse<List<PostDto>> getPosts(@RequestAttribute("authenticatedUser") User user) {
        return ApiResponse.<List<PostDto>>builder()
                .data(feedService.getPosts(user))
                .build();
    }
//
    @GetMapping("/posts/user")
    public ApiResponse<List<PostDto>> getMyPosts(@RequestAttribute("authenticatedUser") User user) {
        List<PostDto> posts = feedService.getMyPosts(user);
        return ApiResponse.<List<PostDto>>builder()
                .data(posts)
                .build();
    }

    @PostMapping("/posts/react")
    public ApiResponse<Void> react(@RequestBody ReactRequest rq, @RequestAttribute("authenticatedUser") User user) {
        feedService.react(rq, user);
        return ApiResponse.<Void>builder()
                .message("Reacted to post")
                .build();
    }
    @DeleteMapping("/posts/un-react")
    public ApiResponse<Void> unReact(@RequestBody ReactRequest rq, @RequestAttribute("authenticatedUser") User user) {
        feedService.unReact(rq, user);
        return ApiResponse.<Void>builder()
                .message("Un reacted to post")
                .build();
    }
    @PostMapping("/posts/comment")
    public ApiResponse<Void> comment(@RequestBody CommentRequest rq, @RequestAttribute("authenticatedUser") User user) {
        feedService.comment(rq, user);
        return ApiResponse.<Void>builder()
                .message("Commented on post")
                .build();
    }
    @DeleteMapping("/posts/comment/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable("commentId") Long commentId, @RequestAttribute("authenticatedUser") User user) {
        feedService.deleteComment(commentId, user);
        return ApiResponse.<Void>builder()
                .message("Deleted comment")
                .build();
    }
    @PutMapping("/posts/comment/{commentId}")
    public ApiResponse<Void> updateComment(@PathVariable("commentId") Long commentId, @RequestBody CommentRequest rq, @RequestAttribute("authenticatedUser") User user) {
        feedService.updateComment(rq, commentId, user);
        return ApiResponse.<Void>builder()
                .message("Updated comment")
                .build();
    }
//
//    @GetMapping("/posts")
//    public ApiResponse<List<Post>> getAllPosts() {
//        List<Post> posts = feedService.getAllPosts();
//        return ApiResponse.<List<Post>>builder()
//                .data(posts)
//                .build();
//    }
//
//    @DeleteMapping("/posts/{postId}")
//    public ApiResponse deletePost(@PathVariable("postId") Long postId ,@RequestAttribute("authenticatedUser") User user) {
//        feedService.deletePost(postId, user.getId());
//        return ApiResponse.builder()
//                .message("Deleted post")
//                .build();
//    }
//
//    @PutMapping("posts/{postId}/like")
//    public ApiResponse<Post> likePost(@PathVariable("postId") Long postId, @RequestAttribute("authenticatedUser") User user) {
//        return ApiResponse.<Post>builder()
//                .data(feedService.likePost(postId, user.getId()))
//                .build();
//    }
//
//    @PostMapping("/posts/{postId}/comment")
//    public ApiResponse<Comment> addComment(@PathVariable("postId") Long postId ,@RequestBody CommentRequest req, @RequestAttribute("authenticatedUser") User user) {
//        return ApiResponse.<Comment>builder()
//                .data(feedService.addComment(postId, req, user.getId()))
//                .build();
//    }
//
//    @GetMapping("/posts/{postId}/comments")
//    public ApiResponse<List<Comment>> getComment(@PathVariable("postId") Long postId) {
//        return ApiResponse.<List<Comment>>builder()
//                .data(feedService.getComments(postId))
//                .build();
//    }
//
//    @GetMapping("/posts/{postId}/likes")
//    public ApiResponse<Set<User>> getLikes(@PathVariable("postId") Long postId) {
//        return ApiResponse.<Set<User>>builder()
//                .data(feedService.getPostLike(postId))
//                .build();
//    }
//
//
//    @PutMapping("/posts/{commentId}/comment")
//    public ApiResponse<Comment> updateComment(@PathVariable("commentId") Long commentId ,@RequestBody CommentRequest req, @RequestAttribute("authenticatedUser") User user) {
//        return ApiResponse.<Comment>builder()
//                .data(feedService.updateComment(commentId, req, user.getId()))
//                .build();
//    }
//
//    @DeleteMapping("/posts/{commentId}/comment")
//    public ApiResponse<Void> deleteComment(@PathVariable("commentId") Long commentId, @RequestAttribute("authenticatedUser") User user) {
//        feedService.removeComment(commentId, user.getId());
//        return ApiResponse.<Void>builder()
//                .message("Deleted comment")
//                .build();
//    }
//
    @GetMapping("/post-bgs")
    public ApiResponse<List<PostBackground>> getAllPostBg(){
        return ApiResponse.<List<PostBackground>>builder()
                .data(feedService.getAllPostBg())
                .build();
    }

}
