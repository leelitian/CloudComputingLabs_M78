import java.io.*;
import java.net.*;

class Client {

	// 建立到Coordinator的连接
	private Socket client;
	// 从标准输入缓冲区读取用户数据
	private DataInputStream dis;
	private DataOutputStream dos;

	Client() throws IOException {
		client = new Socket("127.0.0.1", 8001);
		dis = new DataInputStream(client.getInputStream());
		dos = new DataOutputStream(client.getOutputStream());
	}

	public void send(String msg) throws IOException {
		dos.writeUTF(cmd2resp(msg)); // 将cmd编码为resp格式再发送
		dos.flush();
		System.out.println(msg);
	}

	public String receive() throws IOException {
		return dis.readUTF();
	}

	public static void main(String[] argv) throws Exception {
		Client client = new Client();
		client.send("SET CS06142 \"Cloud Computing\"");
		System.out.println(client.receive());
		Thread.sleep(500);
		client.send("GET CS06142");
		System.out.println(client.receive());
		Thread.sleep(500);
		client.send("SET CS06142 \"!!!!!!\"");
		System.out.println(client.receive());
		Thread.sleep(500);
		client.send("GET CS06142");
		System.out.println(client.receive());
		Thread.sleep(500);
		client.send("DEL CS162");
		System.out.println(client.receive());
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
