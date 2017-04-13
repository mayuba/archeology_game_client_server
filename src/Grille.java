import java.awt.Color;
import javax.swing.JPanel;

// Interface grille de jeu Client
public class Grille extends JPanel {

	private static final long serialVersionUID = -8690423967928771895L;
	JPanel label = new JPanel();

	public Grille() {
		setBackground(Color.white);
		add(label);
	}

	public void setColor(Color color) {
		setBackground(color);

	}
}