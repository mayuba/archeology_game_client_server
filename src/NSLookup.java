import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
 
/**
 * Trouver l'adresse IP à partir d'un domaine ou d'un nom de poste
 */
public class NSLookup {
 
    public static String IPAddress(String hostname) {
        InetAddress inetHost = null;
        try {
            inetHost = InetAddress.getByName(hostname);
        } catch (UnknownHostException ex) {
            Logger.getLogger(NSLookup.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (inetHost == null) {
            return "Invalid hostname: "+hostname;
        } else {
            return inetHost.getHostAddress();
        }
    }
 
    /**
     * Exemple 
     */
    public static void main(String[] args) {
        //Trouver l'IP du serveur web google.com
        System.out.println("IP du serveur 'www.google.com': " + NSLookup.IPAddress("google.com"));
        //Connaitre l'IP du poste 'leserveur' sur un réseau local
        System.out.println("IP du poste 'leserveur' sur le LAN: " + NSLookup.IPAddress("leserveur"));
    }
}