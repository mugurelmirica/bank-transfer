package mmugur81.banktransfer.exception;

import mmugur81.banktransfer.domain.Account;

public class NotAllowedToDepositException extends TransferException {

    public NotAllowedToDepositException(Account account) {
        super("Account " + account.getIban() + " not allowed to deposit");
    }
}
