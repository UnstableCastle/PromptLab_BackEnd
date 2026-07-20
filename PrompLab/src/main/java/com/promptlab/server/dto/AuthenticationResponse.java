package com.promptlab.server.dto;

public record AuthenticationResponse(
	    String token,
	    String tokenType,
	    String username,
	    String role
	) {}		