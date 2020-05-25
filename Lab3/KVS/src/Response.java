import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Response {

    private static final String BLANK = " ";
    private static final String CRLF = "\r\n";

    private StringBuilder responseMessage;
    private DataOutputStream dos;

    public Response(Socket client) throws IOException {
        responseMessage = new StringBuilder();
        dos = new DataOutputStream(client.getOutputStream());
    }

    public Response print(String info) {
        responseMessage.append(info);
        return this;
    }

    public Response println(String info) {
        responseMessage.append(info).append(CRLF);
        return this;
    }

    public void push() throws IOException {
        dos.writeUTF(responseMessage.toString());
        dos.flush();
        responseMessage.delete(0, responseMessage.length());
    }
}
