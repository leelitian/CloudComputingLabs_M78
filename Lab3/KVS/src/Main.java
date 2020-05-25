import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		Utils.getParas();

		if (Utils.isCoordinator) {
			Coordinator c = new Coordinator(Utils.coordinator_port);
		} else {
			new Thread(new Participant(Utils.participants_port.get(0))).start();
		}
	}

}