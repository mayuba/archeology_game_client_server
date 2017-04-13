import java.net.InetAddress;
import java.net.ServerSocket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Server {

	public static void main(String[] args) throws Exception {
		String numPort = JOptionPane.showInputDialog(null, "Veuillez entrer le numero de port ", "Connecter le Serveur",
				JOptionPane.QUESTION_MESSAGE);
		ServerSocket listener = new ServerSocket(Integer.parseInt(numPort));

		try {
			while (true) {
				InetAddress addr=InetAddress.getLocalHost();
				System.out.println(addr.getHostName());
				 System.out.println(addr.getHostAddress());
				AppGame Game = new AppGame();

				Game.runplayer(listener);

			}
		} finally {
			listener.close();
		}
	}

}