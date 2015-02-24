package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JButton;

import com.mongodb.DBObject;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.ListSelectionModel;

import servidor.ServerInterface;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
/**
 * Janela de gerenciador de usuário.
 * @author Ruriel
 *
 */
public class UserManager extends JFrame implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2161940218558434953L;
	private JPanel contentPane;
	private String usuario;
	private ServerInterface pos;
	private ArrayList<DBObject> users;
	private JList<String> list;
	private boolean isAdm;

	/**
	 * Lê todos os usuários registrados e os coloca na JList.
	 */
	public void atualizar() {
		DefaultListModel<String> model = new DefaultListModel<String>();
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
	public UserManager(String user, ServerInterface pos, boolean adm) {
		this.usuario = user;
		this.pos = pos;
		isAdm = adm;
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);

		JScrollPane scrollPane = new JScrollPane();

		list = new JList<String>();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(list);
		setResizable(false);
		JButton btnAdicionar = new JButton("Adicionar");
		/**
		 * Ação do botão Adicionar. Abre uma janela solicitando o nome, senha e permissão 
		 * do novo usuário. 
		 */
		btnAdicionar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UsernameWindow euw = new UsernameWindow("Adicionar Usuário",
						isAdm);
				int option = -2;
				boolean usuarioCriado = false;
				/**
				 * Janela permanecerá aparacendo até um usuário válido seja adicionado ou até
				 * a janela ser fechada.
				 */
				while (!usuarioCriado && option != 1 && option != -1) {
					option = euw.showWindow();
					if (option == 0) {
						if (euw.getUsername().equals("")
								|| euw.getPassword().equals(""))
							JOptionPane.showMessageDialog(null,
									"Não pode haver campos em branco!", "Erro",
									JOptionPane.ERROR_MESSAGE);
						else {
							try {
								usuarioCriado = pos.criarUsuario(
										euw.getUsername(), euw.getPassword(),
										euw.isAdm());

							} catch (RemoteException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if (usuarioCriado)
								JOptionPane.showMessageDialog(null,
										"Usuário Criado com sucesso!",
										"Usuário criado",
										JOptionPane.INFORMATION_MESSAGE);

							else
								JOptionPane.showMessageDialog(null,
										"Usuário já existente!", "Erro",
										JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});

		JButton btnExcluir = new JButton("Excluir");
		/**
		 * Evento do botão Excluir. Mostra uma caixa de confirmação. Se o usuário confirmar, o
		 * usuário selecionado na lista será apagado do banco.
		 */
		btnExcluir.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String[] options = { "Sim", "Não" };
				String selected = list.getSelectedValue();

				int option = JOptionPane.showOptionDialog(null,
						"Excluir usuário " + list.getSelectedValue() + "?",
						"Confirmação", JOptionPane.DEFAULT_OPTION,
						JOptionPane.WARNING_MESSAGE, null, options, options[1]);
				if (option == 0) {
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
		/**
		 * Evento do botão Editar. Cria uma janela contendo as informações do usuário e pronta
		 * para modificação de dados.
		 */
		JButton btnEditar = new JButton("Editar");
		btnEditar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DBObject editUser = null;
				UsernameWindow euw = null;
				String editUsername;
				String editPassword;
				boolean editAdm;
				boolean habilitarChk;
				try {
					editUser = pos.buscarUsuario(list.getSelectedValue()
							.replace("(adm) ", ""));
				} catch (RemoteException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				editUsername = (String) editUser.get("user");
				editPassword = (String) editUser.get("password");
				editAdm = (boolean) editUser.get("adm");
				try {

					/**
					 * Desabilita a checkbox caso haja somente um administrador.
					 */
					habilitarChk = pos.contarAdministradores() > 1 && editAdm || isAdm;
					euw = new UsernameWindow("Editar Usuário", editUsername,
							editPassword, editAdm, habilitarChk);
				} catch (RemoteException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				int option = -2;
				while (option == -2) {
					option = euw.showWindow();
					if (option == 0) {
						if (euw.getPassword().equals("")
								|| euw.getUsername().equals("")) {
							JOptionPane.showMessageDialog(null,
									"Não pode haver campos em branco!", "Erro",
									JOptionPane.ERROR_MESSAGE);
							euw.setAdm(editAdm);
							euw.setPassword(editPassword);
							euw.setUsername(editUsername);
							option = -2;
						} else {
							try {
								pos.atualizarUsuario(list.getSelectedValue()
										.replace("(adm) ", ""), euw
										.getUsername(), euw.getPassword(), euw
										.isAdm());
								if (user.equals(editUsername)) {
									isAdm = euw.isAdm();
									usuario = euw.getUsername();

								}
							} catch (RemoteException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							JOptionPane.showMessageDialog(null,
									"Usuário atualizado com sucesso!",
									"Usuário criado",
									JOptionPane.INFORMATION_MESSAGE);
						}
					}
				}
			}
		});
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane
				.setHorizontalGroup(gl_contentPane
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_contentPane
										.createSequentialGroup()
										.addGap(5)
										.addComponent(scrollPane,
												GroupLayout.PREFERRED_SIZE,
												218, GroupLayout.PREFERRED_SIZE)
										.addPreferredGap(
												ComponentPlacement.RELATED)
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.TRAILING)
														.addGroup(
																Alignment.LEADING,
																gl_contentPane
																		.createSequentialGroup()
																		.addComponent(
																				btnAdicionar,
																				GroupLayout.DEFAULT_SIZE,
																				92,
																				Short.MAX_VALUE)
																		.addContainerGap())
														.addGroup(
																gl_contentPane
																		.createSequentialGroup()
																		.addGroup(
																				gl_contentPane
																						.createParallelGroup(
																								Alignment.TRAILING)
																						.addComponent(
																								btnSair,
																								Alignment.LEADING,
																								GroupLayout.DEFAULT_SIZE,
																								195,
																								Short.MAX_VALUE)
																						.addComponent(
																								btnEditar,
																								Alignment.LEADING,
																								GroupLayout.DEFAULT_SIZE,
																								195,
																								Short.MAX_VALUE)
																						.addComponent(
																								btnAtualizar,
																								Alignment.LEADING,
																								GroupLayout.DEFAULT_SIZE,
																								195,
																								Short.MAX_VALUE)
																						.addComponent(
																								btnExcluir,
																								GroupLayout.DEFAULT_SIZE,
																								GroupLayout.DEFAULT_SIZE,
																								Short.MAX_VALUE))
																		.addContainerGap()))));
		gl_contentPane
				.setVerticalGroup(gl_contentPane
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_contentPane
										.createSequentialGroup()
										.addGroup(
												gl_contentPane
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(
																scrollPane,
																GroupLayout.PREFERRED_SIZE,
																251,
																GroupLayout.PREFERRED_SIZE)
														.addGroup(
																gl_contentPane
																		.createSequentialGroup()
																		.addGap(47)
																		.addComponent(
																				btnAdicionar)
																		.addGap(4)
																		.addComponent(
																				btnExcluir)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				btnAtualizar)
																		.addPreferredGap(
																				ComponentPlacement.RELATED)
																		.addComponent(
																				btnEditar)
																		.addGap(8)
																		.addComponent(
																				btnSair)))
										.addContainerGap(10, Short.MAX_VALUE)));
		contentPane.setLayout(gl_contentPane);
		/**
		 * Listeners da JList.
		 */
		list.addListSelectionListener(new ListSelectionListener() {

			@Override
			/**
			 * Garante que um item não nulo esteja selecionado. Também desabilita os botões
			 * Editar e Excluir dependendo da permissão do usuário. Usuários comuns só podem editar
			 * a si mesmos enquanto administradores podem editar qualquer um.
			 * Exclusões só podem ser feitas por administradores. Mesmo assim, nenhum pode se exclui.
			 */
			public void valueChanged(ListSelectionEvent e) {
				if (list.getSelectedValue() == null)
					list.setSelectedIndex(0);
				if (adm) {
					if (list.getSelectedValue().endsWith(usuario)) {
						btnExcluir.setEnabled(false);
					} else {
						btnExcluir.setEnabled(true);
					}
				} else {
					btnExcluir.setEnabled(false);
					if (list.getSelectedValue().equals(usuario))
						btnEditar.setEnabled(true);
					else
						btnEditar.setEnabled(false);
				}
			}
		});
		this.addWindowListener(new WindowListener() {

			@Override
			/**
			 * Atualiza a janela ao abrí-la pela primeiravez.
			 */
			public void windowOpened(WindowEvent e) {
				atualizar();
				list.setSelectedIndex(0);
			}

			@Override
			public void windowClosing(WindowEvent e) {}

			@Override
			/**
			 * Chama a interface principal ao fechar esta janela.
			 */
			public void windowClosed(WindowEvent e) {
				EventQueue.invokeLater(new PostItGUI(pos, usuario, isAdm));
			}

			@Override
			public void windowIconified(WindowEvent e) {}

			@Override
			public void windowDeiconified(WindowEvent e) {}

			@Override
			/**
			 * Método chamado ao focar na janela. Atualiza a lista.
			 */
			public void windowActivated(WindowEvent e) {
				atualizar();
			}

			@Override
			public void windowDeactivated(WindowEvent e) {}

		});
	}
}
