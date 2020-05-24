package parse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parse {

	private static boolean isCoordinator;
	private static String coordinator_ip;
	private static Integer coordinator_port;
	private static List<String> participants_ip = new ArrayList<String>();
	private static List<Integer> participants_port = new ArrayList<Integer>();

	public static void main(String[] args) throws IOException {

		getParas();
		System.out.println(isCoordinator);
		System.out.println(coordinator_ip + " " + coordinator_port);

		System.out.println(participants_ip);
		System.out.println(participants_port);
	}

	private static void getParas() throws IOException {

		String config_path = System.getProperty("config_path");

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
			
			FileReader fr = new FileReader(PATH + config_path);
			BufferedReader br = new BufferedReader(fr);

			String line;
			while ((line = br.readLine()) != null) {
				if (line.isEmpty() || line.charAt(0) == '!')
					continue;
				System.out.println(line);
				String[] strArr = line.split(" ");

				if (strArr[0].equals("mode")) {
					if (strArr[1].equals("coordinator"))
						isCoordinator = true;
					else
						isCoordinator = false;
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
					}
				}
			}
			br.close();
			fr.close();
		}
	}
}
