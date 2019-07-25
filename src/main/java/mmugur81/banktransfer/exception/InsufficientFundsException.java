package mmugur81.banktransfer.exception;

import mmugur81.banktransfer.domain.Account;

public class InsufficientFundsException extends TransferException {

    public InsufficientFundsException(Account account) {
        super("Insufficient funds for account " + account.getIban());
    }
}
