import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class AppGame {
	// a board of 9 squares
	private Connect[] board = { null, null, null, null, null, null, null, null, null };

	// current player
	Connect currentPlayer;

	// winner
	public boolean hasWinner() {
		return (board[0] != null && board[0] == board[1] && board[0] == board[2])
				|| (board[3] != null && board[3] == board[4] && board[3] == board[5])
				|| (board[6] != null && board[6] == board[7] && board[6] == board[8])
				|| (board[0] != null && board[0] == board[3] && board[0] == board[6])
				|| (board[1] != null && board[1] == board[4] && board[1] == board[7])
				|| (board[2] != null && board[2] == board[5] && board[2] == board[8])
				|| (board[0] != null && board[0] == board[4] && board[0] == board[8])
				|| (board[2] != null && board[2] == board[4] && board[2] == board[6]);
	}

	// no empty squares
	public boolean boardFilledUp() {
		for (int i = 0; i < board.length; i++) {
			if (board[i] == null) {
				return false;
			}
		}
		return true;
	}

	// thread when player tries a move
	public synchronized boolean legalMove(int location, Connect player) {
		if (player == currentPlayer && board[location] == null) {
			board[location] = currentPlayer;
			currentPlayer = currentPlayer.opponent;
			currentPlayer.otherPlayerMoved(location);
			return true;
		}
		return false;
	}

	class Connect extends Thread {
		char mark;
		Connect opponent;
		Socket socket;
		BufferedReader input;
		PrintWriter output;

		// thread handler to initialize stream fields
		public Connect(Socket socket, char mark) {
			this.socket = socket;
			this.mark = mark;
			try {
				input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				output = new PrintWriter(socket.getOutputStream(), true);
				output.println("WELCOME " + mark);
				output.println("MESSAGE Waiting for opponent to connect");
			} catch (IOException e) {
				System.out.println("Player died: " + e);
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
				output.println("MESSAGE All players connected");

				// Tell the first player that it is his/her turn.
				if (mark == 'X') {
					output.println("MESSAGE Your move");
				}

				// Repeatedly get commands from the client and process them.
				while (true) {
					String command = input.readLine();
					if (command.startsWith("MOVE")) {
						int location = Integer.parseInt(command.substring(5));
						//if (legalMove(location, this)) {
							if (this == currentPlayer && board[location] == null) {
								board[location] = currentPlayer;
								currentPlayer = currentPlayer.opponent;
								currentPlayer.otherPlayerMoved(location);

								output.println("VALID_MOVE");
								output.println(hasWinner() ? "VICTORY" : boardFilledUp() ? "TIE" : "");
							} else {
								output.println("MESSAGE ?");
							}
					//	}
					} else if (command.startsWith("QUIT")) {
						return;
					}
				}
			} catch (IOException e) {
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