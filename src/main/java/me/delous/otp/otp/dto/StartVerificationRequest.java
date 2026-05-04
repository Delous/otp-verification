package me.delous.otp.otp.dto;

import me.delous.otp.otp.DeliveryRoute;

public record StartVerificationRequest(String businessKey, DeliveryRoute route, String target) {
}
