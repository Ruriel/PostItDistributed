package cliente;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.swing.JDialog;
import javax.swing.JFrame;

import org.bson.types.ObjectId;

import servidor.Registrador;
import servidor.Servidor;
import Interface.PostIt;
import Interface.PostItInterface;

public class Main {

	public static void main(String[] args) 
	{
		PostItInterface pos = null;
		Registry reg = null;

		do {
			try

			{
				//reg = LocateRegistry.getRegistry(1900);
				System.out.println("Iniciando...");
				pos = (PostItInterface) Naming.lookup("rmi://localhost/PostItDistributed");
			} catch (Exception e) {
				System.out.println("Servidor não registrado. Iniciando novo registrador.");
				EventQueue.invokeLater(new Registrador());
				
			}
		} while (pos == null);
		Login dialog = new Login(pos);
		dialog.setVisible(true);

	}

}
