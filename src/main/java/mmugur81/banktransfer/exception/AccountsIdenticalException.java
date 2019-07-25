package mmugur81.banktransfer.exception;

import mmugur81.banktransfer.domain.Account;

public class AccountsIdenticalException extends TransferException {

    public AccountsIdenticalException(Account account) {
        super("Cannot transfer between same account (" + account.getIban() + ")");
    }
}
