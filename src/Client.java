import javax.swing.JPanel;

import java.awt.GridLayout; 
import java.awt.Toolkit;
import java.io.InputStreamReader;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage; 

import javax.swing.JLabel;

import java.io.BufferedReader; 
import java.awt.Color;
import java.awt.Dimension;
import java.io.PrintWriter;
import java.net.Socket; 

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Client {

	private JFrame frame = new JFrame("Chasse au trésor");
	private JLabel messageLabel = new JLabel("");
	private ImageIcon icon;
	private ImageIcon opponentIcon;
	private ImageIcon winIcon;
	private ImageIcon defeatIcon;
	private Grille[] board = new Grille[49];
	private Grille currentGrille;
	private int loc;
	private static int PORT = 8901;
	private Socket socket;
	private BufferedReader in;
	static BufferedImage p1;
	static BufferedImage p2;
	BufferedImage w;
	BufferedImage d;
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
		boardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		for (int i = 0; i < board.length; i++) {
			final int j = i;
			board[i] = new Grille(); 
			board[i].addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					currentGrille = board[j];
					out.println("MOVE " + j);
					System.out.println(j); 
				} 
			}); 
			System.out.println(i);
			boardPanel.add(board[i]);
		}
		frame.getContentPane().setLayout(null);
		panel.setBounds(0, 0, 520, 462);
		panel.setLayout(null);
		panel.add(boardPanel); 
		frame.getContentPane().add(panel); 
		boardPanel.setBackground(Color.black);
		boardPanel.setLayout(new GridLayout(7, 7, 2, 2));
		messageLabel.setBounds(0, 398, 520, 64);
		panel.add(messageLabel);
		messageLabel.setBackground(Color.BLACK);

	}

	// * The main thread of the client will listen for messages from the server.
	// The first message will be a "DEBUT" message in which we receive our
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
			if (response.startsWith("DEBUT")) {
				char mark = response.charAt(6); 
				icon = new ImageIcon(mark == '1' ? "img/p1.png" : "img/p2.png");
				opponentIcon = new ImageIcon(mark == '1' ? "img/p2.png" : "img/p1.png");
				winIcon = new ImageIcon("img/w.png");
				defeatIcon = new ImageIcon("img/d.png");
				frame.setTitle("Chasse au trésor - Joueur Num " + mark);
			}
			while (true) {
				response = in.readLine();
				if (response.startsWith("VALID_MOVE")) {
					messageLabel.setText("Attendre svp...");
					currentGrille.setIcon(icon);
					currentGrille.repaint();
				} else if (response.startsWith("OPPONENT_MOVED")) {
					loc = Integer.parseInt(response.substring(15));
					board[loc].setIcon(opponentIcon);
					board[loc].repaint();
					messageLabel.setText("Votre adversaire a joué, a toi le tour...");
				} else if (response.startsWith("VICTORY")) {
					currentGrille.setIcon(winIcon);
					currentGrille.repaint();
					messageLabel.setText("Bravo !! Trésor trouvé !!!...");
					break;
				} else if (response.startsWith("DEFEAT")) {
					board[loc].setIcon(defeatIcon);
					board[loc].repaint();
					messageLabel.setText("Vous avez perdu !!!....");
					System.out.println();
					break;
				} else if (response.startsWith("MESSAGE")) {
					messageLabel.setText(response.substring(8));
				}else if (response.startsWith("DIED")) {
					messageLabel.setText("Oups !!! Votre adversaire vient de se déconnecter !!!....");
					break;}
			} 
			out.println("QUIT");
		} finally {
			socket.close();
		}
	}

	private boolean wantsToPlayAgain() {
		int response = JOptionPane.showConfirmDialog(frame, "Voulez-vous rejouer ?", "La chasse au trésor c'est le fun !",
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
			client.frame.setSize(548, 504);
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