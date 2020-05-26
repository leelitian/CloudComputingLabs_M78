import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Coordinator {

    private static int INITIALIZE_TIMEOUT = 450;

    private ServerSocket server;
    private boolean isRunning;
    private List<PService> participants;

    Coordinator(int port) throws IOException {
        System.out.println("Initializing...");
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
                Socket participant = server.accept();
                if (Utils.isParticipant(participant.getPort())) {
                    server.setSoTimeout(INITIALIZE_TIMEOUT);
                    PService ps = new PService(participant);
                    new Thread(ps).start();
                    participants.add(ps);
                    cnt++;
                    System.out.println("New Participant: " + participant.getPort());
                } else {
                    new Thread(new Channel(participant)).start();
                }
            } catch (SocketTimeoutException e) {
                // e.printStackTrace();
                break;
            }
        }
        server.setSoTimeout(0);
        System.out.println("Initialized with " + cnt + " participants");
    }

    private void work() throws IOException {
        while (isRunning) {
            System.out.println("Accepting...");
            // accept client's request
            Socket client = server.accept();
            new Thread(new Channel(client)).start();
        }
    }

    public boolean checkParticipant(PService ps) {
        if (ps.isClosed()) {
            System.out.println("Disconnected: " + ps.getPort());
            participants.remove(ps);
            return true;
        }
        return false;
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

        public boolean checkParticipants() throws IOException {
            // System.out.println("psize = " + participants.size());
            if (participants.size() == 0) {
                cs.pushError();
                return true;
            }
            return false;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    cs.receive();
                    System.out.println("request accepted: " + cs.genMsg());

                    if (checkParticipants()) {
                        continue;
                    }

                    // 1st phase part 1: notify participants
                    for (PService ps: participants) {
                        if (checkParticipant(ps)) {
                            continue;
                        }
                        ps.send(cs.genReqMsg());
                    }
                    System.out.println("1st pt1");

                    if (checkParticipants()) {
                        continue;
                    }

                    // 1st phase part 2: get results
                    boolean isPrepared = true;
                    for (PService ps: participants) {
                        String result = "";
                        while (!ps.isClosed() && (result = ps.getRmsg()) == null);
                        if (checkParticipant(ps)) {
                            continue;
                        }
                        assert result != null;
                        isPrepared &= Utils.getVal(result, "TYPE").equals("VCOMMIT");
                        ps.forward();
                    }
                    System.out.println("1st pt2");

                    if (checkParticipants()) {
                        continue;
                    }

                    // 2nd phase part 1: execute
                    for (PService ps: participants) {
                        if (checkParticipant(ps)) {
                            continue;
                        }
                        if (isPrepared) {
                            ps.send(cs.genExeMsg("COMMIT"));
                        } else {
                            ps.send(cs.genExeMsg("ABORT"));
                        }
                    }
                    System.out.println("2nd pt1");

                    if (checkParticipants()) {
                        continue;
                    }

                    //2nd phase part 2: get results
                    String result2Client = "";
                    for (PService ps: participants) {
                        String result = "";
                        // ensure the participant is connected
                        while (!ps.isClosed() && (result = ps.getRmsg()) == null);
                        if (checkParticipant(ps)) {
                            continue;
                        }
                        assert result != null;
                        if (Utils.getVal(result, "TYPE").equals("DONE")) {
                            result2Client = Utils.getVal(result, "VAL");
                        }
                        ps.forward();
                    }
                    System.out.println("2nd pt2");

                    if (checkParticipants()) {
                        continue;
                    }

                    cs.print(result2Client);
                    cs.push();
                    System.out.println("request done: " + cs.genMsg() + "\n");
                }
            } catch (IOException e) {
                // e.printStackTrace();
                System.out.println("Client disconnected");
            }
        }
    }
}

