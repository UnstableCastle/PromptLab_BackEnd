package com.promptlab.server.service;

import java.util.List;

import com.promptlab.server.dto.PostRequest;
import com.promptlab.server.dto.PostResponse;

public interface PostService {

    PostResponse createPost(PostRequest request);

    List<PostResponse> getAllPosts();

    void upvotePost(Long postId);
}