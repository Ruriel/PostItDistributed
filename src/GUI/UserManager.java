package GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.GridLayout;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.BoxLayout;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;

import servidor.PostItInterface;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class UserManager extends JFrame implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2161940218558434953L;
	private JPanel contentPane;
	private DBObject usuario;
	private PostItInterface pos;
	private ArrayList<DBObject> users;
	private JList<String> list;

	/**
	 * Launch the application.
	 */
	public void atualizar() {
		DefaultListModel model = new DefaultListModel();
		try {
			users = pos.listarUsuarios();
		} catch (RemoteException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
		for (DBObject obj : users) {
			String userName = (String) obj.get("user");
			if ((boolean) obj.get("adm"))
				userName = "(adm) " + userName;
			model.addElement(userName);
		}
		list.setModel(model);
	}

	
	@Override
	public void run() {
		setVisible(true);
	}

	/**
	 * Create the frame.
	 * 
	 * @throws RemoteException
	 */
	public UserManager(DBObject user, PostItInterface pos) {
		this.usuario = user;
		this.pos = pos;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JScrollPane scrollPane = new JScrollPane();

		list = new JList();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(list);
		setResizable(false);
		JButton btnAdicionar = new JButton("Adicionar");
		btnAdicionar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				UsernameWindow euw = new UsernameWindow("Adicionar Usuário", (boolean)usuario.get("adm"));
				int option = -2;
				boolean usuarioCriado = false;
				while(!usuarioCriado && option != 1 && option != -1)
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
								usuarioCriado = pos.criarUsuario(euw.getUsername(), euw.getPassword(), euw.isAdm());
							
							}catch (RemoteException e1) {
							// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if(usuarioCriado)
								JOptionPane.showMessageDialog(null, "Usuário Criado com sucesso!", "Usuário criado", 
									JOptionPane.INFORMATION_MESSAGE);
						
							else
								JOptionPane.showMessageDialog(null, "Usuário já existente!", "Erro", 
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});

		JButton btnExcluir = new JButton("Excluir");
		btnExcluir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] options = { "Sim", "Não" };
				String selected = list.getSelectedValue().replace("(adm) ", "");
				
					int option = JOptionPane.showOptionDialog(null,
						"Excluir usuário " + list.getSelectedValue() + "?",
						"Confirmação", JOptionPane.DEFAULT_OPTION,
						JOptionPane.WARNING_MESSAGE, null, options, options[1]);
					if(option == 0)
					{
						
						try {
							pos.deletarUsuario(selected);
						} catch (RemoteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					}
			}
		});
		JButton btnSair = new JButton("Sair");
		btnSair.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		JButton btnAtualizar = new JButton("Atualizar");
		btnAtualizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				atualizar();
			}
		});
		
		JButton btnEditar = new JButton("Editar");
		btnEditar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DBObject editUser = null;
				UsernameWindow euw = null;
				String userName;
				String password;
				boolean isAdm;
				try {
					editUser = pos.buscarUsuario(list.getSelectedValue().replace("(adm) ", ""));
				} catch (RemoteException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				userName = (String)editUser.get("user");
				password = (String)editUser.get("password");
				isAdm = (boolean)editUser.get("adm");
				try {
					euw = new UsernameWindow("Editar Usuário", userName, 
							password, isAdm, !pos.hasOnlyOneAdm() || !isAdm && (boolean)usuario.get("adm"));
				} catch (RemoteException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				int option = -2;
				while(option == -2)
				{
					option = euw.showWindow();
					if(option == 0)
					{
						if(euw.getPassword().equals("") || euw.getUsername().equals(""))
						{
							JOptionPane.showMessageDialog(null, "Não pode haver campos em branco!", "Erro", JOptionPane.ERROR_MESSAGE);
							euw.setAdm(isAdm);
							euw.setPassword(password);
							euw.setUsername(userName);
							option = -2;
						}
						else
						{
							try {
								pos.atualizarUsuario((String)editUser.get("user"), euw.getUsername(), euw.getPassword(), euw.isAdm());
								if(userName.equals((String)usuario.get("user")))
								{
									usuario.put("user", euw.getUsername());
									usuario.put("password", euw.getPassword());
									usuario.put("adm", euw.isAdm());
								}
									
							}catch (RemoteException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							JOptionPane.showMessageDialog(null, "Usuário atualizado com sucesso!", "Usuário criado", 
								JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGap(5)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 218, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addGroup(Alignment.LEADING, gl_contentPane.createSequentialGroup()
							.addComponent(btnAdicionar, GroupLayout.DEFAULT_SIZE, 92, Short.MAX_VALUE)
							.addContainerGap())
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
								.addComponent(btnSair, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
								.addComponent(btnEditar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
								.addComponent(btnAtualizar, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
								.addComponent(btnExcluir, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addContainerGap())))
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 251, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addGap(47)
							.addComponent(btnAdicionar)
							.addGap(4)
							.addComponent(btnExcluir)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnAtualizar)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnEditar)
							.addGap(8)
							.addComponent(btnSair)))
					.addContainerGap(10, Short.MAX_VALUE))
		);
		contentPane.setLayout(gl_contentPane);
		list.addListSelectionListener(new ListSelectionListener()
		{

			@Override
			public void valueChanged(ListSelectionEvent e)
			{
				if(list.getSelectedValue() == null)
				{
					btnExcluir.setEnabled(false);
					btnEditar.setEnabled(false);
				}
				else
				{
					if((boolean)user.get("adm"))
					{
						btnEditar.setEnabled(true);
						if(list.getSelectedValue().endsWith((String)usuario.get("user")))
							btnExcluir.setEnabled(false);
						else
							btnExcluir.setEnabled(true);
					}
					else
					{
						btnExcluir.setEnabled(false);
						if(list.getSelectedValue().equals((String)usuario.get("user")))
							btnEditar.setEnabled(true);
						else
							btnEditar.setEnabled(false);
					}
				}
			}
		});
		this.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				atualizar();
				if(list.getSelectedValue() == null)
				{
					btnExcluir.setEnabled(false);
					btnEditar.setEnabled(false);
				}
			}

			@Override
			public void windowClosed(WindowEvent e) {
				EventQueue.invokeLater(new PostItGUI(pos, usuario));
			}
			@Override
			public void windowActivated(WindowEvent e) {
				atualizar();
			}
			@Override
			public void windowClosing(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
		});
	}
}
