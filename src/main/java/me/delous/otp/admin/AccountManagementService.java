package me.delous.otp.admin;

import java.util.List;
import me.delous.otp.account.AccountJdbcRepository;
import me.delous.otp.account.dto.AccountView;
import org.springframework.stereotype.Service;

@Service
public class AccountManagementService {
    private final AccountJdbcRepository accounts;

    public AccountManagementService(AccountJdbcRepository accounts) {
        this.accounts = accounts;
    }

    public List<AccountView> listClientAccounts() {
        return accounts.scanNonAdminAccounts();
    }

    public void removeAccountCascade(long accountId) {
        accounts.eraseAccount(accountId);
    }
}
