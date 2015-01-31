import java.awt.EventQueue;

import javax.swing.JFrame;


public class Main {

	public static void main(String[] args) throws InterruptedException {
		Runnable postit = new PostIt();
		postit = new PostIt();
		EventQueue.invokeLater(postit);
		Thread.sleep(5000);
		System.out.println(((PostIt) postit).generateJson("Ruriel").toString());
	}

}
