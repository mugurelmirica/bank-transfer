package mmugur81.banktransfer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Objects;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"iban", "currency"})})
@Getter
public class Account extends BaseEntity {

    @OneToOne
    private Holder holder;

    @Column(nullable = false)
    private String iban;

    @Column(nullable = false)
    private Currency currency;

    // Amount cannot be set directly
    private BigDecimal amount = new BigDecimal(0);

    @JsonIgnore
    private boolean withdrawAllowed;

    @JsonIgnore
    private boolean depositAllowed;

    public void setHolder(Holder holder) {
        this.holder = holder;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setWithdrawAllowed(boolean withdrawalAllowed) {
        this.withdrawAllowed = withdrawalAllowed;
    }

    public void setDepositAllowed(boolean depositAllowed) {
        this.depositAllowed = depositAllowed;
    }

    public synchronized void withdraw(BigDecimal amountToWithdraw) {
        // Maybe i should check if operation possible
        amount = amount.subtract(amountToWithdraw);
    }

    public synchronized void deposit(BigDecimal amountToDeposit) {
        // Maybe i should check if operation possible
        amount = amount.add(amountToDeposit);
    }

    public synchronized BigDecimal getAmount() {
        return amount;
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
