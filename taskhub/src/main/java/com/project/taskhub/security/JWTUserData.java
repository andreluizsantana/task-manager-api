package com.project.taskhub.security;

import lombok.Builder;

@Builder
public record JWTUserData(Long userId, String email) {

}
