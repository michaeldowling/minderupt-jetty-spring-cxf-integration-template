package authentication.filters;


import authentication.ApiKeyHelper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxrs.ext.RequestHandler;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.message.Message;
import service.cache.CacheService;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class ApiKeyValidationFilter implements RequestHandler {

    private static Log LOGGER = LogFactory.getLog(ApiKeyValidationFilter.class);
    public static final String API_KEY_NAME = "X-api-key";
    public static final String API_KEY_TOKEN_NAME = "X-api-key-token";

    private CacheService cacheService;
    private boolean checkTokenFlag;


    public ApiKeyValidationFilter(CacheService cacheService, boolean checkTokenFlag) {
        this.cacheService = cacheService;
        this.checkTokenFlag = checkTokenFlag;
    }


    @Override
    public Response handleRequest(Message message, ClassResourceInfo classResourceInfo) {

        if(LOGGER.isInfoEnabled()) LOGGER.info("Authenticating request");
        Map headers = (Map) message.get(Message.PROTOCOL_HEADERS);

        if(headers == null || headers.get(API_KEY_NAME) == null || headers.get(API_KEY_TOKEN_NAME) == null) {

            if(LOGGER.isDebugEnabled()) LOGGER.debug("No api key or api key token headers present.");
            return(Response.status(Response.Status.UNAUTHORIZED).build());

        }

        String apiKey = ((ArrayList<String>) headers.get(API_KEY_NAME)).get(0);
        String apiKeyToken = ((ArrayList<String>) headers.get(API_KEY_TOKEN_NAME)).get(0);
        if(LOGGER.isDebugEnabled()) LOGGER.debug("** API KEY: " + apiKey);
        if(LOGGER.isDebugEnabled()) LOGGER.debug("** API KEY TOKEN: " + apiKeyToken);

        if(apiKey == null || apiKeyToken == null || this.cacheService.get(apiKey, String.class) == null) {
            if(LOGGER.isDebugEnabled()) LOGGER.debug("Invalid API Key or API Key Token!");
            return(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        if(!this.checkTokenFlag) {

            // just check to make sure the api key exists, and x-token is the secret key
            if(LOGGER.isInfoEnabled()) LOGGER.info("Not checking token - verifying X-api-key-token == secretKey");
            String secretKeyToAssert = this.cacheService.get(apiKey, String.class);
            if(secretKeyToAssert != null && apiKeyToken.equals(secretKeyToAssert)) {
                return(null);
            } else {
                return(Response.status(Response.Status.UNAUTHORIZED).build());
            }

        }

        String messagePayload = null;
        try {
            messagePayload = IOUtils.toString(message.getContent(InputStream.class));
        } catch(IOException ioe) {
            LOGGER.fatal("Unable to decode message body from content.");
            return(Response.serverError().build());
        }


        String apiKeyTokenToAssert = ApiKeyHelper.createApiKeyToken(apiKey, this.cacheService.get(apiKey, String.class), messagePayload);
        if(!apiKeyToken.equals(apiKeyTokenToAssert)) {
            return(Response.status(Response.Status.UNAUTHORIZED).build());
        }

        return(null);

    }
}
