package web;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import utils.IOUtils;

public class RequestBody {

    private static final String PARAM_DELIMITER = "&";
    private static final String PARAM_KEY_VALUE_DELIMITER = "=";

    private String body;

    public RequestBody(BufferedReader br, int contentLength) throws IOException {
        if (hasNoBody(contentLength)) {
            return;
        }
        this.body = IOUtils.readData(br, contentLength);
    }

    private boolean hasNoBody(int contentLength) {
        return contentLength == 0;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getParsedBody() {
        Map<String, String> parsedBody = new HashMap<>();
        String[] splitBody = body.split(PARAM_DELIMITER);
        for (String content : splitBody) {
            String[] keyValue = content.split(PARAM_KEY_VALUE_DELIMITER);
            parsedBody.put(keyValue[0], keyValue[1]);
        }
        return parsedBody;
    }
}
