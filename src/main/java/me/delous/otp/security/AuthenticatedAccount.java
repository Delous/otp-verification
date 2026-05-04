package me.delous.otp.security;

import me.delous.otp.account.AccountRole;

public record AuthenticatedAccount(long id, String username, AccountRole role) {
}
