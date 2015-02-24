package servidor;

import gui.Login;

import java.awt.EventQueue;
import java.rmi.Naming;

/**
 * Classe que cont�m o m�todo main.
 * @author Ruriel
 *
 */
public class Main {

	/**
	 * M�todo main. Inicia uma uma busca pela interface. Se n�o encontrar, inicia um novo
	 * registrador. Ap�s isso, o programa inicia.
	 * @param args
	 */
	public static void main(String[] args) 
	{
		ServerInterface pos = null;
		System.out.println("Iniciando...");
		
		do {
			try

			{
				pos = (ServerInterface) Naming.lookup("rmi://localhost/PostItDistributed");
			} catch (Exception e) {
				System.out.println("Servidor n�o registrado. Iniciando novo registrador.");
				EventQueue.invokeLater(new Registrador());
			}
			
		} while (pos == null);
		Login dialog = new Login(pos);
		dialog.setVisible(true);
	}

}
