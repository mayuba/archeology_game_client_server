import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.InputStreamReader;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import java.io.BufferedReader;
import java.awt.Color;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.BorderLayout;

public class Client {

	private JFrame frame = new JFrame("Tic Tac Toe");
	private JLabel messageLabel = new JLabel("");
	private ImageIcon icon;
	private ImageIcon opponentIcon;

	private Grille[] board = new Grille[16];
	private Grille currentGrille;

	private static int PORT = 8901;
	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private final JPanel panel = new JPanel();

	// Constructs the client by connecting to a server, laying out the GUI and
	// registering GUI listeners.

	/**
	 * @wbp.parser.entryPoint
	 */
	public Client(String serverAddress) throws Exception {

		// Setup networking
		socket = new Socket(serverAddress, PORT);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);

		// Layout GUI
		JPanel boardPanel = new JPanel();
		boardPanel.setBounds(59, 11, 389, 355);
		frame.getContentPane().setLayout(null);
		for (int i = 0; i < board.length; i++) {
			final int j = i;
			board[i] = new Grille();

			board[i].addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					currentGrille = board[j];
					out.println("MOVE " + j);
					System.out.println(j);
					// System.out.println("presse");
				}
			});

			System.out.println(i);
			boardPanel.add(board[i]);
		}
		panel.setLayout(null);
		panel.add(boardPanel);
		panel.setBounds(0, 0, 520, 462);

		frame.getContentPane().add(panel);

		boardPanel.setBackground(Color.black);
		boardPanel.setLayout(new GridLayout(4, 4, 2, 2));
		messageLabel.setBounds(0, 398, 520, 64);
		panel.add(messageLabel);
		messageLabel.setBackground(Color.BLACK);
	}

	// * The main thread of the client will listen for messages from the server.
	// The first message will be a "WELCOME" message in which we receive our
	// mark.
	// Then we go into a loop listening for:
	// --> "VALID_MOVE", --> "OPPONENT_MOVED", --> "VICTORY", --> "DEFEAT", -->
	// "TIE", --> "OPPONENT_QUIT, --> "MESSAGE" messages, and handling each
	// message appropriately.
	// The "VICTORY","DEFEAT" and "TIE" ask the user whether or not to play
	// another game.
	// If the answer is no, the loop is exited and the server is sent a "QUIT"
	// message. If an OPPONENT_QUIT message is recevied then the loop will exit
	// and the server will be sent a "QUIT" message also.
	public void play() throws Exception {
		String response;
		try {
			response = in.readLine();
			if (response.startsWith("WELCOME")) {
				char mark = response.charAt(8);
				icon = new ImageIcon(mark == 'X' ? "x.png" : "o.png");
				opponentIcon = new ImageIcon(mark == 'X' ? "o.png" : "x.png");
				frame.setTitle("Tic Tac Toe - Player " + mark);
			}
			while (true) {
				response = in.readLine();
				if (response.startsWith("VALID_MOVE")) {
					messageLabel.setText("Valid move, please wait");
					currentGrille.setIcon(icon);
					currentGrille.repaint();
				} else if (response.startsWith("OPPONENT_MOVED")) {
					int loc = Integer.parseInt(response.substring(15));
					board[loc].setIcon(opponentIcon);
					board[loc].repaint();
					messageLabel.setText("Opponent moved, your turn");
				} else if (response.startsWith("VICTORY")) {
					messageLabel.setText("You win");
					break;
				} else if (response.startsWith("DEFEAT")) {
					messageLabel.setText("You lose");
					break;
				} else if (response.startsWith("TIE")) {
					messageLabel.setText("You tied");
					break;
				} else if (response.startsWith("MESSAGE")) {
					messageLabel.setText(response.substring(8));
				}
			}
			out.println("QUIT");
		} finally {
			socket.close();
		}
	}

	private boolean wantsToPlayAgain() {
		int response = JOptionPane.showConfirmDialog(frame, "Want to play again?", "Tic Tac Toe is Fun Fun Fun",
				JOptionPane.YES_NO_OPTION);
		frame.dispose();
		return response == JOptionPane.YES_OPTION;
	}

	// main
	public static void main(String[] args) throws Exception {
		while (true) {
			String serverAddress = (args.length == 0) ? "localhost" : args[1];
			Client client = new Client(serverAddress);
			client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			client.frame.setSize(520, 504);
			client.frame.setVisible(true);
			client.frame.setResizable(true);
			Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
			int x = (int) ((dimension.getWidth() - client.frame.getWidth()) / 2);
			int y = (int) ((dimension.getHeight() - client.frame.getHeight()) / 2);
			client.frame.setLocation(x, y);
			client.play();
			if (!client.wantsToPlayAgain()) {
				break;
			}
		}
	}
}