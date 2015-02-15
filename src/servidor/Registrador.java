package servidor;

import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import Interface.PostItInterface;

public class Registrador implements Runnable {

@Override
	public void run() {
		try {

			Servidor obj = new Servidor();
			//UnicastRemoteObject.unexportObject(obj, true);
			//PostItInterface stub = (PostItInterface) UnicastRemoteObject.exportObject(obj, 0);
			//Registry reg = LocateRegistry.createRegistry(1900);
			Naming.rebind("rmi://localhost/PostItDistributed", obj);

			System.out.println("Servidor Registrado!");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
