import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Utils {

	public static final String CRLF = "\r\n";

	public static boolean isCoordinator;
	public static String coordinator_ip;
	public static Integer coordinator_port;
	public static List<String> participants_ip = new ArrayList<>();
	public static List<Integer> participants_port = new ArrayList<>();
	public static int participant_num;

	public static boolean isParticipant(int port) {
		for (int pport: participants_port) {
			if (pport == port) {
				return true;
			}
		}
		return false;
	}

	public static String getVal(String msg, String key) {
		String[] paras = msg.split("&");
		for (String para: paras) {
			if (para.split("=")[0].equals(key)) {
				return para.split("=")[1];
			}
		}
		return "";
	}

	public static void getParas(String[] args) throws IOException {
		
		String config_path = "";
		if(args.length == 2 && args[0].equals("--config_path"))
			config_path = args[1];
		
//		String config_path = System.getProperty("config_path");

		if (config_path != null && !config_path.isEmpty()) {

/*-----------------------------------PATH of the Class------------------------------------------*/
//			String PATH = Object.class.getResource("/").getPath();


/*-----------------------------------PATH of the JAR------------------------------------------*/
//			String PATH = System.getProperty("java.class.path");
//			int firstIndex = PATH.lastIndexOf(System.getProperty("path.separator")) + 1;
//			int lastIndex = PATH.lastIndexOf(File.separator) + 1;
//			PATH = PATH.substring(firstIndex, lastIndex);
/*--------------------------------------------------------------------------------------------*/

//			System.out.println(PATH + config_path);
			
			FileReader fr = new FileReader(config_path);
			BufferedReader br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				if (line.isEmpty() || line.charAt(0) == '!')
					continue;
				// System.out.println(line);
				String[] strArr = line.split(" ");

				if (strArr[0].equals("mode")) {
					isCoordinator = strArr[1].equals("coordinator");
				} else {
					String[] address = strArr[1].split(":");
					String ip = address[0];
					Integer port = Integer.parseInt(address[1]);

					if (strArr[0].equals("coordinator_info")) {
						coordinator_ip = ip;
						coordinator_port = port;
					} else {
						participants_ip.add(ip);
						participants_port.add(port);
						participant_num++;
					}
				}
			}
			br.close();
			fr.close();
		}
	}

	public static void getParas(String config_path) throws IOException {

		// String config_path = System.getProperty("config_path");

		if (config_path != null && !config_path.isEmpty()) {

			/*-----------------------------------PATH of the Class------------------------------------------*/
//			String PATH = Object.class.getResource("/").getPath();


			/*-----------------------------------PATH of the JAR------------------------------------------*/
			String PATH = System.getProperty("java.class.path");
			int firstIndex = PATH.lastIndexOf(System.getProperty("path.separator")) + 1;
			int lastIndex = PATH.lastIndexOf(File.separator) + 1;
			PATH = PATH.substring(firstIndex, lastIndex);
			/*--------------------------------------------------------------------------------------------*/

//			System.out.println(PATH + config_path);

			FileReader fr = new FileReader(config_path);
			BufferedReader br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				if (line.isEmpty() || line.charAt(0) == '!')
					continue;
				// System.out.println(line);
				String[] strArr = line.split(" ");

				if (strArr[0].equals("mode")) {
					isCoordinator = strArr[1].equals("coordinator");
				} else {
					String[] address = strArr[1].split(":");
					String ip = address[0];
					Integer port = Integer.parseInt(address[1]);

					if (strArr[0].equals("coordinator_info")) {
						coordinator_ip = ip;
						coordinator_port = port;
					} else {
						participants_ip.add(ip);
						participants_port.add(port);
						participant_num++;
					}
				}
			}
			br.close();
			fr.close();
		}
	}
}
