package test;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import model.automata.acceptors.fsa.FiniteStateAcceptor;
import universe.JFLAPUniverse;
import universe.preferences.JFLAPPreferences;
import view.automata.views.FSAView;
import view.environment.JFLAPEnvironment;

public class IntersectionTest {

	@Test
	public void intersectionTest1() {
		File f1 = new File("files/intersection/intTest1.jflap");
		File f2 = new File("files/intersection/intTest2.jflap");

		if (JFLAPUniverse.registerEnvironment(f1))
			JFLAPPreferences.addRecentlyOpenend(f1);

		if (JFLAPUniverse.registerEnvironment(f2))
			JFLAPPreferences.addRecentlyOpenend(f2);

		try {
		    Thread.sleep(5000);
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		JFLAPEnvironment out = JFLAPUniverse.getActiveEnvironment();
		FiniteStateAcceptor out_fsa = ((FSAView) out.getPrimaryView()).getDefinition();
		
		File f3 = new File("files/intersection/intOutput.jflap");
		if (JFLAPUniverse.registerEnvironment(f3))
			JFLAPPreferences.addRecentlyOpenend(f3);
		
		JFLAPEnvironment act_out = JFLAPUniverse.getActiveEnvironment();
		FiniteStateAcceptor act_out_fsa =((FSAView) act_out.getPrimaryView()).getDefinition();
		
		assertEquals("Output of intersection should be same as expected output", out_fsa, act_out_fsa);
	}

}
