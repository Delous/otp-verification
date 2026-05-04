package me.delous.otp.admin;

import me.delous.otp.admin.dto.OtpPolicyRequest;
import me.delous.otp.admin.dto.OtpPolicyView;
import me.delous.otp.otp.OtpPolicyJdbcRepository;
import org.springframework.stereotype.Service;

@Service
public class OtpPolicyService {
    private final OtpPolicyJdbcRepository policies;

    public OtpPolicyService(OtpPolicyJdbcRepository policies) {
        this.policies = policies;
    }

    public OtpPolicyView refreshPolicy(OtpPolicyRequest request) {
        if (request.digitsCount() < 4 || request.digitsCount() > 12) {
            throw new IllegalArgumentException("Количество цифр должно быть от 4 до 12");
        }
        if (request.ttlSeconds() < 30 || request.ttlSeconds() > 86_400) {
            throw new IllegalArgumentException("Время жизни кода должно быть от 30 до 86400 секунд");
        }
        return policies.refreshPolicy(request.digitsCount(), request.ttlSeconds());
    }
}
