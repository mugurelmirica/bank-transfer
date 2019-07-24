package mmugur81.banktransfer.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.Immutable;

import javax.persistence.Column;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

@Immutable
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
    private final BigDecimal sourceAmount;

    /**
     * Final converted amount that will be added to target account
     */
    @Column(nullable = false)
    private final BigDecimal targetAmount;

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
}
