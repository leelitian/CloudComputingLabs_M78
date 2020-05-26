import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class Response {

    private static final String BLANK = " ";
    private static final String CRLF = "\r\n";

    private StringBuilder responseMessage;
    private BufferedWriter bw;

    public Response(Socket client) throws IOException {
        responseMessage = new StringBuilder();
        bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
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
        bw.write(responseMessage.toString());
        bw.flush();
        responseMessage.delete(0, responseMessage.length());
    }
}
