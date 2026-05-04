package me.delous.otp.delivery;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import me.delous.otp.otp.DeliveryRoute;
import org.springframework.stereotype.Component;

@Component
public class DeliveryRegistry {
    private final Map<DeliveryRoute, DeliveryGateway> gateways;

    public DeliveryRegistry(List<DeliveryGateway> gatewayList) {
        this.gateways = gatewayList.stream()
                .collect(Collectors.toMap(
                        DeliveryGateway::route,
                        Function.identity(),
                        (left, right) -> left,
                        () -> new EnumMap<>(DeliveryRoute.class)
                ));
    }

    public void sendVia(DeliveryRoute route, String target, String code) {
        DeliveryGateway gateway = gateways.get(route);
        if (gateway == null) {
            throw new IllegalArgumentException("Неподдерживаемый канал доставки");
        }
        gateway.deliver(target, code);
    }
}
