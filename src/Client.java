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
	private Socket socket;
	private BufferedReader in;
	static BufferedImage p1;
	static BufferedImage p2;
	BufferedImage w;
	BufferedImage d;
	private PrintWriter out;
	private final JPanel panel = new JPanel();
	InetAddress LocalAdress = InetAddress.getLocalHost();
	String[] message = { "Voulez-vous rejouer ?", "Serveur déconnecté" };
	String windef;
	int indexMsg;
	boolean testServer = true;
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

	public Client() throws Exception {
		System.out.println(etat + " etat <-------------------");
		while (testServer) {
			if (compteur < 2)
				nomServeur = JOptionPane.showInputDialog(null,
						"Veuillez entrer L'adresse IP du Serveur", "IP Serveur",
						JOptionPane.QUESTION_MESSAGE);
			if (compteur < 2)
				numPort = JOptionPane.showInputDialog(null,
						"Veuillez entrer le numero de port ",
						"Port de Communication", JOptionPane.QUESTION_MESSAGE);

			try {
				String addr = InetAddress.getByName(nomServeur)
						.getHostAddress();
				System.out.println(addr);
				socket = new Socket(addr, Integer.parseInt(numPort));
				if (socket.isBound()) {
					testServer = false;
				} else
					testServer = true;
				etat = false;
				System.out.println(etat + " etat <-------------------");
			} catch (BindException e) {

				JOptionPane.showMessageDialog(null, "Le port " + numPort
						+ " est deja utilisé \n Choisissez a nouveau",
						"erreur", JOptionPane.INFORMATION_MESSAGE);
				System.out.println("port deja utiliser");
			} catch (ConnectException e) {
				// TODO Auto-generated catch block
				JOptionPane
						.showMessageDialog(null,
								"Le port choisi est inaccessible \n Choisissez a nouveau");

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				JOptionPane
						.showMessageDialog(null,
								"le serveur choisi est inaccessible \n Choisissez a noouveau");

			} catch (SocketException e) {
				// TODO Auto-generated catch block
				JOptionPane
						.showMessageDialog(null,
								"Le réseau choisi est inaccessible \n Choisissez a noouveau");

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

	public void play() throws Exception {
		String response;
		setIndexMsg(0);
		try {
			response = in.readLine();
			if (response.startsWith("DEBUT")) {
				char mark = response.charAt(6);
				icon = new ImageIcon(mark == '1' ? "img/p1.png" : "img/p2.png");
				opponentIcon = new ImageIcon(mark == '1' ? "img/p2.png"
						: "img/p1.png");
				winIcon = new ImageIcon("img/w.png");
				defeatIcon = new ImageIcon("img/d.png");
				frame.setTitle("Chasse au trésor - Joueur Num " + mark);
			}
			while (true) {
				response = in.readLine();
				System.out.println(response);
				if (response.startsWith("VALID_MOVE")) {
					messageLabel.setText("Attendre svp...");

					currentGrille.setIcon(icon);
					currentGrille.repaint();
				} else if (response.startsWith("OPPONENT_MOVED")) {
					loc = Integer.parseInt(response.substring(15));
					board[loc].setIcon(opponentIcon);
					board[loc].repaint();
					messageLabel
							.setText("Votre adversaire a joué, a toi le tour...");
				} else if (response.startsWith("VICTORY")) {
					currentGrille.setIcon(winIcon);
					currentGrille.repaint();
					messageLabel.setText("Bravo !! Trésor trouvé !!!...");
					windef = "Bravo !! Trésor trouvé !!!...";
					break;
				} else if (response.startsWith("DEFEAT")) {
					board[loc].setIcon(defeatIcon);
					board[loc].repaint();
					messageLabel.setText("Vous avez perdu !!!....");
					windef = "Vous avez perdu !!!....";
					System.out.println();
					break;
				} else if (response.startsWith("MESSAGE")) {
					messageLabel.setText(response.substring(8));
				} else if (response.startsWith("DIED")) {
					messageLabel
							.setText("Oups !!! Votre adversaire vient de se déconnecter !!!....");
					break;
				}
			}
			out.println("QUIT");
		} catch (SocketException e) {
			System.out.println("serveur deconnecter");
			messageLabel.setText("serveur deconnecter");
			setIndexMsg(1);
		} finally {
			socket.close();
		}
	}

	private boolean wantsToPlayAgain() {
		int pane = JOptionPane.OK_CANCEL_OPTION;
		int response = JOptionPane.showConfirmDialog(frame,
				message[getIndexMsg()], windef, JOptionPane.YES_NO_OPTION);
		frame.dispose();
		return response == JOptionPane.YES_OPTION;
	}

	public static void main(String[] args) throws Exception {

		try {

			while (true) {
				compteur++;
				System.out.println(compteur + "  <------------------------");
				Client client = new Client();
				client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				client.frame.setSize(548, 504);
				client.frame.setVisible(true);
				client.frame.setResizable(true);
				Dimension dimension = Toolkit.getDefaultToolkit()
						.getScreenSize();
				int x = (int) ((dimension.getWidth() - client.frame.getWidth()) / 2);
				int y = (int) ((dimension.getHeight() - client.frame
						.getHeight()) / 2);
				client.frame.setLocation(x, y);
				client.play();
				if (!client.wantsToPlayAgain()) {
					break;
				}
			}
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			JOptionPane
					.showMessageDialog(null, "le serveur n'est pas connecté");

		}
	}
}