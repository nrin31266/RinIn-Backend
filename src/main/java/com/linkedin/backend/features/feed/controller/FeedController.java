package com.linkedin.backend.features.feed.controller;

import com.linkedin.backend.features.feed.service.FeedServiceImpl;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeedController {
    FeedServiceImpl feedService;

//    @PostMapping("/posts")
//    public ApiResponse<Post> createPost(@RequestBody PostRequest postRequest, @RequestAttribute("authenticatedUser") User user) {
//        return ApiResponse.<Post>builder()
//                .data(feedService.createPost(postRequest, user.getId()))
//                .build();
//    }
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
//    @GetMapping
//    public ApiResponse<List<Post>> getFeedPost(@RequestAttribute("authenticatedUser") User user) {
//        return ApiResponse.<List<Post>>builder()
//                .data(feedService.getFeedPosts(user.getId()))
//                .build();
//    }
//
//    @GetMapping("/posts/user/{userId}")
//    public ApiResponse<List<Post>> getPostsByUserId(@PathVariable Long userId) {
//        List<Post> posts = feedService.getPostsByUserId(userId);
//        return ApiResponse.<List<Post>>builder()
//                .data(posts)
//                .build();
//    }
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
//    @GetMapping("/post-bgs")
//    public ApiResponse<List<PostBackground>> getAllPostBg(){
//        return ApiResponse.<List<PostBackground>>builder()
//                .data(feedService.getAllPostBg())
//                .build();
//    }

}
