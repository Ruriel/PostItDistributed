import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.Color;
import java.awt.ScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PostIt {

	private JFrame frame;
	private JPopupMenu menu = new JPopupMenu();
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PostIt window = new PostIt();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public PostIt() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(10, 10, 320, 240);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		
		JTextArea textArea = new JTextArea();
		JScrollPane scroll = new JScrollPane(textArea);
		textArea.setBackground(new Color(255, 255, 153));
		textArea.setBounds(0, 0, 320, 240);
		textArea.setFont(new Font("Times New Roman", Font.BOLD, 24));
		frame.getContentPane().add(scroll);
		PopupListener popuplistener = new PopupListener();
		textArea.addMouseListener(popuplistener);
		menu.add(new JMenuItem("teste"));
		
	}
	
	public class PopupListener extends MouseAdapter 
	{
	    public void mousePressed(MouseEvent e) 
	    {
	        maybeShowPopup(e);
	    }

	    public void mouseReleased(MouseEvent e) 
	    {
	        maybeShowPopup(e);
	    }

	    private void maybeShowPopup(MouseEvent e) 
	    {
	        if (e.isPopupTrigger()) 
	        {
	            menu.show(e.getComponent(), e.getX(), e.getY());
	        }
	    }
	}
}
