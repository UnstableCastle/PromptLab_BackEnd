package com.promptlab.server.dto;

public record UserProfileResponse(

    String username,

    String bio,

    String profilePicture,

    boolean isPrivate,

    Long followerCount,

    Long followingCount,

    Long postCount,

    boolean followedByCurrentUser

) {
}