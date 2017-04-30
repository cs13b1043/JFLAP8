package view.action.automata;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;

import file.xml.graph.AutomatonEditorData;
import model.automata.Automaton;
import model.automata.AutomatonException;
import model.automata.State;
import model.automata.Transition;
import model.automata.TransitionSet;
import model.automata.acceptors.Acceptor;
import model.automata.acceptors.fsa.FSATransition;
import model.automata.transducers.moore.MooreMachine;
import model.automata.turing.MultiTapeTuringMachine;
import model.automata.turing.buildingblock.Block;
import model.automata.turing.buildingblock.BlockTuringMachine;
import model.graph.BlockTMGraph;
import model.graph.TransitionGraph;
import model.symbols.Symbol;
import model.symbols.SymbolString;
import universe.JFLAPUniverse;
import view.ViewFactory;
import view.automata.Note;
import view.automata.editing.AutomatonEditorPanel;
import view.automata.editing.BlockEditorPanel;
import view.automata.editing.MooreEditorPanel;
import view.automata.views.AutomatonView;
import view.environment.JFLAPEnvironment;

public class IntersectionAutomataAction extends AutomatonAction {

	public IntersectionAutomataAction(AutomatonView view) {
		super("Intersection of Automata", view);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		AutomatonEditorPanel panel = getEditorPanel();
		Automaton auto = panel.getAutomaton();

		JFLAPEnvironment[] enviros = JFLAPUniverse.getRegistry().toArray(new JFLAPEnvironment[0]);
		JFLAPEnvironment active = JFLAPUniverse.getActiveEnvironment();
		JComboBox<JFLAPEnvironment> combo = new JComboBox<JFLAPEnvironment>();

		for (int i = 0; i < enviros.length; i++) {
			JFLAPEnvironment env = enviros[i];
			Component primary = env.getPrimaryView();

			if (!env.equals(active) && isValid(auto, primary))
				combo.addItem(env);
		}

		if (combo.getItemCount() == 0)
			throw new AutomatonException("No other automata of this type around!");

		// Prompt the user.
		int result = JOptionPane.showOptionDialog(active, combo, "Combine Two", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, null, null);
		if (result != JOptionPane.OK_OPTION)
			return;

		AutomatonView otherView = (AutomatonView) ((JFLAPEnvironment) combo.getSelectedItem()).getPrimaryView();
		AutomatonEditorData other = new AutomatonEditorData((AutomatonEditorPanel) otherView.getCentralPanel());
		AutomatonEditorData copy = new AutomatonEditorData(panel);

		add(copy, other);
	}

	/**
	 * Appends other to the <CODE>newOne</CODE> automaton.
	 * 
	 * @param newOne
	 */
	private void add(AutomatonEditorData newOne, AutomatonEditorData other) {
		AutomatonView view = ViewFactory.createAutomataView(newOne);
		AutomatonEditorPanel panel = (AutomatonEditorPanel) view.getCentralPanel();
		Automaton auto = panel.getAutomaton();

		TransitionGraph oGraph = other.getGraph();
		Automaton oAuto = oGraph.getAutomaton();

		Map<State, State> stateMapping = new TreeMap<State, State>();

		addStates(panel, oGraph, stateMapping);

		JFLAPUniverse.registerEnvironment(view);
	}

	public void addNotes(AutomatonEditorData other, AutomatonEditorPanel panel, Map<State, State> stateMapping) {
		Map<Point2D, String> oNotes = other.getNotes();

		for (Point2D p : oNotes.keySet()) {
			panel.addNote(new Note(panel, new Point((int) p.getX(), (int) p.getY()), oNotes.get(p)));
		}

		Map<Point2D, String> oLabels = other.getLabels();

		for (Point2D p : oLabels.keySet()) {
			State s = (State) other.getGraph().vertexForPoint(p);

			panel.addStateLabel(stateMapping.get(s), new Note(panel, new Point((int) p.getX(), (int) p.getY())),
					oLabels.get(p));
		}
	}

	public void addTransitions(AutomatonEditorPanel panel, TransitionGraph oGraph, Map<State, State> stateMapping) {
		Automaton auto = panel.getAutomaton();
		Automaton oAuto = oGraph.getAutomaton();

		TransitionSet<? extends Transition<?>> oTransitions = oAuto.getTransitions();

		for (Transition t : oTransitions) {
			State from = stateMapping.get(t.getFromState());
			State to = stateMapping.get(t.getToState());
			auto.getTransitions().add(t.copy(from, to));
		}

		// For some reason, you can't adjust the control points until all
		// transitions are added.
		for (Transition t : oTransitions) {
			State from = stateMapping.get(t.getFromState());
			State to = stateMapping.get(t.getToState());
			Point2D ctrl = oGraph.getControlPt(t);
			panel.moveCtrlPoint(from, to, ctrl);
		}
	}

