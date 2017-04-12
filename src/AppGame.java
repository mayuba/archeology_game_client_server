import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.Color;

public class AppGame {
	// a board of 16 squares
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
	Connect currentPlayer;

	public void MESSAGE(String message) {

	}

	/**
	 * @wbp.parser.entryPoint
	 */
	public void runplayer(ServerSocket listener) {

		try {

			Connect player1 = new Connect(listener.accept(), '1');
			Connect player2 = new Connect(listener.accept(), '2');
			player1.setOpponent(player2);
			player2.setOpponent(player1);
			currentPlayer = player1;
			setTresors(generateTresors());
			System.out.println("tttttttttttttttttttttttttttttttttttttt ->" + getTresors());
			player1.start();
			player2.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// winner
	public boolean hasWinner() {
		return (getBoard()[getTresors()] != null);
	}

	// no empty squares
	public boolean boardFilledUp() {
		for (int i = 0; i < getBoard().length; i++) {
			if (getBoard()[i] == null) {
				return false;
			}
		}
		return true;
	}

	// thread when player tries a move
	public synchronized boolean legalMove(int location, Connect player) {
		if (player == currentPlayer && getBoard()[location] == null) {
			getBoard()[location] = currentPlayer;
			currentPlayer = currentPlayer.opponent;
			currentPlayer.otherPlayerMoved(location);
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

		char mark;
		Connect opponent;
		Socket socket;
		BufferedReader input;
		PrintWriter output;
		// current player

		// thread handler to initialize stream fields
		public Connect(Socket socket, char mark) {
			this.socket = socket;
			this.mark = mark;
			try {
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream(), true);
				output.println("DEBUT " + mark);
				output.println("MESSAGE Vous etes connecté...Veuillez attendre un adversaire svp....");
			} catch (IOException e) {
				// output.println("DIED" + mark);

			}
		}

		// Accepts notification of who the opponent is.
		public void setOpponent(Connect opponent) {
			this.opponent = opponent;
		}

		// Handles the otherPlayerMoved message.
		public void otherPlayerMoved(int location) {
			output.println("OPPONENT_MOVED " + location);
			output.println(hasWinner() ? "DEFEAT" : boardFilledUp() ? "TIE" : "");
		}

		public void run() {
			try {
				// The thread is only started after everyone connects.
				output.println("MESSAGE Votre adversaire est connecté. La chasse peut commencer...");

				// Tell the first player that it is his/her turn.
				if (mark == 'X') {
					output.println("MESSAGE A vous le tour....");
				}

				// Repeatedly get commands from the client and process them.
				while (true) {
					String command = input.readLine();
					System.out.println(socket.getChannel());
					System.out.println(socket.getLocalSocketAddress());
					System.out.println(socket.getInetAddress());

					if (command.startsWith("MOVE")) {
						int location = Integer.parseInt(command.substring(5));
						// if (legalMove(location, this)) {
						if (this == currentPlayer && getBoard()[location] == null) {
							getBoard()[location] = currentPlayer;
							System.out.println(currentPlayer + " AVANT");
							System.out.println(currentPlayer.opponent + " opponent");
							currentPlayer = currentPlayer.opponent;
							currentPlayer.otherPlayerMoved(location);
							System.out.println(currentPlayer.getName() + " APRES");
							output.println("VALID_MOVE");
							output.println(hasWinner() ? "VICTORY" : boardFilledUp() ? "TIE" : "");

						} else if (this == currentPlayer && getBoard()[location] != null) {
							output.println("MESSAGE La grille est deja utiliser....");
						} else {
							output.println("MESSAGE Veuillez attendre votre tour svp....");
						}
						// }
					} else if (command.startsWith("QUIT")) {
						return;
					}
				}
			} catch (SocketException e) {
				currentPlayer = currentPlayer.opponent;
				output.println("DIED");
				System.out.println("---------------------------------");
				System.out.println("player current = " + currentPlayer);
				System.out.println("Votre adversaire s'est déconnecté: " + e);
				System.out.println("Player died: " + e);
				System.out.println("---------------------------------");
			} catch (IOException e) {
				System.out.println("Votre adversaire s'est déconnecté: " + e);
				System.out.println("Player died: " + e);
			} finally {
				try {
					socket.close();
				} catch (IOException e) {
				}
			}
		}
	}
}