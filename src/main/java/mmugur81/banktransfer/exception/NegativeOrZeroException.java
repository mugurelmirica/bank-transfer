package mmugur81.banktransfer.exception;

import mmugur81.banktransfer.domain.Account;

public class NegativeOrZeroException extends TransferException {

    public NegativeOrZeroException(Account account) {
        super("Negative or zero transfer from account " + account.getIban());
    }
}
