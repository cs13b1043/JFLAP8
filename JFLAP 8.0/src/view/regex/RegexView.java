package view.regex;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import model.regex.RegularExpression;
import model.undo.UndoKeeper;
import view.action.regex.REtoFAAction;
import view.formaldef.BasicFormalDefinitionView;

public class RegexView extends BasicFormalDefinitionView<RegularExpression>{

	private static final Dimension REGEX_SIZE = new Dimension(700, 300);

	public RegexView(RegularExpression model, UndoKeeper keeper,
			boolean editable) {
		super(model, keeper, editable);
		setPreferredSize(REGEX_SIZE);
		setMaximumSize(REGEX_SIZE);
	}
	
	public RegexView(RegularExpression model){
		this(model, new UndoKeeper(), true);
	}

	@Override
	public JComponent createCentralPanel(RegularExpression model,
			UndoKeeper keeper, boolean editable) {
		return new RegexPanel(model, keeper, editable);
	}

	
	@Override
	public JPanel createConvertbar(RegularExpression definition, UndoKeeper keeper) {
		JPanel panel = new JPanel(new BorderLayout());
		JToolBar bar = new JToolBar();

		JButton convertREtoNFA = new JButton("Convert to NFA");
		convertREtoNFA.setToolTipText("Convert RE to NFA");
		
		convertREtoNFA.addActionListener(new REtoFAAction(this));
		bar.add(convertREtoNFA);
		panel.add(bar, BorderLayout.WEST);
		
		return panel;
	}

	@Override
	public String getName() {
		return "Regular Expression Editor";
	}

}
