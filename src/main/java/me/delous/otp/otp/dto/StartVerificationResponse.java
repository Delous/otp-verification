package me.delous.otp.otp.dto;

import java.time.Instant;
import me.delous.otp.otp.DeliveryRoute;

public record StartVerificationResponse(String businessKey, DeliveryRoute route, Instant validUntil) {
}