	public void addStates(AutomatonEditorPanel panel, TransitionGraph oGraph, Map<State, State> stateMapping) {
		Automaton auto = panel.getAutomaton();
		Automaton oAuto = oGraph.getAutomaton();

		State oldStart1 = auto.getStartState();
		State oldStart2 = oAuto.getStartState();


		// int i=0;
		State qi = oldStart1;

		// int j=0;
		State qj = oldStart2;
		boolean b = false;

		int c = 0;

		List<State> bfsStates_i = new ArrayList<>();
		List<State> bfsStates_j = new ArrayList<>();
		Map<String, State> newStateLabels = new HashMap<>();

		bfsStates_i.add(null);
		while (!bfsStates_i.isEmpty()) {
			if (c == 0)
				bfsStates_i.remove(0);// dummy state
			Set T1 = auto.getTransitions().getTransitionsFromState(qi);
			Set T2 = oAuto.getTransitions().getTransitionsFromState(qj);

			Iterator<FSATransition> itr1 = T1.iterator();

			FSATransition t1 = null;
			FSATransition t2 = null;

			while (itr1.hasNext()) {
				t1 = itr1.next();
				for (Symbol symbol1 : t1.getInput()) {
					String x1 = symbol1.getString();
					Iterator<FSATransition> itr2 = T2.iterator();
					while (itr2.hasNext()) {
						t2 = itr2.next();
						for (Symbol symbol : t2.getInput()) {
							String x2 = symbol.getString();
							if (x1.equals(x2)) {
								b = true;
								// create transaction qij to qab
								c++;

								String s1Name = qi.getName() + qj.getName();

								State newState1 = newStateLabels.get(s1Name);
								if (!newStateLabels.containsKey(s1Name)) {
									int x4 = (int) ((panel.getPointForVertex(qi).getX()
											+ oGraph.pointForVertex(qj).getX()) / 2);
									int y4 = (int) ((panel.getPointForVertex(qi).getY()
											+ oGraph.pointForVertex(qj).getY()) / 2);
									newState1 = panel.createState(new Point(x4, y4));
									newState1.setName(s1Name);
									newStateLabels.put(s1Name, newState1);

									if (qi != oldStart1 || qj != oldStart2) {
										bfsStates_i.add(qi);
										bfsStates_j.add(qj);
									}
									else{
										auto.setStartState(newState1);
									}
								}

								String s2Name = t1.getToState().getName() + t2.getToState().getName();

								State newState2 = newStateLabels.get(s2Name);
								if (!newStateLabels.containsKey(s2Name)) {
									int x3 = (int) ((panel.getPointForVertex(t1.getToState()).getX()
											+ oGraph.pointForVertex(t2.getToState()).getX()) / 2);
									int y3 = (int) ((panel.getPointForVertex(t1.getToState()).getY()
											+ oGraph.pointForVertex(t2.getToState()).getY()) / 2);

									newState2 = panel.createState(new Point(x3, y3));
									newState2.setName(s2Name);
									newStateLabels.put(s2Name, newState2);

									bfsStates_i.add(t1.getToState());
									bfsStates_j.add(t2.getToState());
								}
								// qij x qab;

								FSATransition newTransition = (FSATransition) panel.createTransition(newState1,
										newState2);
								newTransition.setInput(new SymbolString(new Symbol(x1)));
								auto.getTransitions().add(newTransition);

								// if(a is final in 1 and b is final in 2)
								if (((Acceptor) auto).getFinalStateSet().contains(t1.getToState())
										&& ((Acceptor) oAuto).getFinalStateSet().contains(t2.getToState())) {
									// qab is final
									((Acceptor) auto).getFinalStateSet().add(newState2);
								}

							}
						}
					}
				}
			}

			qi = bfsStates_i.remove(0);
			qj = bfsStates_j.remove(0);
		}

		List<State> tempStates = new ArrayList<>();
		for (State s: auto.getStates()){
			if(!newStateLabels.containsKey(s.getName()))
				tempStates.add(s);
		}
		for(State s: tempStates)
			panel.removeState(s);

	}

	private boolean isValid(Automaton auto, Component primary) {
		if (!(primary instanceof AutomatonView))
			return false;
		Automaton pAuto = ((AutomatonView) primary).getDefinition();
		if (pAuto instanceof MultiTapeTuringMachine && auto instanceof MultiTapeTuringMachine)
			return ((MultiTapeTuringMachine) pAuto).getNumTapes() == ((MultiTapeTuringMachine) auto).getNumTapes();
		return pAuto.getClass().equals(auto.getClass());
	}

}
