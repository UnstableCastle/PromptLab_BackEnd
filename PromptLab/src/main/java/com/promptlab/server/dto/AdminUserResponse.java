package com.promptlab.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUserResponse {

    private Long id;

    private String username;

    private String email;

    
    private String profilePicture; 

    private Long postCount;
}