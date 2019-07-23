package mmugur81.banktransfer.domain;

import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

@Entity
@Getter
public class Account extends BaseEntity {

    @OneToOne
    private Holder holder;

    private String iban;

    private Currency currency;

    private BigDecimal amount;

    public void setHolder(Holder holder) {
        this.holder = holder;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Account)) return false;
        Account that = (Account) o;
        return getHolder().equals(that.getHolder()) &&
                getIban().equals(that.getIban()) &&
                getCurrency().equals(that.getCurrency());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHolder(), getIban(), getCurrency());
    }
}
