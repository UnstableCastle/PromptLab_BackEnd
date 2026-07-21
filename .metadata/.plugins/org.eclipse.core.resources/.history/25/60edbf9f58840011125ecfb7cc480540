package com.promptlab.server.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "follows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Follow {

	public enum FollowStatus{PENDING , ACCEPTED,DECLINED}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="follow_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name="follower_id", nullable = false)
	private User followers;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name="following_id", nullable = false)
	private User following;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	@Builder.Default
	private FollowStatus followStatus = FollowStatus.PENDING;
	
	@CreationTimestamp
	@Column(name="created_at", updatable = false)
	private LocalDateTime createdAt;
	
	
	
}
