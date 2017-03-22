import java.net.ServerSocket;

public class Server {

    public static void main(String[] args) throws Exception {
        ServerSocket listener = new ServerSocket(8901);
        System.out.println("Tic Tac Toe Server is Running");
        try {
            while (true) {
                AppGame Game = new AppGame();
                Game.runplayer(listener);
            }
        } finally {
            listener.close();
        }
    }
    
    
   
}

