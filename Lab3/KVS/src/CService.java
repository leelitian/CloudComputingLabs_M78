import java.io.IOException;
import java.net.Socket;

public class CService {

    private Request request;
    private Response response;

    CService(Socket client) throws IOException {
        request = new Request(client);
        response = new Response(client);
    }

    public void receive() throws IOException {
        request.receive();
        request.parseRequestMessage();
    }

    public String genReqMsg() {
        return "TYPE=REQ";
    }

    public String genExeMsg(String type) {
        return String.format("TYPE=%s&METHOD=%s&KEY=%s&VAL=%s", type, get("METHOD"), get("KEY"), get("VAL"));
    }

    public String get(String key) {
        return request.get(key);
    }

    public void print(String info) {
        response.print(info);
    }

    public void push() throws IOException {
        response.push();
    }

    public void pushError() throws IOException {
        response.print("-ERROR\r\n");
        response.push();
    }
}
