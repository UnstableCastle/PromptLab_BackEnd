package com.promptlab.server.dto;

import java.time.LocalDateTime;

// Inside com.promptlab.server.dto.PostResponse (or similar package)

public record PostResponse(
	    Long id, 
	    String title, 
	    String promptText, 
	    String modelInfo, 
	    String attachmentUrl, 
	    Integer upvoteCount, 
	    Boolean isExplore, 
	    Long userId,            // Added here
	    String authorUsername,  // Swapped order
	    String status,          // Swapped order
	    LocalDateTime createdAt
	) {} 