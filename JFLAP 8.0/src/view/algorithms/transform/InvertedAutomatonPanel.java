package view.algorithms.transform;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import file.xml.graph.AutomatonEditorData;
import model.algorithms.transform.fsa.InvertAutomataConverter;
import model.automata.State;
import model.automata.acceptors.fsa.FSATransition;
import model.automata.acceptors.fsa.FiniteStateAcceptor;
import model.undo.UndoKeeper;
import universe.JFLAPUniverse;
import util.Point2DAdv;
import util.view.magnify.MagnifiablePanel;
import util.view.magnify.MagnifiableScrollPane;
import view.ViewFactory;
import view.automata.AutomatonDisplayPanel;
import view.automata.editing.AutomatonEditorPanel;
import view.automata.simulate.TooltipAction;
import view.automata.tools.ToolBar;
import view.automata.tools.algorithm.NonTransitionArrowTool;

public class InvertedAutomatonPanel extends AutomatonDisplayPanel<FiniteStateAcceptor, FSATransition> {

	private InvertAutomataConverter myAlg;
	private AutomatonEditorPanel<FiniteStateAcceptor, FSATransition> myDFApanel;

	public InvertedAutomatonPanel(AutomatonEditorPanel<FiniteStateAcceptor, FSATransition> nfa,
			InvertAutomataConverter convert) {
		super(nfa, nfa.getAutomaton(), "Complement FSA");
		myAlg = convert;
		updateSize();
		initView();
	}

	private void initView() {
		// AutomatonEditorPanel<FiniteStateAcceptor, FSATransition> nfa =
		// getEditorPanel();
		myDFApanel = new AutomatonEditorPanel<FiniteStateAcceptor, FSATransition>(myAlg.getDFA(), new UndoKeeper(),
				true);
		myDFApanel.getActionMap().put(AutomatonEditorPanel.DELETE, null);
		myDFApanel.updateBounds(getGraphics());

		// MagnifiableScrollPane nScroll = new MagnifiableScrollPane(nfa);
		MagnifiableScrollPane dScroll = new MagnifiableScrollPane(myDFApanel);
		// Dimension nSize = nfa.getMinimumSize();
		// int padding = (int) (nfa.getStateBounds() - nfa.getStateRadius());
		// nScroll.setMinimumSize(new Dimension(nSize.width + padding,
		// nSize.height));
		MagnifiablePanel right = new MagnifiablePanel(new BorderLayout());

		ToolBar tools = createTools();
		right.add(tools, BorderLayout.EAST);
		right.add(dScroll, BorderLayout.CENTER);

		Dimension rSize = right.getMinimumSize();
		int width = (int) (rSize.width * 1.5);
		right.setMinimumSize(new Dimension(width, rSize.height));

		// split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, nScroll,
		// right);
		add(right, BorderLayout.CENTER);

		Dimension size = getPreferredSize();
		rSize = right.getMinimumSize();
		width = size.width + rSize.width;
		setPreferredSize(new Dimension(width, size.height));
		// moveStartState(tools.getPreferredSize().width);

	}

	private void moveStartState(int width) {
		FiniteStateAcceptor fsa = myDFApanel.getAutomaton();
		State start = fsa.getStartState();
		Point2D current = myDFApanel.getPointForVertex(start);

		myDFApanel.moveState(start, new Point2DAdv(width, current.getY()));
	}

	private ToolBar createTools() {
		NonTransitionArrowTool<FiniteStateAcceptor, FSATransition> arrow = new NonTransitionArrowTool<FiniteStateAcceptor, FSATransition>(
				myDFApanel, myDFApanel.getAutomaton());

		// TransitionExpanderTool trans = new TransitionExpanderTool(myDFApanel,
		// myAlg);
		// StateExpanderTool state = new StateExpanderTool(myDFApanel, myAlg);

		// ToolBar tools = new ToolBar(arrow, trans, state);
		// tools.addToolListener(myDFApanel);
		// myDFApanel.setTool(arrow);

		// tools.addSeparator();
		ToolBar tools = new ToolBar();

		tools.add(new TooltipAction("Next", "Next step") {
			public void actionPerformed(ActionEvent e) {
				if (myAlg.canStep()) {
					myAlg.step();
					System.out.println("Step finish");
					// myDFApanel.layoutGraph();
				}
			}
		});

		tools.add(new TooltipAction("Complete", "Final Result") {
			public void actionPerformed(ActionEvent e) {
				while (myAlg.canStep()) {
					myAlg.step();
					System.out.println("whole finish");
					// without the following line, this action will crash JFLAP.
					// No idea
					// why, something to do with FontMetrics when drawing
					// transition labels.
					myDFApanel.layoutGraph();
				}
			}
		});

		/*
		 * tools.add(new TooltipAction("Done?", "Are we finished?") { public
		 * void actionPerformed(ActionEvent e) { done(); } });
		 */

		tools.add(new TooltipAction("Export", "Display complete DFA in new window.") {

			@Override
			public void actionPerformed(ActionEvent e) {
				export();
			}
		});

		JButton changeLayout = new JButton("LayoutGraph");
		// changeLayout.addActionListener(new NFAtoDFAAction((FSAView)this));
		changeLayout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// AutomatonEditorPanel<T, S> panel = (AutomatonEditorPanel<T,
				// S>) getCentralPanel();
				myDFApanel.layoutGraph();
			}
		});
		changeLayout.setToolTipText("Change the layout of the graph");
		tools.add(changeLayout);

		JButton fitScreen = new JButton("Fit to screen");
		// changeLayout.addActionListener(new NFAtoDFAAction((FSAView)this));
		fitScreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				myDFApanel.fitToScreen();
			}
		});
		fitScreen.setToolTipText("Fit to Screen");
		tools.add(fitScreen);

		return tools;
	}

	private void export() {
		if (myAlg.isRunning())
			done();
		else {
			JOptionPane.showMessageDialog(JFLAPUniverse.getActiveEnvironment(),
					"The DFA will now be placed in a new window.");
			AutomatonEditorData<FiniteStateAcceptor, FSATransition> data = new AutomatonEditorData<FiniteStateAcceptor, FSATransition>(
					myDFApanel);
			JFLAPUniverse.registerEnvironment(ViewFactory.createAutomataView(data));
		}
	}

	private void done() {
	}
}
