package view.action.automata;

import java.awt.event.ActionEvent;

import debug.JFLAPDebug;
import model.algorithms.transform.fsa.InvertAutomataConverter;
import model.algorithms.transform.fsa.NFAtoDFAConverter;
import model.automata.acceptors.fsa.FSATransition;
import model.automata.acceptors.fsa.FiniteStateAcceptor;
import universe.JFLAPUniverse;
import view.algorithms.transform.InvertedAutomatonPanel;
import view.algorithms.transform.NFAtoDFAPanel;
import view.automata.editing.AutomatonEditorPanel;
import view.automata.views.AutomatonView;
import view.automata.views.FSAView;

public class InvertAutomataAction extends AutomatonAction {

	public InvertAutomataAction(FSAView view) {
		super("Invert Automata", view);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		InvertAutomataConverter convert = new InvertAutomataConverter(((FiniteStateAcceptor) getAutomaton()).copy());
		InvertedAutomatonPanel panel = new InvertedAutomatonPanel(getEditorPanel(), convert);

		JFLAPUniverse.getActiveEnvironment().addSelectedComponent(panel);
	}

}
