import java.net.InetAddress;
import java.net.ServerSocket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JLabel;

public class Server {
	static JFrame frame = new JFrame();

	/**
	 * @wbp.parser.entryPoint
	 */
	public static void main(String[] args) throws Exception {
		String numPort = JOptionPane.showInputDialog(null, "Veuillez entrer le numero de port ", "Connecter le Serveur",
				JOptionPane.QUESTION_MESSAGE);
		ServerSocket listener = new ServerSocket(Integer.parseInt(numPort));
		frame.getContentPane().setLayout(null);
		InetAddress addr = InetAddress.getLocalHost();
		System.out.println(addr.getHostName());
		System.out.println(addr.getHostAddress());
		JLabel lblAdresseIp = new JLabel("Adresse IP");
		lblAdresseIp.setBounds(38, 31, 81, 14);
		frame.getContentPane().add(lblAdresseIp);

		JLabel lblNewLabelIP = new JLabel(addr.getHostAddress());
		lblNewLabelIP.setBounds(143, 31, 168, 14);
		frame.getContentPane().add(lblNewLabelIP);

		JLabel lblPort = new JLabel("Port");
		lblPort.setBounds(38, 76, 46, 14);
		frame.getContentPane().add(lblPort);

		JLabel lblNewLabel_port = new JLabel(numPort);
		lblNewLabel_port.setBounds(143, 76, 168, 14);
		frame.getContentPane().add(lblNewLabel_port);
		frame.setVisible(true);
		try {
			while (true) {

				AppGame Game = new AppGame();
				Game.runplayer(listener);

			}
		} finally {
			listener.close();
		}
	}
}