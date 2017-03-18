import java.awt.Color;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

// Graphical Grille in the client window.
	public   class Grille extends JPanel {
		JLabel label = new JLabel((Icon) null);

		public Grille() {
			setBackground(Color.white); 
			add(label);
		}

		public void setIcon(Icon icon) {
			label.setIcon(icon);
		}
	}