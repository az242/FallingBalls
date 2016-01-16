package Client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class ClientRun {
	static Client g=new Client();
	public static void main(String args[]){
		g.init();
		JFrame frame = new JFrame();
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.add(g);
	    frame.setBounds(10, 10, 1020, 520);
	    frame.setVisible(true);
	    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    frame.setResizable(false);
	    frame.addWindowListener(new WindowAdapter()
	    { // anonymous inner class
	       public void windowClosing(WindowEvent e)
	       {
	          g.destroy();
	          System.exit(0);
	       }
	    });
	}
	
}
