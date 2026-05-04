package me.delous.otp.security;

import me.delous.otp.account.AccountRole;
import me.delous.otp.common.Texts;
import org.springframework.stereotype.Component;

@Component
public class RoleGuard {
    public void requireAdmin(AuthenticatedAccount account) {
        if (account == null || account.role() != AccountRole.ADMIN) {
            throw new SecurityException(Texts.ADMIN_REQUIRED);
        }
    }

    public void requireUser(AuthenticatedAccount account) {
        if (account == null || account.role() != AccountRole.USER) {
            throw new SecurityException(Texts.USER_REQUIRED);
        }
    }
}
