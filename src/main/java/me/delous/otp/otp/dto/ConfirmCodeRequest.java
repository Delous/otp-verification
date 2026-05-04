package me.delous.otp.otp.dto;

public record ConfirmCodeRequest(String businessKey, String submittedCode) {
}
