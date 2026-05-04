package me.delous.otp.account;

import me.delous.otp.account.dto.AccountView;
import me.delous.otp.account.dto.LoginRequest;
import me.delous.otp.account.dto.SignupRequest;
import me.delous.otp.account.dto.TokenResponse;
import me.delous.otp.common.ApiResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AccountController {
    private final AccountRegistrationService registrationService;
    private final LoginUseCase loginUseCase;

    public AccountController(AccountRegistrationService registrationService, LoginUseCase loginUseCase) {
        this.registrationService = registrationService;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/signup")
    public ApiResult<AccountView> signup(@RequestBody SignupRequest request) {
        return ApiResult.ok(registrationService.signup(request));
    }

    @PostMapping("/token")
    public ApiResult<TokenResponse> token(@RequestBody LoginRequest request) {
        return ApiResult.ok(loginUseCase.issueToken(request));
    }
}
