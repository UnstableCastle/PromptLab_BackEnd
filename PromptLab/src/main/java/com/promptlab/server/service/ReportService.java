package com.promptlab.server.service;

import com.promptlab.server.entity.User;

public interface ReportService {
    void reportPost(User reporter, Long postId, String reasonString);
}