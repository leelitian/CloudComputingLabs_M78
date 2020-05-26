import java.io.*;
import java.net.*;

class Client {

	// 建立到Coordinator的连接
	private Socket client;
	// 从标准输入缓冲区读取用户数据
	private BufferedReader br;
	private BufferedWriter bw;
	private char[] buffer;

	Client() throws IOException {
		client = new Socket("127.0.0.1", 8001);
		br = new BufferedReader(new InputStreamReader(client.getInputStream()));
		bw = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
		buffer = new char[1024];
	}

	public void send(String msg) throws IOException {
		bw.write(cmd2resp(msg)); // 将cmd编码为resp格式再发送
		bw.flush();
		System.out.println(cmd2respstring(msg));
	}

	public String receive() throws IOException {
		int len = br.read(buffer);
		return new String(buffer, 0, len);
	}

	public static void main(String[] argv) throws Exception {
		Client client = new Client();
		client.send("SET item5_key_1 item5_value_1");
		System.out.println(client.receive());
		Thread.sleep(500);
		client.send("SET item5_key_2 item5_value_2");
		System.out.println(client.receive());
		Thread.sleep(500);
		client.send("DEL item5_key_1 item5_key_2");
		System.out.println(client.receive());
		// System.out.println(cmd2respstring("DEL item5_key_1 item5_key_2"));
	}
	
	// 编码
	public static String cmd2resp(String cmd) {
		String[] arr = cmd.split(" ");

		StringBuilder resp = new StringBuilder("*" + arr.length + "\r\n");
		for (String s : arr) {
			if (s.contains("\"")) {
				s = s.replace("\"", "");
			}
			resp.append("$").append(s.length()).append("\r\n").append(s).append("\r\n");
		}
		return resp.toString();
	}

	public static String cmd2respstring(String cmd) {
		String[] arr = cmd.split(" ");

		StringBuilder resp = new StringBuilder("*" + arr.length + "\\r\\n");
		for (String s : arr) {
			if (s.contains("\"")) {
				s = s.replace("\"", "");
			}
			resp.append("$").append(s.length()).append("\\r\\n").append(s).append("\\r\\n");
		}
		return resp.toString();
	}
	
	// 解码
	public static String resp2response(String resp) {
		String[] arr = resp.split("\r\n");
		StringBuilder result = new StringBuilder();
		for (String s : arr) {
			if (s.charAt(0) != '*' && s.charAt(0) != '$')
				result.append(s).append(" ");
		}
		return result.toString();
	}
}
