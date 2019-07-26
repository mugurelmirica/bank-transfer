package mmugur81.banktransfer.service;

import mmugur81.banktransfer.domain.Account;
import mmugur81.banktransfer.domain.Transfer;
import mmugur81.banktransfer.dto.TransferDto;
import mmugur81.banktransfer.exception.TransferException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

import static mmugur81.banktransfer.TestUtils.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class TransferServiceImplTest {

    @Mock
    private CRUDServiceImpl<Transfer> transferCRUDService;

    @Mock
    private AccountService accountService;

    @Mock
    private CurrencyConverter currencyConverter;

    @InjectMocks
    private TransferServiceImpl transferService;

    private Account source;
    private Account target;
    private BigDecimal a50 = BigDecimal.valueOf(50);

    @BeforeEach
    private void setUp() {
        MockitoAnnotations.initMocks(this);

        source = newAccount(1, IBAN1, EURO, no100);
        target = newAccount(2, IBAN2, DOLLAR, no100);
        when(accountService.get(eq(source.getId()))).thenReturn(Optional.of(source));
        when(accountService.get(eq(target.getId()))).thenReturn(Optional.of(target));

        // Setup currency converter to return same amount
        when(currencyConverter.convert(any(), any(), any())).thenAnswer(
                (Answer<BigDecimal>) invocation -> {
                    Object[] args = invocation.getArguments();
                    return (BigDecimal) args[2];
                });

        when(transferCRUDService.create(any())).thenAnswer(
                (Answer<Transfer>) invocation -> {
                    Object[] args = invocation.getArguments();
                    return (Transfer) args[0];
                });
    }

    @Test
    public void create() throws TransferException {
        TransferDto dto = newTransferDto(source.getId(), target.getId(), POUNDS, a50);
        Transfer expected = new Transfer(source, target, dto.getCurrency(), dto.getAmount(), a50, a50);

        Transfer actual = transferService.create(dto);

        // Do verifications
        verify(accountService, times(1)).get(eq(dto.getSourceAccountId()));
        verify(accountService, times(1)).get(eq(dto.getTargetAccountId()));
        verify(currencyConverter, times(1)).convert(eq(dto.getCurrency()), eq(target.getCurrency()), eq(a50));
        verify(currencyConverter, times(1)).convert(eq(dto.getCurrency()), eq(target.getCurrency()), eq(a50));
        verify(transferCRUDService, times(1)).create(eq(expected));

        // Assert expectations
        assertEquals(expected, actual);
    }

    @Test
    public void process() throws TransferException {
        TransferDto dto = newTransferDto(source.getId(), target.getId(), POUNDS, a50);
        Transfer expected = mockTransfer(source, target, dto.getCurrency(), dto.getAmount(), a50, a50);

        transferService.process(expected);

        verify(expected, times(1)).verify();
        verify(expected, times(1)).process();
        verify(transferCRUDService, times(1)).update(eq(expected));
    }


    private Transfer mockTransfer(Account source, Account target, Currency currency, BigDecimal amount,
                                  BigDecimal amountInSourceCurrency, BigDecimal amountInTargetCurrency) {

        Transfer transfer = mock(Transfer.class);

        when(transfer.getSource()).thenReturn(source);
        when(transfer.getTarget()).thenReturn(target);
        when(transfer.getCurrency()).thenReturn(currency);
        when(transfer.getAmount()).thenReturn(amount);
        when(transfer.getAmountInSourceCurrency()).thenReturn(amountInSourceCurrency);
        when(transfer.getAmountInTargetCurrency()).thenReturn(amountInTargetCurrency);
        when(transfer.getId()).thenReturn(0L);

        return transfer;
    }
}