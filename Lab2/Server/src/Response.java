import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Date;

public class Response {

    private static final String BLANK = " ";
    private static final String CRLF = "\r\n";

    private StringBuilder head;
    private StringBuilder content;
    private BufferedWriter bw;

    public Response(Socket client) throws IOException {
        head = new StringBuilder();
        content = new StringBuilder();
        bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
    }

    // 添加响应内容
    public Response print(String info) {
        content.append(info);
        return this;
    }

    // 添加响应内容并换行
    public Response println(String info) {
        content.append(info).append(CRLF);
        return this;
    }

    // 推送响应报文
    public void push(int code) throws IOException {
        createHead(code);
        bw.append(head);
        bw.append(content);
        bw.flush();
    }

    // 生成响应报文首部
    private void createHead(int code) {
        // 构造初始状态行
        head.append("HTTP/1.1").append(BLANK);
        head.append(code).append(BLANK);
        head.append(getStatusInfo(code)).append(CRLF);

        // 构造首部行
        head.append("Date: ").append(new Date()).append(CRLF);
        head.append("Server: ").append("M78 Web Server").append(CRLF);
        head.append("Content-type: ").append("text/html").append(CRLF);
        head.append("Content-length: ").append(getByteLength(content)).append(CRLF);
        head.append(CRLF);
    }

    // 构造状态信息
    private String getStatusInfo(int code) {
        switch (code) {
            case 200:
                return "OK";
            case 404:
                return "Not Found";
            case 501:
                return "Not Implemented";
            default:
                return "";
        }
    }

    private int getByteLength(StringBuilder sb) {
        return sb.toString().getBytes().length;
    }

}
