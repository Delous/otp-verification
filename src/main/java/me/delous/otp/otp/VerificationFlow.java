package me.delous.otp.otp;

import java.time.Clock;
import java.time.Instant;
import me.delous.otp.admin.dto.OtpPolicyView;
import me.delous.otp.delivery.DeliveryRegistry;
import me.delous.otp.otp.dto.ConfirmCodeRequest;
import me.delous.otp.otp.dto.ConfirmCodeResponse;
import me.delous.otp.otp.dto.StartVerificationRequest;
import me.delous.otp.otp.dto.StartVerificationResponse;
import me.delous.otp.security.AuthenticatedAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VerificationFlow {
    private static final Logger log = LoggerFactory.getLogger(VerificationFlow.class);
    private final OtpJdbcRepository otpRepository;
    private final OtpPolicyJdbcRepository policyRepository;
    private final CodeFactory codeFactory;
    private final DeliveryRegistry deliveryRegistry;
    private final Clock clock;

    public VerificationFlow(
            OtpJdbcRepository otpRepository,
            OtpPolicyJdbcRepository policyRepository,
            CodeFactory codeFactory,
            DeliveryRegistry deliveryRegistry,
            Clock clock
    ) {
        this.otpRepository = otpRepository;
        this.policyRepository = policyRepository;
        this.codeFactory = codeFactory;
        this.deliveryRegistry = deliveryRegistry;
        this.clock = clock;
    }

    @Transactional
    public StartVerificationResponse openVerification(AuthenticatedAccount account, StartVerificationRequest request) {
        if (request.businessKey() == null || request.businessKey().isBlank()) {
            throw new IllegalArgumentException("Идентификатор операции не может быть пустым");
        }
        if (request.route() == null) {
            throw new IllegalArgumentException("Канал доставки обязателен");
        }
        if (request.target() == null || request.target().isBlank()) {
            throw new IllegalArgumentException("Получатель кода не может быть пустым");
        }

        OtpPolicyView policy = policyRepository.currentPolicy();
        String code = codeFactory.numericSecret(policy.digitsCount());
        Instant validUntil = clock.instant().plusSeconds(policy.ttlSeconds());
        OtpTicket ticket = otpRepository.storeTicket(new OtpTicket(
                0,
                account.id(),
                request.businessKey().trim(),
                code,
                OtpState.ACTIVE,
                clock.instant(),
                validUntil,
                null
        ));
        deliveryRegistry.sendVia(request.route(), request.target(), code);
        log.info("verification.ticket.opened businessKey={} route={} ticketId={}",
                ticket.businessKey(), request.route(), ticket.ticketId());
        return new StartVerificationResponse(ticket.businessKey(), request.route(), ticket.validUntil());
    }

    @Transactional
    public ConfirmCodeResponse confirmVerification(AuthenticatedAccount account, ConfirmCodeRequest request) {
        OtpTicket ticket = otpRepository.activeTicket(account.id(), request.businessKey())
                .orElseThrow(() -> new IllegalArgumentException("Активный код для операции не найден"));

        if (clock.instant().isAfter(ticket.validUntil())) {
            otpRepository.expire(ticket.ticketId());
            log.info("verification.confirmation.rejected reason=code_expired businessKey={}", request.businessKey());
            return ConfirmCodeResponse.rejectedResult("code_expired");
        }

        if (!ticket.secretCode().equals(request.submittedCode())) {
            log.info("verification.confirmation.rejected reason=code_mismatch businessKey={}", request.businessKey());
            return ConfirmCodeResponse.rejectedResult("code_mismatch");
        }

        otpRepository.consume(ticket.ticketId());
        log.info("verification.confirmation.accepted businessKey={}", request.businessKey());
        return ConfirmCodeResponse.acceptedResult();
    }
}
