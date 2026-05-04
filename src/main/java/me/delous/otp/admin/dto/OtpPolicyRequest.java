package me.delous.otp.admin.dto;

public record OtpPolicyRequest(int digitsCount, int ttlSeconds) {
}
