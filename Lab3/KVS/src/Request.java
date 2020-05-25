import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Request {
    private DataInputStream dis;
    private String requestMessage;
    private Map<String, String> parasMap;

    public Request(Socket client) throws IOException {
        dis = new DataInputStream(client.getInputStream());
        parasMap = new HashMap<>();
    }

    public void receive() throws IOException {
        requestMessage = dis.readUTF();
    }

    public void parseRequestMessage() {
        // number of lines followed by the star
        int lines = Integer.parseInt(requestMessage.substring(1, requestMessage.indexOf(Utils.CRLF)));
        // split the message
        String[] paras = requestMessage.split(Utils.CRLF);
        // check the method
        switch (paras[2]) {
            case "SET":
                parasMap.put("METHOD", "SET");
                parasMap.put("KEY", paras[4]);
                // ignore the fucking format
                parasMap.put("VAL", requestMessage.substring(requestMessage.indexOf(paras[5])));
                break;
            case "GET":
                parasMap.put("METHOD", "GET");
                parasMap.put("KEY", paras[4]);
                break;
            case "DEL":
                parasMap.put("METHOD", "DEL");
                StringBuilder delKeys = new StringBuilder();
                delKeys.append(paras[4]);
                for (int i = 6; i < lines*2; i += 2) {
                    delKeys.append(Utils.CRLF);
                    delKeys.append(paras[i]);
                }
                parasMap.put("KEY", delKeys.toString());
                break;
            default:
                parasMap.put("METHOD", "MIS");
                break;
        }
    }

    public String get(String key) {
        return parasMap.get(key);
    }
}
