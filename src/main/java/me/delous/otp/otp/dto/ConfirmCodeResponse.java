package me.delous.otp.otp.dto;

public record ConfirmCodeResponse(boolean accepted, String result) {
    public static ConfirmCodeResponse acceptedResult() {
        return new ConfirmCodeResponse(true, "code_accepted");
    }

    public static ConfirmCodeResponse rejectedResult(String result) {
        return new ConfirmCodeResponse(false, result);
    }
}
