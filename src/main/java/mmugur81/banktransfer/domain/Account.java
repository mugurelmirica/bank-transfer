package mmugur81.banktransfer.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;
import java.util.Set;

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
    private boolean withdrawAllowed = true;

    @JsonIgnore
    private boolean depositAllowed = true;

    @JsonIgnore
    @OneToMany(mappedBy = "source", fetch = FetchType.EAGER)
    private Set<Transfer> withdrawTransfers;

    @JsonIgnore
    @OneToMany(mappedBy = "target", fetch = FetchType.EAGER)
    private Set<Transfer> depositTransfers;

    public Account() {
    }

    public Account(Holder holder, String iban, Currency currency, BigDecimal initialAmount) {
        this.holder = holder;
        this.iban = iban;
        this.currency = currency;
        this.amount = initialAmount.round(new MathContext(2, RoundingMode.HALF_DOWN));
    }

    public void setWithdrawAllowed(boolean withdrawalAllowed) {
        this.withdrawAllowed = withdrawalAllowed;
    }

    public void setDepositAllowed(boolean depositAllowed) {
        this.depositAllowed = depositAllowed;
    }

    public void withdraw(BigDecimal amountToWithdraw) {
        // Maybe i should check if operation possible
        amount = amount.subtract(amountToWithdraw);
    }

    public void deposit(BigDecimal amountToDeposit) {
        // Maybe i should check if operation possible
        amount = amount.add(amountToDeposit);
    }

    public BigDecimal getAmount() {
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
