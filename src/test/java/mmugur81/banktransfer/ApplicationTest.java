package mmugur81.banktransfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import mmugur81.banktransfer.controller.AccountController;
import mmugur81.banktransfer.controller.HolderController;
import mmugur81.banktransfer.controller.TransferController;
import mmugur81.banktransfer.domain.Account;
import mmugur81.banktransfer.domain.Holder;
import mmugur81.banktransfer.domain.Transfer;
import mmugur81.banktransfer.dto.AccountDto;
import mmugur81.banktransfer.dto.TransferDto;
import mmugur81.banktransfer.dto.TransferInfoDto;
import mmugur81.banktransfer.dto.TransferType;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static mmugur81.banktransfer.TestUtils.DOLLAR;
import static mmugur81.banktransfer.TestUtils.EURO;
import static mmugur81.banktransfer.TestUtils.POUNDS;
import static mmugur81.banktransfer.TestUtils.newHolder;
import static mmugur81.banktransfer.dto.TransferType.DEPOSIT;
import static mmugur81.banktransfer.dto.TransferType.WITHDRAWAL;
import static mmugur81.banktransfer.service.CurrencyConverterImpl.ROUND_DECIMALS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * API endpoints test
 */
class ApplicationTest {

    private static HttpClient httpClient;
    private static ObjectMapper objectMapper;

    private static Integer port = 7000;
    private static String serverUrl = "http://localhost:" + port;

    private static long firstHolderId;
    private static long firstAccountId;

    private static long secondHolderId;
    private static long secondAccountId;

    @BeforeAll
    public static void setApp() throws Exception {
        String[] args = {port.toString()};
        Application.main(args);

        httpClient = new HttpClient();
        httpClient.start();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Setup some data
        firstHolderId = postHolder("First Holder");
        secondHolderId = postHolder("Second Holder");

        // First Account
        AccountDto dto1 = new AccountDto();
        dto1.setHolderId(firstHolderId);
        dto1.setIban("IBAN 1");
        dto1.setCurrency(EURO);
        dto1.setInitialAmount(newBigDecimal(1000));
        firstAccountId = postAccount(dto1);

        // Second Account
        AccountDto dto2 = new AccountDto();
        dto2.setHolderId(secondHolderId);
        dto2.setIban("IBAN 2");
        dto2.setCurrency(DOLLAR);
        dto2.setInitialAmount(newBigDecimal(500));
        secondAccountId = postAccount(dto2);
    }

    private static long postHolder(String name) throws Exception {
        ContentResponse response = post(serverUrl + HolderController.PATH, newHolder(name));
        String content = response.getContentAsString();
        Holder actual = objectMapper.readValue(content, Holder.class);
        return actual.getId();
    }

    private static Holder getHolder(long id) throws Exception {
        ContentResponse response = httpClient.GET(serverUrl + HolderController.PATH + "/" + id);
        String content = response.getContentAsString();
        return objectMapper.readValue(content, Holder.class);
    }

    private static ContentResponse post(String uri, Object payload) throws Exception {
        ContentProvider contentProvider = new StringContentProvider(objectMapper.writeValueAsString(payload));
        return httpClient.POST(uri).content(contentProvider).send();
    }

    private static BigDecimal newBigDecimal(double number) {
        return BigDecimal.valueOf(number).setScale(ROUND_DECIMALS, RoundingMode.HALF_UP);
    }

    private static long postAccount(AccountDto dto) throws Exception {
        ContentResponse response = post(serverUrl + AccountController.PATH, dto);
        String content = response.getContentAsString();
        Account actual = objectMapper.readValue(content, Account.class);
        return actual.getId();
    }

    @Test
    public void postHolderTest() throws Exception {
        String holderName = "New holder";

        long id = postHolder(holderName);

        Holder actual = getHolder(id);

        assertEquals(holderName, actual.getName());
        assertTrue(actual.getId() > 0);
    }

    @Test
    public void postAccountTest() throws Exception {
        AccountDto dto = new AccountDto();
        dto.setHolderId(firstHolderId);
        dto.setIban("IBAN 3");
        dto.setCurrency(EURO);
        dto.setInitialAmount(newBigDecimal(1000));

        long id = postAccount(dto);
        Account actual = getAccount(id);

        assertEquals(dto.getHolderId(), actual.getHolder().getId());
        assertEquals(dto.getIban(), actual.getIban());
        assertEquals(dto.getCurrency(), actual.getCurrency());
        assertEquals(dto.getInitialAmount(), actual.getAmount());
        assertTrue(actual.getId() > 0);
    }

