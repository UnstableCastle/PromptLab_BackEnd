package com.promptlab.server.service;

import com.promptlab.server.entity.Report.ReportReason;

public interface ReportService {

    void reportPost(Long postId, ReportReason reason);
}