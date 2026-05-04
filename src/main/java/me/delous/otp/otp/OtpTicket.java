package me.delous.otp.otp;

import java.time.Instant;

public record OtpTicket(
        long ticketId,
        long accountId,
        String businessKey,
        String secretCode,
        OtpState codeState,
        Instant issuedAt,
        Instant validUntil,
        Instant consumedAt
) {
}
