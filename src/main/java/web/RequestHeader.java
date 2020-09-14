package web;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestHeader {

    private static final String HEADER_DELIMITER = ": ";

    private final Map<String, String> headers = new HashMap<>();

    public RequestHeader(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();

        while (!isEmpty(line)) {
            String[] headerToken = line.split(HEADER_DELIMITER);
            headers.put(headerToken[0], headerToken[1]);
            line = bufferedReader.readLine();
        }
    }

    private boolean isEmpty(String line) {
        return Objects.isNull(line) || Objects.equals(line, "");
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getContentLength() {
        return Integer.parseInt(headers.get("Content-Length"));
    }
}
