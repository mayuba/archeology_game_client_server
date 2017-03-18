import java.net.ServerSocket;

public class Server {

    public static void main(String[] args) throws Exception {
        ServerSocket listener = new ServerSocket(8901);
        System.out.println("Tic Tac Toe Server is Running");
        try {
            while (true) {
                AppGame AppGame = new AppGame(); 
                AppGame.Connect playerX = AppGame.new Connect(listener.accept(), 'X');
                AppGame.Connect playerO = AppGame.new Connect(listener.accept(), 'O');
                playerX.setOpponent(playerO);
                playerO.setOpponent(playerX);
                AppGame.currentPlayer = playerX;
                playerX.start();
                playerO.start();
            }
        } finally {
            listener.close();
        }
    }
    
    
   
}


