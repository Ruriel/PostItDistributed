package GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JLabel;

import servidor.PostItInterface;

import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class PostItGUI extends JFrame implements Runnable {

	private JPanel contentPane;
	private PostItInterface pos;
	private ArrayList<PostIt> notas;
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int width = 0;
	private int height = 0;
	private DBObject usuario = null;
	/**
	 * Create the frame.
	 */

	public void salvarNotas()
	{
		for (PostIt nota : notas) {
			try {
				if(!nota.isVisible())
					pos.deletarPostIt(nota.getId());
				else
					pos.salvarPostIt(nota.generateEntry((String)usuario.get("user")));
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	public void fecharNotas()
	{
		for (PostIt nota : notas) 
			nota.fechar();
	}
	
	public PostItGUI(PostItInterface pos)
	{
		this(pos, null);
	}
	public PostItGUI(PostItInterface pos, DBObject user) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 450, 300);
		setLocationRelativeTo(null);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(3, 2, 0, 0));

		notas = new ArrayList<PostIt>();
		usuario = user;
		JButton btnAtualizar = new JButton("Atualizar");
		btnAtualizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				ArrayList<DBObject> list = null;
				boolean isPresent;
				try {
					list = pos.buscarPostIt((String)usuario.get("user"));
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for(PostIt minhaNota : notas)
				{
					isPresent = false;
					for(DBObject obj : list)
					{
						if(minhaNota.getId().equals(obj.get("_id")))
						{
							minhaNota.atualizar(obj);
							isPresent = true;
						}
					}
					if(!isPresent)
						minhaNota.fechar();
				}
			}
		});
		contentPane.add(btnAtualizar);
		JButton btnNovo = new JButton("Novo");
		btnNovo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PostIt novoPostIt = new PostIt(width, height);
				notas.add(novoPostIt);
				EventQueue.invokeLater(novoPostIt);
				width += 320;
				if (width >= screenSize.width) {
					width = 0;
					height += 240;
					if (height >= screenSize.height)
						height = 0;
				}
			}
		});
		contentPane.add(btnNovo);
		
				JButton btnSalvar = new JButton("Salvar");
				btnSalvar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) 
					{
						{
							salvarNotas();
						}
					}
				});
				contentPane.add(btnSalvar);

		JButton btnGerenciarUsuarios = new JButton("Gerenciar usu\u00E1rios");
		btnGerenciarUsuarios.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				UserManager u = new UserManager(usuario, pos);
				EventQueue.invokeLater(u);
				dispose();
			}
		});
		contentPane.add(btnGerenciarUsuarios);
				JButton btnLogout = new JButton("Logout");
				btnLogout.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						reiniciar();
						dispose();
					}
				});
				contentPane.add(btnLogout);
		this.pos = pos;
		this.addWindowListener(new WindowListener()
		{

			@Override
			public void windowOpened(WindowEvent e) 
			{
				try {
					ArrayList<DBObject> objects = pos.iniciarPostIts((String)usuario.get("user"));
					for (DBObject obj : objects) {
						PostIt minhaNota = new PostIt(obj);
						notas.add(minhaNota);
						EventQueue.invokeLater(minhaNota);
					}
				} catch (RemoteException ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}
				
			}
			@Override
			public void windowClosing(WindowEvent e) {}
			@Override
			public void windowClosed(WindowEvent e) {
				salvarNotas();
				fecharNotas();
			}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e){}
		});
	}

	public PostIt buscarNota(String id)
	{
		for(PostIt nota : notas)
		{
			if(nota.getId().toString().equals(id))
			{
				return nota;
			}
		}
		return null;
	}
	private void reiniciar()
	{
		UsernameWindow euw = new UsernameWindow();
		int option = -2;
		usuario = null;
		while(usuario == null)
		{
			option = euw.showWindow();
			if(option == 0)
			{
				if(euw.getUsername().equals("") || euw.getPassword().equals(""))
					JOptionPane.showMessageDialog(null, "Não pode haver campos em branco!", "Erro", 
							JOptionPane.ERROR_MESSAGE);
				else
				{
					try {
						usuario = pos.login(euw.getUsername(), euw.getPassword());
					
					}catch (RemoteException e1) {
					// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					if(usuario == null)
					{
						JOptionPane.showMessageDialog(null, "Nome de usuário ou senha incorretos!", "Erro", 
								JOptionPane.ERROR_MESSAGE);
					}
					else
					{
						String welcome = (String) usuario.get("user");
						welcome = (boolean) usuario.get("adm") == true ? "(adm) " + welcome : welcome; 
						JLabel lblUsuario = new JLabel("Logado como: " + welcome);
						contentPane.add(lblUsuario);
						setVisible(true);
					}
				}
			}
			else
				System.exit(0);
		}
	}
	@Override
	public void run() {
		reiniciar();
	}
}