    @Test
    public void postTransfer() throws Exception {
        TransferDto dto = newTransferDto(POUNDS, newBigDecimal(50));

        // Expected converted amounts (please see @CurrencyConverterImpl for rates)
        BigDecimal amountInSourceCurrency = newBigDecimal(56.01);
        BigDecimal amountInTargetCurrency = newBigDecimal(62.44);

        Account firstAccount = getAccount(firstAccountId);
        BigDecimal expectedBalanceFirstAccount = firstAccount.getAmount()
                .subtract(amountInSourceCurrency)
                .setScale(ROUND_DECIMALS, RoundingMode.HALF_UP);

        Account secondAccount = getAccount(secondAccountId);
        BigDecimal expectedBalanceSecondAccount = secondAccount.getAmount()
                .add(amountInTargetCurrency)
                .setScale(ROUND_DECIMALS, RoundingMode.HALF_UP);

        long id = postTransfer(dto);
        Transfer transfer = getTransfer(id);

        assertEquals(dto.getSourceAccountId(), transfer.getSource().getId());
        assertEquals(dto.getTargetAccountId(), transfer.getTarget().getId());
        assertEquals(dto.getAmount(), transfer.getAmount());
        assertEquals(dto.getCurrency(), transfer.getCurrency());
        assertEquals(amountInSourceCurrency, transfer.getAmountInSourceCurrency());
        assertEquals(amountInTargetCurrency, transfer.getAmountInTargetCurrency());
        assertTrue(transfer.isProcessed());

        // Assert correct balance in refreshed accounts
        Account refreshedFirstAccount = getAccount(firstAccountId);
        assertEquals(expectedBalanceFirstAccount, refreshedFirstAccount.getAmount());
        Account refreshedSecondAccount = getAccount(secondAccountId);
        assertEquals(expectedBalanceSecondAccount, refreshedSecondAccount.getAmount());

        // Check account transfers
        Map<String, List<Map<String, Object>>> firstAccountTransfers = getTransfersForAccount(firstAccountId);
        assertEquals(firstAccountTransfers.get(WITHDRAWAL.toString()).size(), 1);
        assertEquals(firstAccountTransfers.get(DEPOSIT.toString()).size(), 0);
        Map<String, Object> withdrawal = firstAccountTransfers.get(WITHDRAWAL.toString()).get(0);
        assertEquals(withdrawal.get("accountIBAN"), refreshedSecondAccount.getIban());
        assertEquals(withdrawal.get("currency"), transfer.getCurrency().toString());
        assertEquals(newBigDecimal((Double) withdrawal.get("amount")), transfer.getAmount());
        assertEquals(newBigDecimal((Double) withdrawal.get("convertedAmount")), transfer.getAmountInSourceCurrency());

        Map<String, List<Map<String, Object>>> secondAccountTransfers = getTransfersForAccount(secondAccountId);
        assertEquals(secondAccountTransfers.get(WITHDRAWAL.toString()).size(), 0);
        assertEquals(secondAccountTransfers.get(DEPOSIT.toString()).size(), 1);
        Map<String, Object> deposit = secondAccountTransfers.get(DEPOSIT.toString()).get(0);
        assertEquals(deposit.get("accountIBAN"), refreshedFirstAccount.getIban());
        assertEquals(deposit.get("currency"), transfer.getCurrency().toString());
        assertEquals(newBigDecimal((Double) deposit.get("amount")), transfer.getAmount());
        assertEquals(newBigDecimal((Double) deposit.get("convertedAmount")), transfer.getAmountInTargetCurrency());
    }

    private Map<String, List<Map<String, Object>>> getTransfersForAccount(long id) throws Exception {
        ContentResponse response = httpClient.GET(serverUrl + AccountController.PATH + "/" + id + "/transfers");
        String content = response.getContentAsString();
        return objectMapper.readValue(content, HashMap.class);
    }

    private Account getAccount(long id) throws Exception {
        ContentResponse response = httpClient.GET(serverUrl + AccountController.PATH + "/" + id);
        String content = response.getContentAsString();
        return objectMapper.readValue(content, Account.class);
    }

    private Transfer getTransfer(long id) throws Exception {
        ContentResponse response = httpClient.GET(serverUrl + TransferController.PATH + "/" + id);
        String content = response.getContentAsString();
        return objectMapper.readValue(content, Transfer.class);
    }

    private TransferDto newTransferDto(Currency currency, BigDecimal amount) {
        TransferDto dto = new TransferDto();
        dto.setSourceAccountId(firstAccountId);
        dto.setTargetAccountId(secondAccountId);
        dto.setAmount(amount);
        dto.setCurrency(currency);
        return dto;
    }

    private long postTransfer(TransferDto dto) throws Exception {
        ContentResponse response = post(serverUrl + TransferController.PATH, dto);
        String content = response.getContentAsString();
        Transfer actual = objectMapper.readValue(content, Transfer.class);
        return actual.getId();
    }
}