import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import java.awt.Color;
import java.awt.ScrollPane;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;

public class PostIt {

	private JFrame frame;
	private JPopupMenu menu = new JPopupMenu();
	private Color amarelo = new Color(252, 250, 176);
	private Color azul = new Color(197, 228, 246);
	private Color verde = new Color(187, 239, 183);
	private Color rosa = new Color(238, 184, 238);
	private Color roxo = new Color(214, 208, 254);
	private Color branco = new Color(254, 254, 254);
	private JMenuItem blue = new JMenuItem("Azul");
	private JMenuItem yellow = new JMenuItem("Amarelo");
	private JMenuItem pink = new JMenuItem("Rosa");
	private JMenuItem green = new JMenuItem("Verde");
	private JMenuItem purple = new JMenuItem("Roxo");
	private JMenuItem white = new JMenuItem("Branco");
	private JMenuItem copy = new JMenuItem("Copiar");
	private JMenuItem cut = new JMenuItem("Recortar");
	private JMenuItem paste = new JMenuItem("Colar");
	private JMenuItem delete = new JMenuItem("Excluir");
	private JMenuItem select = new JMenuItem("Selecionar Tudo");
	private Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	private JTextArea textArea;
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

	private void initializeSubmenu()
	{
		menu.add(copy);
		menu.add(paste);
		menu.add(cut);
		menu.add(delete);
		menu.addSeparator();
		menu.add(select);
		menu.addSeparator();
		menu.add(blue);
		menu.add(yellow);
		menu.add(pink);
		menu.add(green);
		menu.add(purple);
		menu.add(white);
		
		clipboard.addFlavorListener(new FlavorListener()
		{

			@Override
			public void flavorsChanged(FlavorEvent arg0) {
				if(arg0.getSource() == null)
					paste.setEnabled(false);
				else
					paste.setEnabled(true);
			}
			
		});
		paste.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				textArea.paste();
			}
		});
		delete.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				String selectedText = textArea.getSelectedText();
				textArea.setText(textArea.getText().replace(selectedText, ""));
			}
		});
		copy.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				textArea.copy();
			}
		});
		cut.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				textArea.cut();
			}
		});
		select.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				textArea.selectAll();
			}
		});
		blue.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				textArea.setBackground(azul);
			}
		});
		pink.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				textArea.setBackground(rosa);
			}
		});
		yellow.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				textArea.setBackground(amarelo);
			}
		});
		green.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				textArea.setBackground(verde);
			}
		});
		purple.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				textArea.setBackground(roxo);
			}
		});
		white.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				textArea.setBackground(branco);
			}
		});
		textArea.addCaretListener(new CaretListener()
		{
			@Override
			public void caretUpdate(CaretEvent arg0) {
				int dot = arg0.getDot();
				int mark = arg0.getMark();
				if(dot == mark)
				{
					delete.setEnabled(false);
					copy.setEnabled(false);
					cut.setEnabled(false);
					select.setEnabled(true);
				}
				else
				{
					delete.setEnabled(true);
					copy.setEnabled(true);
					cut.setEnabled(true);
					select.setEnabled(false);
					
				}
				
			}
		});
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(10, 10, 320, 240);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		
		textArea = new JTextArea();
		JScrollPane scroll = new JScrollPane(textArea);
		textArea.setBackground(amarelo);
		textArea.setBounds(0, 0, 320, 240);
		textArea.setFont(new Font("Times New Roman", Font.BOLD, 24));
		frame.getContentPane().add(scroll);
		PopupListener popuplistener = new PopupListener();
		textArea.addMouseListener(popuplistener);
		initializeSubmenu();
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
