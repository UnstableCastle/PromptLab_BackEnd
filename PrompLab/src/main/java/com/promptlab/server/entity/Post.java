package com.promptlab.server.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
    name = "posts",
    indexes = {
        @Index(name = "idx_posts_user", columnList = "user_id"),
        @Index(name = "idx_posts_created_at", columnList = "created_at"),
        @Index(name = "idx_posts_explore", columnList = "is_explore")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = "user")
public class Post implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank
    @Size(max = 150)
    @Column(nullable = false, length = 150)
    private String title;

    @NotBlank
    @Lob
    @Column(name = "prompt_text", nullable = false)
    private String promptText;

    @Size(max = 100)
    @Column(name = "model_info", length = 100)
    private String modelInfo;

    @Size(max = 512)
    @Column(name = "attachment_url", length = 512)
    private String attachmentUrl;

    @Builder.Default
    @Min(0)
    @Column(name = "upvote_count", nullable = false)
    private Integer upvoteCount = 0;

    @Builder.Default
    @Column(name = "is_explore", nullable = false)
    private boolean isExplore = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}