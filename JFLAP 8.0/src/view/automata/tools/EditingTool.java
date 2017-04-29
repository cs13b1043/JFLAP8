package view.automata.tools;

import java.awt.event.MouseEvent;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;

import model.automata.Automaton;
import model.automata.Transition;
import model.undo.UndoKeeper;
import view.automata.editing.AutomatonEditorPanel;

/**
 * Superclass for all Tools that require access to the AutomatonEditorPanel that
 * is using them.
 * 
 * @author Ian McMahon
 */
public abstract class EditingTool<T extends Automaton<S>, S extends Transition<S>>
		extends Tool {

	
	private AutomatonEditorPanel<T, S> myPanel;
	private UndoKeeper myKeeper;

	public EditingTool(AutomatonEditorPanel<T, S> panel) {
		this.myPanel = panel;
		myKeeper = panel.getKeeper();
		UIManager.put("ToolTip.background", new ColorUIResource(255, 250, 205));
	}

	public AutomatonEditorPanel<T, S> getPanel() {
		return myPanel;
	}

	public UndoKeeper getKeeper() {
		return myKeeper;
	}
	
	@Override
	public void mousePressed(MouseEvent arg0) {
		myPanel.clearSelection();
	}

	public void setActive(boolean active) {
		if (active) {
			myPanel.addMouseListener(this);
			myPanel.addMouseMotionListener(this);
		} else {
			myPanel.removeMouseListener(this);
			myPanel.removeMouseMotionListener(this);
		}
		myPanel.requestFocus();
		myPanel.clearSelection();
	}
}
