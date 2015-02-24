package servidor;

import gui.Login;

import java.awt.EventQueue;
import java.rmi.Naming;

/**
 * Classe que contém o método main.
 * @author Ruriel
 *
 */
public class Main {

	/**
	 * Método main. Inicia uma uma busca pela interface. Se não encontrar, inicia um novo
	 * registrador. Após isso, o programa inicia.
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
				System.out.println("Servidor não registrado. Iniciando novo registrador.");
				EventQueue.invokeLater(new Registrador());
			}
			
		} while (pos == null);
		Login dialog = new Login(pos);
		dialog.setVisible(true);
	}

}
