package TCP;

import java.io.*;
import java.net.*;

class Client {

	public static void main(String argv[]) throws Exception {

		// 建立到Coordinator的连接
		Socket client = new Socket("127.0.0.1", 8001);

		// 从标准输入缓冲区读取用户数据
		BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			// 从键盘读入一条命令（如：GET CS06142）
			String cmd = userInput.readLine();
			String send = cmd2resp(cmd);		// 将cmd编码为resp格式再发送
			if (cmd.equals("exit"))
				break;

			// 向Coordinator发送
			DataOutputStream output = new DataOutputStream(client.getOutputStream());
			output.writeUTF(send);
			output.flush();

			// 从Coordinator获取数据
			DataInputStream dataInputStream = new DataInputStream(client.getInputStream());
			String response = dataInputStream.readUTF();
			
			// 打印response（已经解码为普通字符串）
			System.out.println(resp2response(response));
			// System.out.println(response);
		}

		client.close();
	}
	
	// 编码
	public static String cmd2resp(String cmd) {
		String[] arr = cmd.split(" ");

		String resp = "*" + Integer.toString(arr.length) + "\r\n";
		for (int i = 0; i < arr.length; ++i)
			resp = resp + "$" + Integer.toString(arr[i].length()) + "\r\n" + arr[i] + "\r\n";
		return resp;
	}
	
	// 解码
	public static String resp2response(String resp) {
		String[] arr = resp.split("\r\n");
		String result = "";
		for (String s : arr) {
			if (s.charAt(0) != '*' && s.charAt(0) != '$')
				result = result + s + " ";
		}
		return result;
	}
}
