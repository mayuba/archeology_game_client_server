import java.net.ServerSocket;

public class Server {
    private static final int PORT = 9001;
    public static void main(String[] args) throws Exception {
        ServerSocket listener = new ServerSocket(8901);
        System.out.println("Tic Tac Toe Server is Running");
        System.out.println("The chat server is running.");
            ServerSocket listenerchat = new ServerSocket(PORT);
        try {
            while (true) {
                AppGame Game = new AppGame();
                Game.runplayer(listener);
                new Handler(listenerchat.accept()).start();
            }
            
          
        } finally {
            listener.close();  listenerchat.close();
        }
    }
    
    
   
}


