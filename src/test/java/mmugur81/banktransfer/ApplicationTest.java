package mmugur81.banktransfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import mmugur81.banktransfer.controller.AccountController;
import mmugur81.banktransfer.controller.HolderController;
import mmugur81.banktransfer.domain.Account;
import mmugur81.banktransfer.domain.Holder;
import mmugur81.banktransfer.dto.AccountDto;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentProvider;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static mmugur81.banktransfer.TestUtils.newHolder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * API endpoints test
 */
class ApplicationTest {

    private static HttpClient httpClient;
    private static ObjectMapper objectMapper;

    private static String serverUrl = "http://localhost:7000";

    @BeforeAll
    public static void setApp() throws Exception {
        String[] args = {"7000"};
        Application.main(args);

        httpClient = new HttpClient();
        httpClient.start();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        // Setup some data
        //postHolder("First Holder");
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
        dto.setHolderId(1);

        long id = postAccount(dto);
        Account actual = getAccount(id);

        //assertEquals(holderName, actual.getName());
        assertTrue(actual.getId() > 0);

    }

    private long postHolder(String name) throws Exception {
        ContentResponse response = post(serverUrl + HolderController.PATH, newHolder(name));
        String content = response.getContentAsString();
        Holder actual = objectMapper.readValue(content, Holder.class);
        return actual.getId();
    }

    private Holder getHolder(long id) throws Exception {
        ContentResponse response = httpClient.GET(serverUrl + HolderController.PATH + "/" + id);
        String content = response.getContentAsString();
        return objectMapper.readValue(content, Holder.class);
    }

    private long postAccount(AccountDto dto) throws Exception {
        ContentResponse response = post(serverUrl + HolderController.PATH, dto);
        String content = response.getContentAsString();
        Account actual = objectMapper.readValue(content, Account.class);
        return actual.getId();
    }

    private Account getAccount(long id) throws Exception {
        ContentResponse response = httpClient.GET(serverUrl + AccountController.PATH + "/" + id);
        String content = response.getContentAsString();
        return objectMapper.readValue(content, Account.class);
    }

    private ContentResponse post(String uri, Object payload) throws Exception {
        ContentProvider contentProvider = new StringContentProvider(objectMapper.writeValueAsString(payload));
        return httpClient.POST(uri).content(contentProvider).send();
    }
}