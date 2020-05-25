import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException, InterruptedException {
		Utils.getParas();

		if (Utils.isCoordinator) {
			new Coordinator(Utils.coordinator_port);
		} else {
			new Participant(Utils.participants_port.get(0));
		}
	}

}