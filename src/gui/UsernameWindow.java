package gui;


import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Classe responsável por criar a caixa de diálogo que pede o nome de usuário e senha para
 * adicionar ou editar usuários no banco.
 * @author Ruriel
 *
 */
public class UsernameWindow 
{
	/**
	 * Painel.
	 */
	JPanel panel = new JPanel();
	/**
	 * Checkbox que indica se o usuário tem permissão de administrador ou não.
	 */
	JCheckBox checkAdm = null;
	/**
	 * Nome dos botões.
	 */
	Object[] options = { "OK", "Cancelar"};
	String title;
	JTextField userName = new JTextField(20);
	JPasswordField password = new JPasswordField(20);
	
	/**
	 * Constrói a janela.
	 * @param titulo Título da janela.
	 * @param login Nome de usuário a aparecer no campo de login.
	 * @param senha Senha a aparecer no campo de senha.
	 * @param isAdm Permissão de administrador. Indica se a checkbox estará marcada ou não.
	 * @param enableChkAdm Indica se a checkbox deva estar ativada ou não.
	 */
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
		
		/**
		 * Checa se os valores booleanos não são nulos. Se forem, a checkbox estará ausente.
		 * Caso contrário, a checkbox estará ativada ou habilitada de acordo com a entrada.
		 */
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
	/**
	 * Cria uma janela sem a checkbox.
	 * @param titulo Título da janela.
	 */
	public UsernameWindow(String titulo)
	{
		this(titulo, "", "", null, null);
	}
	/**
	 * Cria uma janela com a checkbox deselecionada.
	 * @param titulo Título da janela.
	 * @param isAdm Entrada que indica se o botão deve ser habilitado ou não.
	 */
	public UsernameWindow(String titulo, boolean isAdm)
	{
		this(titulo, "", "", false, isAdm);
	}
	
	
	/**
	 * Mostra a janela e retorna a opção selecionada pelo usuário.
	 * @return 0 para OK, 1 para Cancelar e -1 caso a janela tenha sido fechada.
	 */
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