package mmugur81.banktransfer.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import mmugur81.banktransfer.exception.TransferAlreadyProcessedException;

import javax.persistence.Column;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

@Getter
@RequiredArgsConstructor
public class Transfer extends BaseEntity {

    @OneToOne
    private final Account source;

    @OneToOne
    private final Account target;

    /**
     * Currency of the transfer; It can be different from both source and target accounts
     */
    @Column(nullable = false)
    private final Currency currency;

    /**
     * Amount in transfer's currency
     */
    @Column(nullable = false)
    private final BigDecimal amount;

    /**
     * Final converted amount that will be withdrawn from source account
     */
    @Column(nullable = false)
    private final BigDecimal amountInSourceCurrency;

    /**
     * Final converted amount that will be added to target account
     */
    @Column(nullable = false)
    private final BigDecimal amountInTargetCurrency;

    private boolean processed = false;

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
     * Actual processing of the transfer
     */
    public void process() throws TransferAlreadyProcessedException {
        if (processed) {
            throw new TransferAlreadyProcessedException(getId());
        }

        source.withdraw(amountInSourceCurrency);
        target.deposit(amountInTargetCurrency);
        processed = true;
    }
}
