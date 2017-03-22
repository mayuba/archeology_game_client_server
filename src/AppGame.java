import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class AppGame {
	// a board of 9 squares
	private Connect[] board = { null, null, null, null, null, null, null, null, null };

	// current player
	Connect currentPlayer;

	public void runplayer(ServerSocket listener) {
		
		try {
			Connect playerX = new Connect(listener.accept(), 'X');
			Connect playerO = new Connect(listener.accept(), 'O');
			playerX.setOpponent(playerO);
			playerO.setOpponent(playerX);
			currentPlayer = playerX;
			playerX.start();
			playerO.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// winner
	public boolean hasWinner() {
		return (getBoard()[0] != null && getBoard()[0] == getBoard()[1] && getBoard()[0] == getBoard()[2])
				|| (getBoard()[3] != null && getBoard()[3] == getBoard()[4] && getBoard()[3] == getBoard()[5])
				|| (getBoard()[6] != null && getBoard()[6] == getBoard()[7] && getBoard()[6] == getBoard()[8])
				|| (getBoard()[0] != null && getBoard()[0] == getBoard()[3] && getBoard()[0] == getBoard()[6])
				|| (getBoard()[1] != null && getBoard()[1] == getBoard()[4] && getBoard()[1] == getBoard()[7])
				|| (getBoard()[2] != null && getBoard()[2] == getBoard()[5] && getBoard()[2] == getBoard()[8])
				|| (getBoard()[0] != null && getBoard()[0] == getBoard()[4] && getBoard()[0] == getBoard()[8])
				|| (getBoard()[2] != null && getBoard()[2] == getBoard()[4] && getBoard()[2] == getBoard()[6]);
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
						// if (legalMove(location, this)) {
						if (this == currentPlayer && getBoard()[location] == null) {
							getBoard()[location] = currentPlayer;
							currentPlayer = currentPlayer.opponent;
							currentPlayer.otherPlayerMoved(location);

							output.println("VALID_MOVE");
							output.println(hasWinner() ? "VICTORY" : boardFilledUp() ? "TIE" : "");
						} else {
							output.println("MESSAGE ?");
						}
						// }
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