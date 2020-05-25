import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Participant implements Runnable{

    private int port;
    private Socket coordinator;
    private DataInputStream dis;
    private DataOutputStream dos;
    private Map<String, String> data;

    Participant(int port) throws IOException {
        this.port = port;
        coordinator = new Socket();
        coordinator.bind(new InetSocketAddress(port));
        coordinator.connect(new InetSocketAddress(Utils.coordinator_port));
        dis = new DataInputStream(coordinator.getInputStream());
        dos = new DataOutputStream(coordinator.getOutputStream());
        data = new HashMap<>();
        System.out.println("P: " + port);
        //work();
    }

    private String receive() throws IOException {
        return dis.readUTF();
    }

    private void send(String msg) throws IOException {
        dos.writeUTF(msg);
        dos.flush();
    }

    private void sendPrepared() throws IOException {
        send("TYPE=VCOMMIT");
    }

    private void sendUnprepared() throws IOException {
        send("TYPE=VABORT");
    }

    private void sendResult(String values) throws IOException {
        send("TYPE=DONE&VAL=" + values);
    }

    private void doCommit(String msg) throws IOException {
        String method = Utils.getVal(msg, "METHOD");
        String key = Utils.getVal(msg, "KEY");
        String val;
        switch (method) {
            case "SET":
                val = Utils.getVal(msg, "VAL");
                data.put(key, val);
                sendResult("+OK\r\n");
                break;
            case "GET":
                val = data.get(key);
                if (val == null) {
                    sendResult("*1\r\n$3\r\nnil\r\n");
                } else {
                    int cnt = 0;
                    for (int i = 0; i < val.length(); ++i) {
                        if (val.charAt(i) == '$') {
                            cnt ++;
                        }
                    }
                    sendResult("*" + cnt + Utils.CRLF + val);
                }
                break;
            case "DEL":
                int cnt = 0;
                String[] keys = key.split(Utils.CRLF);
                for (String dkey: keys) {
                    if (data.remove(dkey) != null) {
                        cnt ++;
                    }
                }
                sendResult(":" + cnt + Utils.CRLF);
                break;
            default:
                sendResult("-ERROR\r\n");
        }
    }

    private void work() throws IOException {
        while (true) {
            // 1st phase
            String msg = receive();
            System.out.println(port + " 1\n" + msg);
            if (Utils.getVal(msg, "TYPE").equals("REQ")) {
                sendPrepared();
            }

            // 2nd phase
            msg = receive();
            System.out.println(port + " 2\n" + msg);
            if (Utils.getVal(msg, "TYPE").equals("COMMIT")) {
                doCommit(msg);
            } else {
                sendResult("");
            }
        }
    }

    @Override
    public void run() {
        try {
            work();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Utils.getParas("C:/Users/liang/Desktop/coordinator.conf");
        for (int i = 0; i < Utils.participant_num; ++i) {
            new Thread(new Participant(Utils.participants_port.get(i))).start();
        }
    }
}
