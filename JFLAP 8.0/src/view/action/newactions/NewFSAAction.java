package view.action.newactions;

import model.automata.acceptors.fsa.FiniteStateAcceptor;

public class NewFSAAction extends NewFormalDefinitionAction<FiniteStateAcceptor>{

	public NewFSAAction() {
		super("Go to Automaton editor");
		// TODO Auto-generated constructor stub
	}

	@Override
	public FiniteStateAcceptor createDefinition() {
		return  new FiniteStateAcceptor();
	}

}
