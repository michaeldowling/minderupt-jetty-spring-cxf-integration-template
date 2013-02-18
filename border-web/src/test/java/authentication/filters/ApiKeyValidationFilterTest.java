package authentication.filters;


import authentication.ApiKeyHelper;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.message.Message;
import org.junit.Test;
import service.cache.CacheService;
import service.cache.memory.MemoryHashCacheServiceImpl;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ApiKeyValidationFilterTest {


    @Test
    public void testWillRejectEmptyTokenHeaders() throws Exception {

        Message message = buildMockCxfMessage(null, null, "{}");
        ApiKeyValidationFilter filter = new ApiKeyValidationFilter(createApiCacheService(), false);
        Response response = filter.handleRequest(message, null);

        assertNotNull(response);
        assertEquals(401, response.getStatus());

    }

    @Test
    public void testWillAcceptSecretKeyAsToken() throws Exception {

        Message message = buildMockCxfMessage("12345", "67890", "{\"body\":\"here\"}");
        ApiKeyValidationFilter filter = new ApiKeyValidationFilter(createApiCacheService(), false);
        Response response = filter.handleRequest(message, null);

        assertNull(response);

    }

    @Test
    public void testWillAcceptMessageWithPayloadPartOfToken() throws Exception {

        String messageBody = "{\"body\":\"here\"}";

        String token = ApiKeyHelper.createApiKeyToken("12345", "67890", messageBody);
        Message message = buildMockCxfMessage("12345", token, messageBody);
        ApiKeyValidationFilter filter = new ApiKeyValidationFilter(createApiCacheService(), true);
        Response response = filter.handleRequest(message, null);

        assertNull(response);

    }

    @Test
    public void testWillRejectMessageWithPayloadPartOfBadToken() throws Exception {

        String messageBody = "{\"body\":\"here\"}";

        String token = ApiKeyHelper.createApiKeyToken("12345", "67890", messageBody);
        Message message = buildMockCxfMessage("12345", token + "123", messageBody);
        ApiKeyValidationFilter filter = new ApiKeyValidationFilter(createApiCacheService(), true);
        Response response = filter.handleRequest(message, null);

        assertNotNull(response);
        assertEquals(401, response.getStatus());

    }

    private CacheService createApiCacheService() {

        MemoryHashCacheServiceImpl map = new MemoryHashCacheServiceImpl();
        map.put("12345", "67890");

        return(map);


    }


    private Message buildMockCxfMessage(String apiKey, String apiKeyToken, String messageBody) {

        Map<String, ArrayList<String>> headers = new HashMap<String, ArrayList<String>>();
        Message message = mock(Message.class);

        if(apiKey != null) {
            ArrayList<String> apiKeyValue = new ArrayList<String>();
            apiKeyValue.add(apiKey);
            headers.put(ApiKeyValidationFilter.API_KEY_NAME, apiKeyValue);
        }

        if(apiKeyToken != null) {
            ArrayList<String> apiKeyTokenValue = new ArrayList<String>();
            apiKeyTokenValue.add(apiKeyToken);
            headers.put(ApiKeyValidationFilter.API_KEY_TOKEN_NAME, apiKeyTokenValue);
        }

        when(message.get(Message.PROTOCOL_HEADERS)).thenReturn(headers);

        InputStream messageBodyInputStream = IOUtils.toInputStream(messageBody);
        when(message.getContent(InputStream.class)).thenReturn(messageBodyInputStream);

        return(message);



    }


}
