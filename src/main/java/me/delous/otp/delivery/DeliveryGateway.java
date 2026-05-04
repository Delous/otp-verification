package me.delous.otp.delivery;

import me.delous.otp.otp.DeliveryRoute;

public interface DeliveryGateway {
    DeliveryRoute route();

    void deliver(String target, String code);
}
