package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JPasswordField;

import servidor.PostItInterface;
import servidor.Registrador;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Login extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7699251602791922187L;
	private final JPanel contentPanel = new JPanel();
	private JTextField loginField;
	private JPasswordField passwordField;
	private PostItInterface pos;

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
	public Login(PostItInterface pos) {
		setBounds(0, 0, 450, 139);
		setLocationRelativeTo(null);
		setResizable(false);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		this.pos = pos;
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
							case 0: // Iniciar aplicação e fechar essa janela.
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
