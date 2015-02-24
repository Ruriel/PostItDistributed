package servidor;

import java.rmi.*;

/**
 * Registrador que implementa uma Thread. Responsável por registrar localmente o Servidor.
 * 
 * @author Ruriel
 *
 */
public class Registrador implements Runnable {

@Override
	public void run() {
		try {

			Servidor obj = new Servidor();
			Naming.rebind("rmi://localhost/PostItDistributed", obj);

		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Servidor registrado!");
		
	}

}