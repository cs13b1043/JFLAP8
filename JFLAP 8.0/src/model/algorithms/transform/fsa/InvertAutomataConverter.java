package model.algorithms.transform.fsa;

import java.util.ArrayList;
import java.util.List;

import errors.BooleanWrapper;
import model.algorithms.AlgorithmException;
import model.algorithms.FormalDefinitionAlgorithm;
import model.algorithms.steppable.AlgorithmStep;
import model.automata.State;
import model.automata.acceptors.FinalStateSet;
import model.automata.acceptors.fsa.FSATransition;
import model.automata.acceptors.fsa.FiniteStateAcceptor;
import model.automata.determinism.FSADeterminismChecker;
import model.symbols.SymbolString;

public class InvertAutomataConverter extends FormalDefinitionAlgorithm<FiniteStateAcceptor> {

	// private TreeMap<State, State[]> myStateToStatesMap;
	// private TreeMap<State, MappingWrapper> myStatesToSymbolsMap;
	private FiniteStateAcceptor myDFA;
	private List<FSATransition> uninverted;
	private FinalStateSet finalStates;

	public InvertAutomataConverter(FiniteStateAcceptor dfa) {
		super(dfa);
		myDFA = dfa;
		uninverted = new ArrayList<>(myDFA.getTransitions());
		finalStates = myDFA.getFinalStateSet().copy();
	}

	@Override
	public String getDescriptionName() {
		return "Invert Automata converter";
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BooleanWrapper[] checkOfProperForm(FiniteStateAcceptor dfa) {
		List<BooleanWrapper> errors = new ArrayList<BooleanWrapper>();
		FSADeterminismChecker check = new FSADeterminismChecker();
		/*
		 * if (check.isDeterministic(dfa)) errors.add(new
		 * BooleanWrapper(false,"This FSA is a DFA already!"));
		 * if(!FiniteStateAcceptor.hasAllSingleSymbolInput(dfa)) errors.add(new
		 * BooleanWrapper(false, "The NFA to convert must have transitions " +
		 * "with either 1 or 0 input symbols."));
		 */
		return errors.toArray(new BooleanWrapper[0]);
	}

	@Override
	public AlgorithmStep[] initializeAllSteps() {
		return new AlgorithmStep[] { new FlipFinalStatesStep() };
	}

	@Override
	public boolean reset() throws AlgorithmException {
		// myDFA = this.getNFA().alphabetAloneCopy();
		// myStateToStatesMap = new TreeMap<State, State[]>();
		// myStatesToSymbolsMap = new TreeMap<State, MappingWrapper>();
		// createAndAddInitialState();
		return true;
	}

	public FiniteStateAcceptor getDFA() {
		// FinalStateSet finalstates= myDFA.getFinalStateSet();
		// StateSet states= this.getNFA().getStates();
		//
		// FinalStateSet newFinalStates= new FinalStateSet();
		// Iterator<State> stateIterator = states.iterator();
		//
		// while(stateIterator.hasNext()){
		// State i = stateIterator.next();
		// if (!finalstates.contains(i)){
		// newFinalStates.add(i);
		// }
		// }
		// myDFA.componentChanged(new AdvancedChangeEvent(finalstates,
		// finalstates.ITEM_REMOVED, finalstates.toArray()));
		// myDFA.componentChanged(new
		// AdvancedChangeEvent(newFinalStates,newFinalStates.ITEM_REMOVED,
		// newFinalStates.toArray()));
		// System.out.println("Hello, I'm here.");
		return myDFA;
	}

	//
	// //////// Algorithm Steps ///////
	//

	private class FlipFinalStatesStep implements AlgorithmStep {

		boolean complete = false;

		@Override
		public String getDescriptionName() {
			return "Add new Start State";
		}

		@Override
		public String getDescription() {
			return null;
		}

		@Override
		public boolean execute() throws AlgorithmException {
			// remove the current final states
			
			myDFA.getFinalStateSet().removeAll(finalStates);

			for(State state : myDFA.getStates()){
				if(!finalStates.contains(state))
					myDFA.getFinalStateSet().add(state);
			}
			complete = true;
			return true;

		}

		@Override
		public boolean isComplete() {
			// TODO Auto-generated method stub
			return complete;
		}

	}

	// public static FiniteStateAcceptor convertToDFA(FiniteStateAcceptor nfa) {
	// InvertAutomataConverter converter = new InvertAutomataConverter(nfa);
	// converter.stepToCompletion();
	// return converter.getDFA();
	// }

}
