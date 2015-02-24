package gui;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.swing.JLabel;

import servidor.ServerInterface;

import com.mongodb.DBObject;

/**
 * Class respons�vel pela interface gr�fica do aplicativo.
 * 
 * @author Ruriel
 *
 */
public class PostItGUI extends JFrame implements Runnable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3709822382907798283L;
	/**
	 * Painel que conter� os elementos.
	 */
	private JPanel contentPane;
	/**
	 * Interface a ser acessada remotamente.
	 */
	private ServerInterface pos;
	/**
	 * Lista de notas da sess�o.
	 */
	private ArrayList<PostIt> notas;
	/**
	 * Nome do usu�rio.
	 */
	private String usuario;
	/**
	 * Informa se o usu�rio � administrador ou n�o.
	 */
	private boolean isAdm;
	/**
	 * Pega a resolu��o do usu�rio para iniciar novas notas em posi��es
	 * consecutivas.
	 */
	private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private int width = 0;
	private int height = 0;

	/**
	 * Salva as notas da sess�o bem como suas modifica��es. Se a nota n�o
	 * estiver vis�vel, ser� apagada do banco.
	 */
	public void salvarNotas() {
		for (PostIt nota : notas) {
			try {
				if (!nota.isVisible())
					pos.deletarPostIt(nota.getId());
				else
					pos.salvarPostIt(nota.generateEntry(usuario));
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	/**
	 * Fecha todas as notas.
	 */
	public void fecharNotas() {
		for (PostIt nota : notas)
			nota.fechar();
	}

	/**
	 * Atualiza todas as notas de acordo com o que estiver no banco. Se houver
	 * uma nota ativa que n�o est� no banco, ser� fechada. Se tiver uma nota no
	 * banco que n�o est� presente, ser� instanciada.
	 */
	public void atualizarNotas() {
		ArrayList<DBObject> list = null;
		boolean isPresent;
		try {
			list = pos.iniciarPostIts(usuario);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(DBObject obj : list)
		{
			isPresent = false;
			for(PostIt minhaNota : notas)
			{
				if(minhaNota.getId().equals(obj.get("_id")))
				{
					minhaNota.atualizar(obj);
					isPresent = true;
					System.out.println(obj.get("_id"));
				}
			}
			if(!isPresent)
				notas.add(new PostIt(obj));
		}
		isPresent = false;
		for (PostIt minhaNota : notas) {
			isPresent = false;
			for (DBObject obj : list) 
			{
				if (minhaNota.getId().equals(obj.get("_id"))) 
					isPresent = true;
			}
			if (!isPresent)
				minhaNota.fechar();
		}
	}

	/**
	 * 
	 * Construtor da janela.
	 * 
	 * @param pos
	 *            Interface a ser utilizada.
	 * @param user
	 *            Nome de usu�rio.
	 * @param adm
	 *            Indica se o usu�rio � administrador ou n�o.
	 */
	public PostItGUI(ServerInterface pos, String user, boolean adm) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 450, 300);
		setLocationRelativeTo(null);
		setResizable(false);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(3, 2, 0, 0));

		notas = new ArrayList<PostIt>();
		this.usuario = user;
		this.isAdm = adm;
		JLabel lblUsuario = new JLabel("Logado como: " + user);
		contentPane.add(lblUsuario);

		JButton btnAtualizar = new JButton("Atualizar");
		/**
		 * A��o do bot�o Atualizar onde as informa��es das notas s�o
		 * atualizadas.
		 */
		btnAtualizar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				atualizarNotas();
			}
		});
		contentPane.add(btnAtualizar);
		JButton btnNovo = new JButton("Novo");
		/**
		 * A��o do bot�o Novo. Cria uma nota em posi��es consecutivas da tela.
		 */
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
		/**
		 * A��o do bot�o Salvar. Todas as notas da sess�o s�o salvas no banco.
		 */
		btnSalvar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				{
					salvarNotas();
				}
			}
		});
		contentPane.add(btnSalvar);

		JButton btnGerenciarUsuarios = new JButton("Gerenciar usu\u00E1rios");
		/**
		 * A��o do bot�o Gerenciar Usu�rios. Fecha a janela atual e abre a janela de ger�ncia
		 * de usu�rios.
		 */
		btnGerenciarUsuarios.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				UserManager u = new UserManager(usuario, pos, isAdm);
				EventQueue.invokeLater(u);
				dispose();
			}
		});
		contentPane.add(btnGerenciarUsuarios);
		JButton btnLogout = new JButton("Logout");
		/**
		 * A��o do bot�o Logout. Fecha a janela e reabre a janela de Login.
		 */
		btnLogout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Login dialog = new Login(pos);
				dialog.setVisible(true);
				dispose();
			}
		});
		contentPane.add(btnLogout);
		this.pos = pos;
		/**
		 * Listeners de evento desta janela.
		 */
		this.addWindowListener(new WindowListener() {

			@Override
			/**
			 * M�todo chamado automaticamente quando a janela � aberta pela primeira vez.
			 * Pesquisa e inicia cada nota presente no banco.
			 */
			public void windowOpened(WindowEvent e) {
				try {
					ArrayList<DBObject> objects = pos.iniciarPostIts(usuario);
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
			/**
			 * M�todo chamado ao fechar a janela. Salva e fecha todas as notas.
			 */
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
			public void windowDeactivated(WindowEvent e) {}
		});
	}
	
	@Override
	/**
	 * Torna a janela vis�vel.
	 */
	public void run() {
		setVisible(true);
	}
}
