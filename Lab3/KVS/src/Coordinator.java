import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Coordinator {

    private ServerSocket server;
    private boolean isRunning;
    private List<PService> participants;

    Coordinator(int port) throws IOException {
        server = new ServerSocket(port);
        isRunning = true;
        participants = new CopyOnWriteArrayList<>();
        init();
        work();
    }

    // initialize all the participants
    private void init() throws IOException {
        int cnt = 0;
        while (cnt < Utils.participant_num) {
            try {
                System.out.println("accepting...");
                Socket participant = server.accept();
                if (Utils.isParticipant(participant.getPort())) {
                    server.setSoTimeout(1200);
                    PService ps = new PService(participant);
                    new Thread(ps).start();
                    participants.add(ps);
                    //new Thread(new HeartbeatSender(Utils.coordinator_port, participant.getPort())).start();
                    cnt++;
                    // System.out.println(cnt);
                    System.out.println("New Participant: " + participant.getPort());
                }
            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                break;
            }
        }
        server.setSoTimeout(0);

        new Thread(() -> {
            while (isRunning) {
                for (PService ps: participants) {
                    if (ps.isClosed()) {
                        System.out.println("Disconnected: " + ps.getPort());
                        participants.remove(ps);
                    }
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void work() throws IOException {
        while (isRunning) {
            // accept client's request
            Socket client = server.accept();
            new Thread(new Channel(client)).start();
        }
    }

    public static void main(String[] args) throws IOException {
        Utils.getParas("C:/Users/liang/Desktop/coordinator.conf");
        Coordinator c = new Coordinator(Utils.coordinator_port);
    }

    class Channel implements Runnable {

        Socket client;
        CService cs;

        Channel(Socket client) throws IOException {
            this.client = client;
            this.cs = new CService(client);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    cs.receive();
                    // System.out.println("request accepted");

                    if (participants.size() == 0) {
                        cs.pushError();
                        continue;
                    }

                    // 1st phase part 1: notify participants
                    for (PService ps: participants) {
                        ps.send(cs.genReqMsg());
                    }
                    // System.out.println("1st pt1");

                    // 1st phase part 2: get results
                    boolean isPrepared = true;
                    for (PService ps: participants) {
                        String result;
                        while ((result = ps.getRmsg()) == null);
                        isPrepared &= Utils.getVal(result, "TYPE").equals("VCOMMIT");
                        ps.forward();
                    }
                    // System.out.println("1st pt2");

                    // 2nd phase part 1: execute
                    for (PService ps: participants) {
                        if (isPrepared) {
                            ps.send(cs.genExeMsg("COMMIT"));
                        } else {
                            ps.send(cs.genExeMsg("ABORT"));
                        }
                    }
                    // System.out.println("2nd pt1");

                    //2nd phase part 2: get results
                    String result2Client = "";
                    for (PService ps: participants) {
                        String result;
                        while ((result = ps.getRmsg()) == null);
                        if (Utils.getVal(result, "TYPE").equals("DONE")) {
                            result2Client = Utils.getVal(result, "VAL");
                        }
                        ps.forward();
                    }
                    // System.out.println("2nd pt2");

                    cs.print(result2Client);
                    cs.push();
                    // System.out.println("request done\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

