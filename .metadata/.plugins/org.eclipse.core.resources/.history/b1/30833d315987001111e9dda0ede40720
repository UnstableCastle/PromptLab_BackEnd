package com.promptlab.server.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.promptlab.server.entity.Post;
import com.promptlab.server.entity.Report;
import com.promptlab.server.entity.User;
import com.promptlab.server.repository.PostRepository;
import com.promptlab.server.repository.ReportRepository;
import com.promptlab.server.service.ReportService;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final PostRepository postRepository;

    @Override
    public void reportPost(User reporter, Long postId, String reasonString) {
        
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with ID: " + postId));

        // 1. Verify the user hasn't already reported this post (enforces your UniqueConstraint)
        if (reportRepository.existsByReporterAndPost(reporter, post)) {
            throw new RuntimeException("You have already reported this post.");
        }

        // 2. Safely convert the incoming String to your ReportReason Enum
        Report.ReportReason reasonEnum;
        try {
            reasonEnum = Report.ReportReason.valueOf(reasonString.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid report reason. Allowed values: SPAM, OFFENSIVE, NSFW, COPYRIGHT, MISLEADING, OTHER.");
        }

        // 3. Build and save the report using your Lombok Builder
        Report report = Report.builder()
                .reporter(reporter)
                .post(post)
                .reason(reasonEnum)
                // Note: status is automatically set to OPEN because of your @Builder.Default
                .build();

        reportRepository.save(report);
    }
}