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
import java.net.BindException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.BorderFactory; 
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.event.MouseMotionAdapter;

public class Client {

	private JFrame frame = new JFrame("Chasse au tr�sor");
	private JLabel messageLabel = new JLabel("");
	private Grille[] board = new Grille[49];
	private Grille currentGrille;
	private int loc;
	private Socket socket;
	private BufferedReader in;
	static BufferedImage p1;
	static BufferedImage p2;
	BufferedImage w;
	BufferedImage d;
	private PrintWriter out;
	private final JPanel panel = new JPanel();
	InetAddress LocalAdress = InetAddress.getLocalHost();
	String[] message = { "Voulez-vous rejouer ?", "Serveur d�connect�" };
	String windef;
	int indexMsg;
	boolean testServer = true;
	String effet = "effet de bord";
	static int compteur = 0;
	static String nomServeur;
	static String numPort;
	boolean etat = true;

	public int getIndexMsg() {
		return indexMsg;
	}

	public void setIndexMsg(int indexMsg) {
		this.indexMsg = indexMsg;
	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public Client() throws Exception {
		frame.getContentPane().addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				out.println("test");
			}
		}); 
		while (testServer) {
			if (compteur < 2)
				nomServeur = JOptionPane.showInputDialog(null, "Veuillez entrer l'adresse IP du Serveur",
						"IP du Serveur", JOptionPane.QUESTION_MESSAGE);
			if (compteur < 2)
				numPort = JOptionPane.showInputDialog(null, "Veuillez entrer le numero de port ",
						"PORT DE COMMUNICATION", JOptionPane.QUESTION_MESSAGE);

			try {
				String addr = InetAddress.getByName(nomServeur).getHostAddress(); 
				socket = new Socket(addr, Integer.parseInt(numPort));
				if (socket.isBound()) {
					testServer = false;
				} else
					testServer = true;
				etat = false; 
			} catch (BindException e) {

				JOptionPane.showMessageDialog(null,
						"Le port " + numPort + " est deja utiliser \n Choisissez a noouveau", "erreur",
						JOptionPane.INFORMATION_MESSAGE); 
			} catch (ConnectException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, "Le port choisi est inaccessible \n Choisissez a noouveau");

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, "le Serveur sp�cifi� est inaccessible \n Choisissez a noouveau");

			} catch (SocketException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, "Le r�seau choisi est inaccessible \n Choisissez a noouveau");

			}

		}

		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
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
				}
			});
			 
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

	public void playGame() throws Exception {
		String response;
		setIndexMsg(0);
		try {
			response = in.readLine();
			if (response.startsWith("DEBUT")) {
				char mark = response.charAt(6);
				frame.setTitle("Chasse au tr�sor - Joueur Num " + mark);
			}
			while (true) {
				response = in.readLine(); 
				if (response.startsWith("VALID_MOVE")) {
					messageLabel.setText("Attendre svp...");

					currentGrille.setColor(Color.GREEN);
					currentGrille.repaint();
				} else if (response.startsWith("OPPONENT_MOVED")) {
					loc = Integer.parseInt(response.substring(15));
					board[loc].setColor(Color.RED);
					board[loc].repaint();
					messageLabel.setText("Votre adversaire a jou�, a vous le tour...");
				} else if (response.startsWith("VICTORY")) {
					currentGrille.setColor(Color.YELLOW);
					currentGrille.repaint();
					messageLabel.setText("Bravo !! Tr�sor trouv� !!!...");
					windef = "Bravo !! Tr�sor trouv� !!!...";
					break;
				} else if (response.startsWith("DEFEAT")) {
					board[loc].setColor(Color.YELLOW);
					board[loc].repaint();
					messageLabel.setText("Vous avez perdu !!!....");
					windef = "Vous avez perdu !!!...."; 
					break;
				} else if (response.startsWith("MESSAGE")) {
					messageLabel.setText(response.substring(8));
				} else if (response.startsWith("DIED")) {
					messageLabel.setText("Oups !!! Votre adversaire vient de se d�connecter !!!....");
					windef="Oups !!! Votre adversaire vient de se d�connecter !!!....";
					break;
				}
			}
			out.println("QUIT");
		} catch (SocketException e) { 
			messageLabel.setText("Serveur d�connect�");
			setIndexMsg(1);
		} finally {
			socket.close();
		}
	}

	private boolean wantsToAgain() { 
		int response = JOptionPane.showConfirmDialog(frame, windef+" \n "+message[getIndexMsg()], windef, JOptionPane.YES_NO_OPTION);
		frame.dispose();
		return response == JOptionPane.YES_OPTION;
	}

	public static void main(String[] args) throws Exception {

		try {

			while (true) {
				compteur++; 
				Client client = new Client();
				client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				client.frame.setSize(548, 504);
				client.frame.setVisible(true);
				client.frame.setResizable(true);
				Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
				int x = (int) ((dimension.getWidth() - client.frame.getWidth()) / 2);
				int y = (int) ((dimension.getHeight() - client.frame.getHeight()) / 2);
				client.frame.setLocation(x, y);
				client.playGame();
				if (!client.wantsToAgain()) {
					break;
				}
			}
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Le Serveur n'est pas connect�");

		}
	}
}