import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Rectangle;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

import javax.swing.JFrame;

import org.bson.types.ObjectId;


public class Main {

	public static void main(String[] args) throws InterruptedException, RemoteException, UnknownHostException {
		Server server = new Server();
		ObjectId id = new ObjectId("54ceb40bf454e80f1e61c399");
		PostIt a = server.iniciarPostIt(id);
		EventQueue.invokeLater(a);
		//Thread.sleep(5000);
		//server.salvarPostIt("Ruriel", a);
		//EventQueue.invokeLater(server.iniciarPostIt(id));
	}

}
