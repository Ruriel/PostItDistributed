package gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

import servidor.ServerInterface;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;

public class Login extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7699251602791922187L;
	private final JPanel contentPanel = new JPanel();
	private JTextField loginField;
	private JPasswordField passwordField;

	/**
	 * Launch the application.
	 * 
	 * @throws NotBoundException
	 * @throws RemoteException
	 * @throws MalformedURLException
	 */

	/**
	 * Create the dialog.
	 */
	public Login(ServerInterface pos) {
		setBounds(0, 0, 450, 139);
		setLocationRelativeTo(null);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		{
			JLabel lblLogin = new JLabel("Login");
			lblLogin.setBounds(139, 14, 42, 14);
			contentPanel.add(lblLogin);

			JLabel lblSenha = new JLabel("Senha");
			lblSenha.setBounds(139, 39, 42, 14);
			contentPanel.add(lblSenha);
		}
		{
			passwordField = new JPasswordField();
			passwordField.setBounds(191, 39, 96, 20);
			contentPanel.add(passwordField);
		}
		{
			loginField = new JTextField();
			loginField.setBounds(191, 11, 96, 20);
			contentPanel.add(loginField);
			loginField.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 66, 434, 33);
			contentPanel.add(buttonPane);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int result = -1;
						if (!loginField.getText().equals("")
								&& !passwordField.getPassword().equals("")) {
							try {
								result = pos.login(loginField.getText(),
										new String(passwordField.getPassword()));
							} catch (RemoteException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							switch (result) {
							case 0: // Iniciar aplica��o e fechar essa janela.
								EventQueue.invokeLater(new PostItGUI(pos,
										loginField.getText(), true));
								dispose();
								break;
							case 1:
								EventQueue.invokeLater(new PostItGUI(pos,
										loginField.getText(), false));
								dispose();
								break;
							case 2:
								JOptionPane.showMessageDialog(null,
										"Senha incorreta.", "Erro de Login",
										JOptionPane.ERROR_MESSAGE);
								break;
							case 3:
								JOptionPane.showMessageDialog(null,
										"Login inexistente.", "Erro de Login",
										JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				});
				
				JButton btnRegistrar = new JButton("Registrar");
				btnRegistrar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						UsernameWindow euw = new UsernameWindow("Registrar novo usu�rio");
						int option = -2;
						boolean usuarioCriado = false;
						while (!usuarioCriado && option != 1 && option != -1) {
							option = euw.showWindow();
							if (option == 0) {
								if (euw.getUsername().equals("")
										|| euw.getPassword().equals(""))
									JOptionPane.showMessageDialog(null,
											"N�o pode haver campos em branco!", "Erro",
											JOptionPane.ERROR_MESSAGE);
								else {
									try {
										if(pos.contarAdministradores() < 1)
											usuarioCriado = pos.criarUsuario(euw.getUsername(), euw.getPassword(), true);
										else
											usuarioCriado = pos.criarUsuario(euw.getUsername(), euw.getPassword(), false);
									} catch (RemoteException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									if (usuarioCriado)
										JOptionPane.showMessageDialog(null,
												"Usu�rio Criado com sucesso!",
												"Usu�rio criado",
												JOptionPane.INFORMATION_MESSAGE);

									else
										JOptionPane.showMessageDialog(null,
												"Usu�rio j� existente!", "Erro",
												JOptionPane.ERROR_MESSAGE);
								}
							}
						}

					}
				});
				buttonPane.add(btnRegistrar);
				okButton.setActionCommand("Entrar");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Sair");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
}
