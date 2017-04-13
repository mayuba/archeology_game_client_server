import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

public class AppGame {
	// Grille de 49 cases
	private Connect[] board = { null, null, null, null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
			null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null };

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

	// current player
	Connect JoueurCourant;

	public void MESSAGE(String message) {

	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void runplayer(ServerSocket listener) {

		try {

			Connect player1 = new Connect(listener.accept(), '1');
			Connect player2 = new Connect(listener.accept(), '2');
			player1.setAdvers(player2);
			player2.setAdvers(player1);
			JoueurCourant = player1;
			setTresors(generateTresors());
			System.out.println("------------------------->" + getTresors());
			player1.start();
			player2.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean winner() {
		return (getBoard()[getTresors()] != null);
	}

	public boolean initGrille() {
		for (int i = 0; i < getBoard().length; i++) {
			if (getBoard()[i] == null) {
				return false;
			}
		}
		return true;
	}

	public Connect[] getBoard() {
		return board;
	}

	public void setBoard(Connect[] board) {
		this.board = board;
	}

	class Connect extends Thread {

		char mark;
		Connect opponent;
		Socket socket;
		BufferedReader input;
		PrintWriter output;

		public Connect(Socket socket, char mark) {
			this.socket = socket;
			this.mark = mark;
			try {
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream(), true);
				output.println("DEBUT " + mark);
				output.println("MESSAGE Vous etes connecté...Veuillez attendre un adversaire svp....");
			} catch (IOException e) {

			}
		}

		public void setAdvers(Connect opponent) {
			this.opponent = opponent;
		}

		public void adversMoved(int location) {
			output.println("OPPONENT_MOVED " + location);
			output.println(winner() ? "DEFEAT" : initGrille() ? "TIE" : "");
		}

		public void run() {
			try {

				output.println("MESSAGE Votre adversaire est connecté. La chasse peut commencer...");

				if (mark == '1') {
					output.println("MESSAGE A vous le tour....");
				}

				while (true) {
					String command = input.readLine();
					System.out.println(socket.getChannel());
					System.out.println(socket.getLocalSocketAddress());
					System.out.println(socket.getInetAddress());

					if (command.startsWith("MOVE")) {
						int location = Integer.parseInt(command.substring(5));

						if (this == JoueurCourant && getBoard()[location] == null) {
							getBoard()[location] = JoueurCourant;
							System.out.println(JoueurCourant + " AVANT");
							System.out.println(JoueurCourant.opponent + " opponent");
							JoueurCourant = JoueurCourant.opponent;
							JoueurCourant.adversMoved(location);
							System.out.println(JoueurCourant.getName() + " APRES");
							output.println("VALID_MOVE");
							output.println(winner() ? "VICTORY" : initGrille() ? "TIE" : "");

						} else if (this == JoueurCourant && getBoard()[location] != null) {
							output.println("MESSAGE La grille est deja utiliser....");
						} else if (JoueurCourant == null) {
							System.out.println("probleme");
							output.println("DIED");
						} else {
							output.println("MESSAGE Veuillez attendre votre tour svp....");
						}

					} else if (command.startsWith("test") && JoueurCourant == null) {
						output.println("DIED");
					} else if (command.startsWith("QUIT")) {
						return;
					}
				}
			} catch (

			SocketException e)

			{
				JoueurCourant.interrupt();
				System.out.println("j1 " + JoueurCourant);
				JoueurCourant = null;
				System.out.println("j2 " + JoueurCourant.isAlive());
				output.println("DIED");
				System.out.println("---------------------------------");
				System.out.println("player current = " + JoueurCourant);
				System.out.println("Votre adversaire s'est déconnecté: " + e);
				System.out.println("Player died: " + e);
				System.out.println("---------------------------------");

			} catch (

			IOException e)

			{
				System.out.println("Votre adversaire s'est déconnecté: " + e);
				System.out.println("Player died: " + e);
			} finally

			{
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}
}