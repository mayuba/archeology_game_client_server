<<<<<<< HEAD
import java.net.InetAddress;
import java.net.ServerSocket;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Server {

	/**
	 * @wbp.parser.entryPoint
	 */
	public static void main(String[] args) throws Exception {
		String numPort = JOptionPane.showInputDialog(null, "Veuillez entré le numero de port ", "Connecter le server",
				JOptionPane.QUESTION_MESSAGE);
		ServerSocket listener = new ServerSocket(Integer.parseInt(numPort));

		try {
			while (true) {
				InetAddress addr=InetAddress.getLocalHost();
				System.out.println(addr.getHostName());
				 System.out.println(addr.getHostAddress());
				AppGame Game = new AppGame();

				Game.runplayer(listener);

=======
import java.awt.EventQueue;
import java.net.ServerSocket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Server {

	private JFrame frame;
	private JTextField textField;

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(48, 50, 334, 185);
		frame.getContentPane().add(panel);
		panel.setLayout(null);

		JLabel lblPort = new JLabel("port");
		lblPort.setBounds(25, 11, 46, 14);
		frame.getContentPane().add(lblPort);

		JButton btnNewButton = new JButton("New button");
		btnNewButton.setBounds(257, 7, 89, 23);
		frame.getContentPane().add(btnNewButton);

		textField = new JTextField();
		textField.setBounds(93, 8, 130, 20);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		JTextArea textArea = new JTextArea();
		textArea.setBounds(48, 50, 334, 185);
		frame.getContentPane().add(textArea);
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public Server() {
		initialize();
	}

	public static void main(String[] args) throws Exception {
		ServerSocket listener = new ServerSocket(8901);
		System.out.println(" Chasse au trésor...Serveur connecté...");

		try {
			while (true) {
				Server window = new Server();
				window.frame.setVisible(true);
				
				System.out.println(listener.getInetAddress());
				AppGame Game = new AppGame();
				Game.runplayer(listener);
>>>>>>> origin/master
			}
		} finally {
			listener.close();
		}
	}

}
