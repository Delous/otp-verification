package me.delous.otp.admin;

import java.util.List;
import me.delous.otp.account.dto.AccountView;
import me.delous.otp.admin.dto.OtpPolicyRequest;
import me.delous.otp.admin.dto.OtpPolicyView;
import me.delous.otp.common.ApiResult;
import me.delous.otp.security.AuthenticatedAccount;
import me.delous.otp.security.BearerTokenFilter;
import me.delous.otp.security.RoleGuard;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management")
public class AdminController {
    private final OtpPolicyService otpPolicyService;
    private final AccountManagementService accountManagementService;
    private final RoleGuard roleGuard;

    public AdminController(
            OtpPolicyService otpPolicyService,
            AccountManagementService accountManagementService,
            RoleGuard roleGuard
    ) {
        this.otpPolicyService = otpPolicyService;
        this.accountManagementService = accountManagementService;
        this.roleGuard = roleGuard;
    }

    @PatchMapping("/otp-policy")
    public ApiResult<OtpPolicyView> updatePolicy(
            @RequestAttribute(BearerTokenFilter.AUTH_ATTR) AuthenticatedAccount account,
            @RequestBody OtpPolicyRequest request
    ) {
        roleGuard.requireAdmin(account);
        return ApiResult.ok(otpPolicyService.refreshPolicy(request));
    }

    @GetMapping("/accounts")
    public ApiResult<List<AccountView>> accounts(
            @RequestAttribute(BearerTokenFilter.AUTH_ATTR) AuthenticatedAccount account
    ) {
        roleGuard.requireAdmin(account);
        return ApiResult.ok(accountManagementService.listClientAccounts());
    }

    @DeleteMapping("/accounts/{accountId}")
    public ApiResult<Void> deleteAccount(
            @RequestAttribute(BearerTokenFilter.AUTH_ATTR) AuthenticatedAccount account,
            @PathVariable long accountId
    ) {
        roleGuard.requireAdmin(account);
        accountManagementService.removeAccountCascade(accountId);
        return ApiResult.ok(null);
    }
}
