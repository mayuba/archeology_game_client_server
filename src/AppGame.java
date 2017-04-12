import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

public class AppGame {
	// Tableau de 49 cases
	private Connect[] board = { null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null };

	private int tresors;

	public int getTresors() {
		return tresors;
	}

	public void setTresors(int tresors) {
		this.tresors = tresors;
	}

	public int generateTresors() {
		Random rnd = new Random();
		return rnd.nextInt(48);

	}

	// Joueur courant
	Connect JoueurCourant;

	public void MESSAGE(String message) {

	}

	public void runplayer(ServerSocket listener) {

		try {

			Connect player1 = new Connect(listener.accept(), '1');
			Connect player2 = new Connect(listener.accept(), '2');
			player1.setOpponent(player2);
			player2.setOpponent(player1);
			JoueurCourant = player1;
			setTresors(generateTresors());
			System.out
					.println("------------------------------>" + getTresors());
			player1.start();
			player2.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Gagnant
	public boolean hasWinner() {
		return (getBoard()[getTresors()] != null);
	}

	public boolean boardFilledUp() {
		for (int i = 0; i < getBoard().length; i++) {
			if (getBoard()[i] == null) {
				return false;
			}
		}
		return true;
	}

	public synchronized boolean legalMove(int location, Connect player) {
		if (player == JoueurCourant && getBoard()[location] == null) {
			getBoard()[location] = JoueurCourant;
			JoueurCourant = JoueurCourant.Adversaire;
			JoueurCourant.otherPlayerMoved(location);
			return true;
		}
		return false;
	}

	public Connect[] getBoard() {
		return board;
	}

	public void setBoard(Connect[] board) {
		this.board = board;
	}

	class Connect extends Thread {

		char croix;
		Connect Adversaire;
		Socket socket;
		BufferedReader input;
		PrintWriter output;

		public Connect(Socket socket, char croix) {
			this.socket = socket;
			this.croix = croix;
			try {
				input = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream(), true);
				output.println("DEBUT " + croix);
				output.println("MESSAGE Vous etes connecté...Veuillez attendre un adversaire svp....");
			} catch (IOException e) {

			}
		}

		public void setOpponent(Connect Adversaire) {
			this.Adversaire = Adversaire;
		}

		public void otherPlayerMoved(int location) {
			output.println("Adversaire_Joue " + location);
			output.println(hasWinner() ? "Defaite" : boardFilledUp() ? "Remise"
					: "");
		}

		public void run() {
			try {

				output.println("MESSAGE Votre adversaire est connecté. La chasse peut commencer...");

				if (croix == 'X') {
					output.println("MESSAGE A vous le tour....");
				}

				while (true) {
					String command = input.readLine();
					System.out.println(socket.getChannel());
					System.out.println(socket.getLocalSocketAddress());
					System.out.println(socket.getInetAddress());

					if (command.startsWith("MOVE")) {
						int location = Integer.parseInt(command.substring(5));
						if (this == JoueurCourant
								&& getBoard()[location] == null) {
							getBoard()[location] = JoueurCourant;
							System.out.println(JoueurCourant + " AVANT");
							System.out.println(JoueurCourant.Adversaire
									+ " Adversaire");
							JoueurCourant = JoueurCourant.Adversaire;
							JoueurCourant.otherPlayerMoved(location);
							System.out.println(JoueurCourant.getName()
									+ " APRES");
							output.println("VALID_MOVE");
							output.println(hasWinner() ? "Victoire"
									: boardFilledUp() ? "Remise" : "");

						} else if (this == JoueurCourant
								&& getBoard()[location] != null) {
							output.println("MESSAGE La grille est deja utilisée....");
						} else {
							output.println("MESSAGE Veuillez attendre votre tour svp....");
						}
						// }
					} else if (command.startsWith("QUIT")) {
						return;
					}
				}
			} catch (SocketException e) {
				JoueurCourant = JoueurCourant.Adversaire;
				output.println("Mort");
				System.out.println("---------------------------------");
				System.out.println("Joueur Courant = " + JoueurCourant);
				System.out.println("Votre adversaire s'est déconnecté: " + e);
				System.out.println("Joueur mort: " + e);
				System.out.println("---------------------------------");
			} catch (IOException e) {
				System.out.println("Votre adversaire s'est déconnecté: " + e);
				System.out.println("Joueur mort: " + e);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}
}