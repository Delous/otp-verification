package me.delous.otp.otp;

import me.delous.otp.common.ApiResult;
import me.delous.otp.otp.dto.ConfirmCodeRequest;
import me.delous.otp.otp.dto.ConfirmCodeResponse;
import me.delous.otp.otp.dto.StartVerificationRequest;
import me.delous.otp.otp.dto.StartVerificationResponse;
import me.delous.otp.security.AuthenticatedAccount;
import me.delous.otp.security.BearerTokenFilter;
import me.delous.otp.security.RoleGuard;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/verifications")
public class VerificationController {
    private final VerificationFlow verificationFlow;
    private final RoleGuard roleGuard;

    public VerificationController(VerificationFlow verificationFlow, RoleGuard roleGuard) {
        this.verificationFlow = verificationFlow;
        this.roleGuard = roleGuard;
    }

    @PostMapping
    public ApiResult<StartVerificationResponse> open(
            @RequestAttribute(BearerTokenFilter.AUTH_ATTR) AuthenticatedAccount account,
            @RequestBody StartVerificationRequest request
    ) {
        roleGuard.requireUser(account);
        return ApiResult.ok(verificationFlow.openVerification(account, request));
    }

    @PostMapping("/confirm")
    public ApiResult<ConfirmCodeResponse> confirm(
            @RequestAttribute(BearerTokenFilter.AUTH_ATTR) AuthenticatedAccount account,
            @RequestBody ConfirmCodeRequest request
    ) {
        roleGuard.requireUser(account);
        return ApiResult.ok(verificationFlow.confirmVerification(account, request));
    }
}
