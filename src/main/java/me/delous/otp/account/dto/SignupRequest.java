package me.delous.otp.account.dto;

import me.delous.otp.account.AccountRole;

public record SignupRequest(String username, String rawPassword, AccountRole requestedRole) {
}
