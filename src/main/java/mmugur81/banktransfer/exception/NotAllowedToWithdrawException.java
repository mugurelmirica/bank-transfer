package mmugur81.banktransfer.exception;

import mmugur81.banktransfer.domain.Account;

public class NotAllowedToWithdrawException extends TransferException {

    public NotAllowedToWithdrawException(Account account) {
        super("Account " + account.getIban() + " not allowed to withdraw");
    }
}
