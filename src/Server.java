import java.net.InetAddress;
import java.net.ServerSocket;

import javax.swing.JFrame;
import javax.swing.JOptionPane; 

public class Server { 
	  
    /**
     * @wbp.parser.entryPoint
     */
    public static void main(String[] args) throws Exception {
    	String numPort = JOptionPane.showInputDialog(null, "Veuillez entré le numero de port ","Connecter le server",
				JOptionPane.QUESTION_MESSAGE);
        ServerSocket listener = new ServerSocket(Integer.parseInt(numPort));
       
        try {
            while (true) {
            	System.out.println(listener.getInetAddress());
            	AppGame Game = new AppGame(); 
                Game.runplayer(listener);
                
            }
        } finally {
            listener.close();
        }
    }
    
    
   
}


