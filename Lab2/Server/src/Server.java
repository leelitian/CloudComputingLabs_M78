import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Server {

    private ServerSocket serverSocket;
    private ThreadPoolExecutor executor;

    private static String ip = "127.0.0.1";
    private static int port = 80;
    private static int tnum = 4;

    private static boolean isRunning;

    Server(int tnum) {
        executor = new ThreadPoolExecutor(tnum, tnum, 200, TimeUnit.MILLISECONDS,
        		new LinkedBlockingQueue<Runnable>());
//                new ArrayBlockingQueue<Runnable>(2 * tnum));
    }

    public static void main(String[] args) {
        getParas();

        Server server = new Server(tnum);
        try {
            server.start();
        } catch (IOException e) {
            try {
                server.stop();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private void start() throws IOException {
        serverSocket = new ServerSocket(port);
        isRunning = true;

        receive();
    }

    private void receive() throws IOException {
        while (isRunning) {
            Socket clientSocket = serverSocket.accept();
            executor.execute(new Service(clientSocket));
        }
    }

    private void stop() throws IOException {
        serverSocket.close();
        isRunning = false;
    }

    private static void getParas() {
        String tport = System.getProperty("port");
        if (tport != null && !tport.isEmpty()) {
            port = Integer.parseInt(tport);
        }
        String ttnum = System.getProperty("number-thread");
        if (ttnum != null && !ttnum.isEmpty()) {
            tnum = Integer.parseInt(ttnum);
        }
    }
}
