package me.delous.otp.account.dto;

import java.time.Instant;
import me.delous.otp.account.AccountRole;

public record AccountView(long accountId, String username, AccountRole accountRole, Instant registeredAt) {
}
