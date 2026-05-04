package me.delous.otp.account.dto;

import java.time.Instant;

public record TokenResponse(String tokenType, String accessToken, Instant expiresAt) {
}
