package gui;

import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import servidor.PostItInterface;

public class UsernameWindow 
{
	JPanel panel = new JPanel();
	JCheckBox checkAdm = null;
	Object[] options = { "OK", "Cancel"};
	String title;
	JTextField userName = new JTextField(20);
	JPasswordField password = new JPasswordField(20);
	public UsernameWindow()
	{
		this("Login", "", "", null, null);
	}
	public UsernameWindow(String titulo, boolean isAdm)
	{
		this(titulo, "", "", false, isAdm);
	}
	
	public UsernameWindow(String titulo, String login, String senha, Object isAdm, Object enableChkAdm)
	{
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		userName.setText(login);
		password.setText(senha);
		
		panel.add(new JLabel("Login"));
		panel.add(userName);
		panel.add(new JLabel("Password"));
		panel.add(password);
		title = titulo;
			
		if(isAdm != null && enableChkAdm != null)
		{
			checkAdm = new JCheckBox("Administrador");
			checkAdm.setEnabled((boolean)enableChkAdm);
			checkAdm.setSelected((boolean)isAdm);
			options = new Object[3];
			options[2] = checkAdm;
		}
		else
			options = new Object[2];
		options[0] = "OK";
		options[1] = "Cancelar";
	}
	
	public int showWindow()
	{
		return JOptionPane.showOptionDialog(null, panel,
				title, JOptionPane.OK_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		
	}
	
	
	public String getUsername()
	{
		return userName.getText();
	}
	
	public String getPassword()
	{
		return new String(password.getPassword());
	}
	
	public boolean isAdm()
	{
		return checkAdm.isSelected();
	}

	public void setUsername(String name)
	{
		userName.setText(name);
	}
	
	public void setPassword(String pass)
	{
		password.setText(pass);
	}
	
	public void setAdm(boolean adm)
	{
		checkAdm.setSelected(adm);
	}
}