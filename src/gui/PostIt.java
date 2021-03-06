package gui;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import java.awt.Color;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
/**
 * Classe respons�vel pela cria��o da janela da nota. A nota � um textarea onde � poss�vel
 * copiar, colar, recortar, selecionar tudo e mudar a cor de fundo.
 * @author Ruriel
 *
 */
public class PostIt implements Runnable{

	private JFrame frame;
	private JPopupMenu menu;
	private static final Color AMARELO = new Color(252, 250, 176);
	private static final Color AZUL = new Color(197, 228, 246);
	private static final Color VERDE = new Color(187, 239, 183);
	private static final Color ROSA = new Color(238, 184, 238);
	private static final Color ROXO = new Color(214, 208, 254);
	private static final Color BRANCO = new Color(254, 254, 254);
	private JMenuItem blue;
	private JMenuItem yellow;
	private JMenuItem pink;
	private JMenuItem green;
	private JMenuItem purple;
	private JMenuItem white;
	private JMenuItem copy;
	private JMenuItem cut;
	private JMenuItem paste;
	private JMenuItem delete;
	private JMenuItem select;
	private JTextArea textArea;
	private PopupListener popuplistener;
	private ObjectId _id;
	
	/**
	 * Construtor onde gera-se a nota a partir de um objeto do banco. Caso o objeto seja nulo,
	 * uma nota amarela de dimens�es 320x240 e vazia ser� instanciada.
	 * @param entry
	 */
	public PostIt(DBObject entry) {
		super();
		
		frame = new JFrame();
		textArea = new JTextArea();
		JScrollPane scroll = new JScrollPane(textArea);
		
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(scroll);
		
		popuplistener = new PopupListener();
		textArea.addMouseListener(popuplistener);
		initializeSubmenu();
		if(entry != null)
		{
			BasicDBObject rectangle = (BasicDBObject) entry.get("rectangle");
			int x = rectangle.getInt("x");
			int y = rectangle.getInt("y");
			int width = rectangle.getInt("width");
			int height = rectangle.getInt("height");
			frame.setBounds(new Rectangle(x, y, width, height));
			textArea.setBackground(new Color((int) entry.get("background")));
			textArea.setText(entry.get("text").toString());
			_id = (ObjectId)(entry.get("_id"));
		}
		else
			textArea.setBackground(AMARELO);
		textArea.setBounds(frame.getBounds());
		textArea.setFont(new Font("Times New Roman", Font.BOLD, 24));
		
	}
	
	/**
	 * Cria uma nota na posi��o especificada.
	 * @param x Indica a posi��o horizontal da janela.
	 * @param y Indica a posi��o vertical da janela.
	 */
	public PostIt(int x, int y)
	{
		this(null);
		frame.setBounds(x, y, 320, 240);
	}
	
	public ObjectId getId()
	{
		return _id;
	}

	/**
	 * Inicializa o submenu.
	 */
 	private void initializeSubmenu()
	{
		menu = new JPopupMenu();
		blue = new JMenuItem("Azul");
		yellow = new JMenuItem("Amarelo");
		pink = new JMenuItem("Rosa");
		green = new JMenuItem("Verde");
		purple = new JMenuItem("Roxo");
		white = new JMenuItem("Branco");
		copy = new JMenuItem("Copiar");
		cut = new JMenuItem("Recortar");
		paste = new JMenuItem("Colar");
		delete = new JMenuItem("Excluir");
		select = new JMenuItem("Selecionar Tudo");
		
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
		Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(new FlavorListener()
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
				textArea.setBackground(AZUL);
			}
		});
		pink.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				textArea.setBackground(ROSA);
			}
		});
		yellow.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				textArea.setBackground(AMARELO);
			}
		});
		green.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				textArea.setBackground(VERDE);
			}
		});
		purple.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				textArea.setBackground(ROXO);
			}
		});
		white.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent evt)
			{
				textArea.setBackground(BRANCO);
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
	
 	@Override
	public void run() {
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
 	/**
 	 * Gera um objeto de banco a partir da configura��o da janela e o nome do usu�rio.
 	 * A configura��o consiste em posi��es horizontal e vertical, altura e largura, cor de fundo,
 	 * texto e data.
 	 * @param user Nome do usu�rio
 	 * @return Um objeto de banco contendo as informa��es da janela.
 	 */
	public BasicDBObject generateEntry(CharSequence user)
	{
		if(_id == null)
			_id = new ObjectId();
		BasicDBObject entry = new BasicDBObject("_id" , _id);
		BasicDBObject rectangle = new BasicDBObject();
		entry.append("user", user);
		rectangle.append("x", frame.getX());
		rectangle.append("y", frame.getY());
		rectangle.append("width", frame.getWidth());
		rectangle.append("height", frame.getHeight());
		entry.append("rectangle", rectangle);
		entry.append("background", textArea.getBackground().getRGB());
		entry.append("text", textArea.getText());
		entry.append("date", new Date().toString());
		System.out.println(entry.toString());
		return entry;
	}
	
	public void fechar()
	{
		frame.dispose();
	}
	
	public boolean isVisible()
	{
		return frame.isVisible();
	}
	
	/**
	 * Atualiza a nota de acordo com objeto.
	 * @param entry Objeto que cont�m as mudan�as a serem efetuadas.
	 */
	public void atualizar(DBObject entry)
	{
		BasicDBObject rectangle = (BasicDBObject) entry.get("rectangle");
		int x = rectangle.getInt("x");
		int y = rectangle.getInt("y");
		int width = rectangle.getInt("width");
		int height = rectangle.getInt("height");
		frame.setBounds(new Rectangle(x, y, width, height));
		textArea.setBackground(new Color((int) entry.get("background")));
		textArea.setText(entry.get("text").toString());
		_id = (ObjectId)(entry.get("_id"));
	}
	
	/**
	 * Listener para mostrar o submenu quando o bot�o direito for pressionado.
	 * @author Ruriel
	 *
	 */
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
