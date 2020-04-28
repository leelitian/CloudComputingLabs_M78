
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class Service implements Runnable {

    private Socket client;
    private Request request;
    private Response response;

    public Service(Socket client) throws IOException {
        this.client = client;
        request = new Request(client);
        response = new Response(client);
    }

    @Override
    public void run() {
        if (request.isMethodValid()) {
            try {
                serve();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                push501();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        release();
    }

    private void serve() throws IOException {
        String method = request.getMethod();
        String url = request.getUrl();
        if (method.equals("GET")) {
            File file = getFile(url);
            if (file != null) {
                pushFile(file);
            } else {
                push404();
            }
        } else if (method.equals("POST")) {
            String name = request.getPara("Name");
            String id = request.getPara("ID");
            if (url.equals("Post_show") && name != null && id != null) {
                pushPost200(name, id);
            } else {
                push404();
            }
        }
    }

    private File getFile(String url) {
//        String PATH = this.getClass().getResource("/").getPath();
    	
    	// 获取Jar包所在的路径
    	String PATH = System.getProperty("java.class.path");
    	int firstIndex = PATH.lastIndexOf(System.getProperty("path.separator")) + 1;
    	int lastIndex = PATH.lastIndexOf(File.separator) + 1;
    	PATH = PATH.substring(firstIndex, lastIndex);
    	
        File file = new File(PATH + (url.isEmpty() ? "index.html" : url));
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File f: files) {
                    if (f.getName().equals("index.html")) {
                        return f;
                    }
                }
            } else {
                return null;
            }
        } else if (file.exists()) {
            return file;
        }
//        System.out.println("???" + file.toString());
        return null;
    }

    private void pushFile(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            int length = inputStream.available();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            String fileContent = new String(bytes);
            response.print(fileContent);
            response.push(200);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pushPost200(String name, String id) throws IOException {
        response.println("<!DOCTYPE html>");
        response.println("<html>");
        response.println("<head>");
        response.println("    <title>Post Method</title>");
        response.println("</head>");
        response.println("<body>");
        response.println("    Your Name: " + name);
        response.println("    ID: " + id);
        response.println("    <hr><em>HTTP Web Server</em>");
        response.println("</body>");
        response.println("</html>");
        response.push(200);
    }

    private void push404() throws IOException {
        response.println("<!DOCTYPE html>");
        response.println("<html>");
        response.println("<head>");
        response.println("    <title>404 Not Found</title>");
        response.println("</head>");
        response.println("<body>");
        response.println("    Not Found");
        response.println("    <p>Couldn't find this file: ./" + request.getUrl() + " </p>");
        response.println("    <hr><em>HTTP Web Server</em>");
        response.println("</body>");
        response.println("</html>");
        response.push(404);
    }

    private void push501() throws IOException {
        response.println("<!DOCTYPE html>");
        response.println("<html>");
        response.println("<head>");
        response.println("    <title>501 Not Implemented</title>");
        response.println("</head>");
        response.println("<body>");
        response.println("    Not Implemented");
        response.println("    <p>Does not implement this method:" + request.getMethod() + " </p>");
        response.println("    <hr><em>HTTP Web Server</em>");
        response.println("</body>");
        response.println("</html>");
        response.push(501);
    }

    private void release() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
