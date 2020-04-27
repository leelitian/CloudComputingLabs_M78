import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.*;

public class Request {

    private static final String BLANK = " ";
    private static final String CRLF = "\r\n";

    private String requestMessage;
    private String method;
    private String url;
    private String paras;
    private Map<String, String> parasMap;
    private boolean isMethodValid;

    public Request(Socket client) throws IOException {
        InputStream is = client.getInputStream();
        byte[] data = new byte[1024*1024];
        int len = is.read(data);
        if (len > 0) {
            requestMessage = new String(data, 0, len);
            parasMap = new HashMap<>();
            parseRequestMessage();
        }
    }

    public String getPara(String key) {
        return parasMap.get(key);
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public boolean isMethodValid() {
        return isMethodValid;
    }

    private void parseRequestMessage() {
        if (parseRequestLine()) {
            isMethodValid = true;
            if (paras != null && !paras.isEmpty()) {
                convertMap();
            }
        } else {
            isMethodValid = false;
        }
    }

    private boolean parseRequestLine() {
        String[] requests = requestMessage.substring(0, requestMessage.indexOf(CRLF)).split(" ");
        method = requests[0];
        if (method.equals("GET") || method.equals("POST")) {
            if (requests[1].contains("?")) {
                String[] urls = requests[1].split("\\?");
                url = urls[0].substring(1);
                paras = urls[1];
            } else {
                url = requests[1].substring(1);
            }

            if (method.equals("POST")) {
                String entityBody = requestMessage.substring(requestMessage.lastIndexOf(CRLF)).trim();
                if (paras == null) {
                    paras = entityBody;
                } else if (!entityBody.isEmpty()){
                    paras += ("&" + entityBody);
                }
            }

            System.out.println("method="+method);
            System.out.println("url="+url);
            System.out.println("paras="+paras);

            return true;
        }
        return false;
    }

    private void convertMap() {
        String[] keyValues = paras.split("&");
        for (String keyValue: keyValues) {
            String[] kv = keyValue.split("=");
            kv = Arrays.copyOf(kv, 2);
            String key = kv[0];
            String val = kv[1];
            parasMap.put(key, val);
        }
    }
}
