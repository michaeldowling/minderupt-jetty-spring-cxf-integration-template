package authentication;


import org.apache.commons.codec.digest.DigestUtils;

public class ApiKeyHelper {

    public static String createApiKeyToken(String apiKey, String secretKey, String content) {

        String toHash = null;
        if(content == null) {
            toHash = apiKey + secretKey;
        } else {
            toHash = apiKey + secretKey + content;
        }

        String apiKeyToken = DigestUtils.sha256Hex(toHash);
        return(apiKeyToken);


    }




}
