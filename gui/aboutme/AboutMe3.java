import javax.swing.*;        
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class AboutMe3 extends JFrame implements ActionListener {
	JTextField name;
	JTextArea biography;
	JButton portrait;

	public AboutMe3() {
		super("About me");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new FlowLayout());

		add(new JLabel("Name"));
		name = new JTextField();
		name.setPreferredSize(new Dimension(450, 20));
		add(name);

		add(new JLabel("Biography"));
		biography = new JTextArea();
		biography.setPreferredSize(new Dimension(450, 150));
		add(biography);

		add(new JLabel("Portrait"));
		ImageIcon image = new ImageIcon("bilder/skate.jpg");
		portrait = new JButton(image);
		portrait.addActionListener(this);
		add(portrait);


	}

	public void actionPerformed(ActionEvent e) {
		JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(this);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			ImageIcon image = new ImageIcon(file.getPath());
			portrait.setIcon(image);
		}
	}

	public static void main(String[] args) {
		AboutMe3 a = new AboutMe3();
		a.pack();
		a.setSize(new Dimension(550, 800));
		a.setVisible(true);
	}
}
