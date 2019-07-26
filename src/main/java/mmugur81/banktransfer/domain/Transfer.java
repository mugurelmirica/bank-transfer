package mmugur81.banktransfer.domain;

import lombok.Getter;
import lombok.extern.java.Log;
import mmugur81.banktransfer.exception.AccountsIdenticalException;
import mmugur81.banktransfer.exception.InsufficientFundsException;
import mmugur81.banktransfer.exception.NegativeOrZeroException;
import mmugur81.banktransfer.exception.NotAllowedToDepositException;
import mmugur81.banktransfer.exception.NotAllowedToWithdrawException;
import mmugur81.banktransfer.exception.TransferAlreadyProcessedException;
import mmugur81.banktransfer.exception.TransferException;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

@Log
@Getter
@Entity
public class Transfer extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "source_account_id")
    private Account source;

    @ManyToOne
    @JoinColumn(name = "target_account_id")
    private Account target;

    /**
     * Currency of the transfer; It can be different from both source and target accounts
     */
    @Column(nullable = false)
    private Currency currency;

    /**
     * Amount in transfer's currency
     */
    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * Final converted amount that will be withdrawn from source account
     */
    @Column(nullable = false)
    private BigDecimal amountInSourceCurrency;

    /**
     * Final converted amount that will be added to target account
     */
    @Column(nullable = false)
    private BigDecimal amountInTargetCurrency;

    private boolean processed = false;

    public Transfer() {
    }

    public Transfer(Account source, Account target, Currency currency, BigDecimal amount,
                    BigDecimal amountInSourceCurrency, BigDecimal amountInTargetCurrency) {
        this.source = source;
        this.target = target;
        this.currency = currency;
        this.amount = amount;
        this.amountInSourceCurrency = amountInSourceCurrency;
        this.amountInTargetCurrency = amountInTargetCurrency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Transfer)) return false;
        if (!super.equals(o)) return false;
        Transfer transfer = (Transfer) o;
        return getSource().equals(transfer.getSource()) &&
                getTarget().equals(transfer.getTarget()) &&
                getCurrency().equals(transfer.getCurrency()) &&
                getAmount().equals(transfer.getAmount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSource(), getTarget(), getCurrency(), getAmount());
    }

    /**
     * Verify transfer possible
     */
    public void verify() throws TransferException {
        myLog("verifying ", source.getId(), target.getId());

        // Lock both accounts
        synchronized (source) {
            synchronized (target) {
                myLog("into lock ***************", source.getId(), target.getId());

                // TODO after DEBUG remove
                /*try {
                    Thread.sleep(target.getId() == 2 ? 5000 : 100);
                } catch (InterruptedException ignored) {
                    ignored.printStackTrace();
                }*/

                // [1] Check accounts differ
                if (source.equals(target)) {
                    throw new AccountsIdenticalException(source);
                }

                // [2] check source account has the right to withdraw
                if (!source.isWithdrawAllowed()) {
                    throw new NotAllowedToWithdrawException(source);
                }

                // [3] check target account has the right to deposit
                if (!target.isDepositAllowed()) {
                    throw new NotAllowedToDepositException(source);
                }

                // [4] check the amount is positive
                if (amountInSourceCurrency.compareTo(BigDecimal.ZERO) <= 0) {
                    throw new NegativeOrZeroException(source);
                }

                // [5] check source account has enough money
                if (source.getAmount().compareTo(amountInSourceCurrency) < 0) {
                    throw new InsufficientFundsException(source);
                }
            }
        }
        myLog("exit lock", source.getId(), target.getId());
    }

    /**
     * Actual processing of the transfer
     */
    public void process() throws TransferAlreadyProcessedException {
        myLog("processing ", source.getId(), target.getId());
        if (processed) {
            throw new TransferAlreadyProcessedException(getId());
        }

        // Lock both accounts
        synchronized (source) {
            synchronized (target) {
                myLog("into lock ***************", source.getId(), target.getId());

                source.withdraw(amountInSourceCurrency);
                target.deposit(amountInTargetCurrency);
                processed = true;

                myLog("exit lock ***************", source.getId(), target.getId());
            }
        }
    }

    private void myLog(String message, long sourceId, long targetId) {
//        log.info(String.format("[Pid:%s] Transfer [%s -> %s] ",
//                Thread.currentThread().getId(), sourceId, targetId).concat(message));
    }
}
