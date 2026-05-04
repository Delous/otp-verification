package me.delous.otp.account;

import java.time.Instant;

public record Account(
        long accountId,
        String username,
        String passwordDigest,
        AccountRole accountRole,
        Instant registeredAt
) {
}
