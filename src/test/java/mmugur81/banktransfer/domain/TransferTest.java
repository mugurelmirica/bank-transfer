package mmugur81.banktransfer.domain;

import mmugur81.banktransfer.exception.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TransferTest {

    private static final Currency EURO = Currency.getInstance("EUR");
    private static final Currency DOLLAR = Currency.getInstance("USD");

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private static final BigDecimal no100 = BigDecimal.valueOf(100);

    private static final String IBAN1 = "GB11BARC20035347275952";
    private static final String IBAN2 = "GB16BARC20038073137852";

    @Test
    public void verifyFailsIfAccountsIdentical() {
        Account source = newAccount(1, IBAN1, EURO, no100);
        Account target = newAccount(1, IBAN1, EURO, no100);

        Transfer transfer = new Transfer(source, target, EURO, no100, no100, no100);
        assertThrows(AccountsIdenticalException.class, transfer::verify);
    }

    @Test
    public void verifyFailsIfSourceAccountNotAllowedToWithdraw() {
        Account source = newAccount(1, IBAN1, EURO, no100);
        source.setWithdrawAllowed(false);
        Account target = newAccount(2, IBAN2, EURO, no100);

        Transfer transfer = new Transfer(source, target, EURO, no100, no100, no100);
        assertThrows(NotAllowedToWithdrawException.class, transfer::verify);
    }

    @Test
    public void verifyFailsIfTargetAccountNotAllowedToDeposit() {
        Account source = newAccount(1, IBAN1, EURO, no100);
        Account target = newAccount(2, IBAN2, EURO, no100);
        target.setDepositAllowed(false);

        Transfer transfer = new Transfer(source, target, EURO, no100, no100, no100);
        assertThrows(NotAllowedToDepositException.class, transfer::verify);
    }

    @Test
    public void verifyFailsIfAmountNegativeOrZero() {
        Account source = newAccount(1, IBAN1, EURO, no100);
        Account target = newAccount(2, IBAN2, EURO, no100);

        Transfer transfer = new Transfer(source, target, EURO, ZERO, ZERO, ZERO);
        assertThrows(NegativeOrZeroException.class, transfer::verify);
    }

    @Test
    public void verifyFailsIfInsufficientFunds() {
        Account source = newAccount(1, IBAN1, EURO, no100);
        Account target = newAccount(2, IBAN2, EURO, no100);

        BigDecimal amount = source.getAmount().add(BigDecimal.ONE);
        Transfer transfer = new Transfer(source, target, EURO, amount, amount, amount);
        assertThrows(InsufficientFundsException.class, transfer::verify);
    }

    @Test
    public void verifySucceedsIfAmountJustRight() throws TransferException {
        Account source = newAccount(1, IBAN1, EURO, no100);
        Account target = newAccount(2, IBAN2, DOLLAR, no100);

        Transfer transfer = new Transfer(source, target, EURO, no100, no100, no100);
        transfer.verify();
    }

    @Test
    public void processSuccessful() throws TransferAlreadyProcessedException {
        Account source = newAccount(1, IBAN1, EURO, no100);
        Account target = newAccount(2, IBAN2, EURO, no100);

        BigDecimal amount = BigDecimal.valueOf(50);
        Transfer transfer = new Transfer(source, target, EURO, amount, amount, amount);

        // Setup expectations
        BigDecimal expectedSourceAmount = source.getAmount().subtract(amount);
        BigDecimal expectedTargetAmount = target.getAmount().add(amount);

        // Successful processing
        transfer.process();

        assertEquals(expectedSourceAmount, source.getAmount());
        assertEquals(expectedTargetAmount, target.getAmount());
    }

    @Test
    public void processFailsIfAlreadyProcessed() throws TransferAlreadyProcessedException {
        Account source = newAccount(1, IBAN1, EURO, no100);
        Account target = newAccount(2, IBAN2, EURO, no100);

        Transfer transfer = new Transfer(source, target, EURO, no100, no100, no100);
        // Successful processing
        transfer.process();

        // Failed processing
        assertThrows(TransferAlreadyProcessedException.class, transfer::process);
    }

    private Account newAccount(long id, String iban, Currency currency, BigDecimal initialAmount) {
        Account account = new Account(newHolder(), iban, currency, initialAmount);
        account.setId(id);

        return account;
    }

    private Holder newHolder() {
        Holder holder = new Holder();
        holder.setName("TEST");

        return holder;
    }
}