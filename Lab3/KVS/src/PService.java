import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class PService implements Runnable {

    private Socket participant;
    private DataInputStream dis;
    private DataOutputStream dos;
    private volatile String rmsg;
    private boolean isClosed;

    PService(Socket participant) throws IOException {
        this.participant = participant;
        dis = new DataInputStream(participant.getInputStream());
        dos = new DataOutputStream(participant.getOutputStream());
        isClosed = false;
    }

    private String receive() throws IOException {
        return dis.readUTF();
    }

    public void send(String msg) throws IOException {
        dos.writeUTF(msg);
        dos.flush();
    }

    private void receiveRequestResult() throws IOException {
        rmsg = receive();
    }

    public void forward() {
        rmsg = null;
    }

    public String getRmsg() {
        return rmsg;
    }

    public int getPort() {
        return participant.getPort();
    }

    public boolean isClosed() {
        return isClosed;
    }

    @Override
    public void run() {
        while (true) {
            try {
                receiveRequestResult();
                receiveRequestResult();
            } catch (IOException e) {
                // e.printStackTrace();
                isClosed = true;
                break;
            }
        }
    }
}
