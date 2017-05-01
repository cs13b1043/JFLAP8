package view.automata.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import file.xml.graph.AutomatonEditorData;
import model.automata.Automaton;
import model.automata.Transition;
import model.undo.UndoKeeper;
import view.action.automata.DFAtoREAction;
import view.action.automata.FastSimulateAction;
import view.action.automata.IntersectionAutomataAction;
import view.action.automata.InvertAutomataAction;
import view.action.automata.MultipleSimulateAction;
import view.action.automata.NFAtoDFAAction;
import view.action.automata.UnionAutomataAction;
import view.automata.editing.AutomatonEditorPanel;
import view.automata.tools.ArrowTool;
import view.automata.tools.DeleteTool;
import view.automata.tools.StateTool;
import view.automata.tools.ToolBar;
import view.automata.tools.TransitionTool;
import view.automata.undoing.AutomataRedoAction;
import view.automata.undoing.AutomataUndoAction;
import view.formaldef.BasicFormalDefinitionView;
import view.undoing.redo.RedoAction;
import view.undoing.redo.RedoButton;
import view.undoing.undo.UndoButton;

public class AutomatonView<T extends Automaton<S>, S extends Transition<S>> extends BasicFormalDefinitionView<T> {
	private static final Dimension AUTOMATON_SIZE = new Dimension(600, 600);

	public AutomatonView(T model) {
		this(model, new UndoKeeper(), true);
	}

	public AutomatonView(T model, UndoKeeper keeper, boolean editable) {
		super(model, keeper, editable);
		setPreferredSize(AUTOMATON_SIZE);

		JScrollPane pane = getScroller();
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		pane.revalidate();
		repaint();
	}

	@Override
	public JComponent createCentralPanel(T model, UndoKeeper keeper, boolean editable) {
		return new AutomatonEditorPanel<T, S>(model, keeper, editable);
	}

	@Override
	public String getName() {
		return "Automaton Editor";
	}

	@Override
	public void repaint() {
		super.repaint();
	}

	@Override
	public void setMagnification(double mag) {
		super.setMagnification(mag);
		repaint();
	}

	@Override
	public Component createToolbar(T definition, UndoKeeper keeper) {
		AutomatonEditorPanel<T, S> panel = (AutomatonEditorPanel<T, S>) getCentralPanel();

		ArrowTool<T, S> arrow = createArrowTool(panel, definition);
		StateTool<T, S> state = createStateTool(panel, definition);
		TransitionTool<T, S> trans = new TransitionTool<T, S>(panel);
		DeleteTool<T, S> delete = new DeleteTool<T, S>(panel);

		panel.setTool(arrow);
		ToolBar bar = new ToolBar(arrow, state, trans, delete);
		bar.addToolListener(panel);

		AutomataUndoAction undo = new AutomataUndoAction(keeper, panel);
		RedoAction redo = new AutomataRedoAction(keeper, panel);

		UndoButton undoB = new UndoButton(undo, true);
		undoB.setToolTipText("Undo");
		
		RedoButton redoB = new RedoButton(redo, true);
		redoB.setToolTipText("Redo");
		
		bar.add(undoB);
		bar.add(redoB);
		return bar;
	}

	@Override
	public JPanel createConvertbar(T definition, UndoKeeper keeper) {
		AutomatonEditorPanel<T, S> panel = (AutomatonEditorPanel<T, S>) getCentralPanel();

		JPanel bar =new JPanel(new BorderLayout());
		
		JToolBar bar1 = new JToolBar();
		JButton fastRun = new JButton("FastRun");
		fastRun.addActionListener((new FastSimulateAction((FSAView) this)));
		fastRun.setToolTipText("Run an input on the automaton");
		bar1.add(fastRun);
		
		JButton multipleRun = new JButton("MultipleRun");
		multipleRun.addActionListener((new MultipleSimulateAction((FSAView) this)));
		multipleRun.setToolTipText("Run multiple inputs on the automaton");
		bar1.add(multipleRun);

		JToolBar bar2 = new JToolBar();
		JButton convertToRE = new JButton("ConvertToRE");
		convertToRE.addActionListener(new DFAtoREAction((FSAView) this));
		convertToRE.setToolTipText("Convert the DFA to RE");
		bar2.add(convertToRE);

		JButton convertToDFA = new JButton("ConvertToDFA");
		convertToDFA.addActionListener(new NFAtoDFAAction((FSAView) this));
		convertToDFA.setToolTipText("Convert the NFA to DFA");
		bar2.add(convertToDFA);
		
		JButton invert = new JButton("Complement");
		invert.addActionListener(new InvertAutomataAction((FSAView) this));
		invert.setToolTipText("Complement of Finite Automaton");
		bar2.add(invert);

		JButton union = new JButton("Union");
		union.addActionListener(new UnionAutomataAction((FSAView) this));
		union.setToolTipText("Union of Finite Automata");
		bar2.add(union);
		
		JButton intersection = new JButton("Intersection");
		intersection.addActionListener(new IntersectionAutomataAction((FSAView) this));
		intersection.setToolTipText("Intersection of Finite Automata");
		bar2.add(intersection);

		bar1.setAlignmentX(LEFT_ALIGNMENT);
		bar2.setAlignmentY(LEFT_ALIGNMENT);

		bar.add(bar1, BorderLayout.NORTH);
		bar.add(bar2, BorderLayout.SOUTH);
		return bar;
	}

	@Override
	public JToolBar createViewbar(T definition, UndoKeeper keeper) {
		AutomatonEditorPanel<T, S> panel = (AutomatonEditorPanel<T, S>) getCentralPanel();

		JToolBar bar = new JToolBar();

		JButton fitScreen = new JButton("Fit to screen");
		// changeLayout.addActionListener(new NFAtoDFAAction((FSAView)this));
		fitScreen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				panel.fitToScreen();
			}
		});
		fitScreen.setToolTipText("Fit to Screen");
		bar.add(fitScreen);

		JButton changeLayout = new JButton("LayoutGraph");
		// changeLayout.addActionListener(new NFAtoDFAAction((FSAView)this));
		changeLayout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// AutomatonEditorPanel<T, S> panel = (AutomatonEditorPanel<T,
				// S>) getCentralPanel();
				panel.layoutGraph();
			}
		});
		changeLayout.setToolTipText("Change the layout of the graph");
		bar.add(changeLayout);

		return bar;
	}

	public ArrowTool<T, S> createArrowTool(AutomatonEditorPanel<T, S> panel, T def) {
		return new ArrowTool<T, S>(panel, def);
	}

	public StateTool<T, S> createStateTool(AutomatonEditorPanel<T, S> panel, T def) {
		return new StateTool<T, S>(panel, def);
	}

	@Override
	public T getDefinition() {
		// TODO Auto-generated method stub
		return super.getDefinition();
	}

	public AutomatonEditorData<T, S> createData() {
		return new AutomatonEditorData<T, S>((AutomatonEditorPanel<T, S>) getCentralPanel());
	}

}
