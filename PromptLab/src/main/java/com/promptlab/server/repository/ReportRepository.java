package com.promptlab.server.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.promptlab.server.entity.Post;
import com.promptlab.server.entity.Report;
import com.promptlab.server.entity.Report.ReportStatus;
import com.promptlab.server.entity.User;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReporterAndPost(User reporter, Post post);

    Optional<Report> findByReporterAndPost(User reporter, Post post);

    @EntityGraph(attributePaths = {"reporter", "post"})
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"reporter", "post"})
    Page<Report> findByPost(Post post, Pageable pageable);

    @EntityGraph(attributePaths = {"reporter", "post"})
    Page<Report> findByReporter(User reporter, Pageable pageable);

    long countByPost(Post post);

}