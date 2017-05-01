package oldnewstuff.main;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * This is the class that starts JFLAP.
 * 
 * @author Thomas Finley
 * @author Moti Ben-Ari All code moved to gui.Main Parameter dontQuit false for
 *         command line invocation
 */

public class JFLAP {
	public static void main(String[] args) {
		try {
			// UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
			// MetalLookAndFeel.setCurrentTheme(new );
			for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		oldnewstuff.main.Main.main(args, false);
	}
}
