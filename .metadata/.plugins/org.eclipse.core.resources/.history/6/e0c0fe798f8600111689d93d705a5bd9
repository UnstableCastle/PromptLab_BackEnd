package com.promptlab.server.service;

import com.promptlab.server.dto.*;
import com.promptlab.server.entity.User;
import org.springframework.data.domain.Page;

public interface PostService {
    PostResponse createPost(User user, PostRequest request);
    Page<PostResponse> getAllPosts(int page, int size);
}