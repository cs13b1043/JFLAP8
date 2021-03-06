package oldnewstuff.view.tree;

/*
 *  JFLAP - Formal Languages and Automata Package
 * 
 * 
 *  Susan H. Rodger
 *  Computer Science Department
 *  Duke University
 *  August 27, 2009

 *  Copyright (c) 2002-2009
 *  All rights reserved.

 *  JFLAP is open source software. Please see the LICENSE for terms.
 *
 */
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import debug.JFLAPDebug;

import model.algorithms.testinput.simulate.ConfigurationChain;
import model.algorithms.testinput.simulate.configurations.tm.TMConfiguration;
import model.automata.Automaton;
import model.automata.turing.MultiTapeTuringMachine;
import model.formaldef.FormalDefinition;
import model.grammar.Grammar;
import model.symbols.SymbolString;
import model.symbols.symbolizer.Symbolizers;
import universe.preferences.JFLAPPreferences;
import util.view.tables.GrowableTableModel;

/**
 * The <CODE>InputTableModel</CODE> is a table specifically used for the input
 * of multiple inputs for simulation in an automaton. It has columns for each of
 * the inputs, each of the outputs (when run on a Turing machine and the user
 * wants to treat it as a transducer), and the final result.
 * 
 * @author Thomas Finley
 */

public class InputTableModel extends GrowableTableModel {
	private FormalDefinition myDefinition;

	/**
	 * This instantiates an <CODE>InputTableModel</CODE>.
	 * 
	 * @param automaton
	 *            the automaton that we're inputting stuff for
	 */
	public InputTableModel(Automaton automaton, int add) {
		super(2 * inputsForMachine(automaton) + 1 + add);
		myDefinition = automaton;
	}

	public InputTableModel(Grammar gram, int add) {
		super(2 * 1 + 1 + add);
		myDefinition = gram;
	}

	/**
	 * This instantiates a copy of the <CODE>InputTableModel</CODE>.
	 * 
	 * @param model
	 *            the model to copy
	 */
	public InputTableModel(InputTableModel model) {
		super(model);
		myDefinition = model.myDefinition;
	}

	/**
	 * This instantiates an <CODE>InputTableModel</CODE>.
	 * 
	 * @param columns
	 *            the number of columns for this input table model
	 */
	protected InputTableModel(int columns) {
		super(columns);
	}

	/**
	 * Initializes the contents of a new array to be all blank strings.
	 */
	protected Object[] initializeRow(int row) {
		return createEmptyRow();
	}

	/**
	 * This returns the name of the columns. In the input table model, each of
	 * the first {@link #getColumnCount} columns is an input column and is
	 * titled "Input #", where # is replaced with the number of the column (e.g.
	 * "Input 1", then "Input 2" for a two tape machine), unless there is only
	 * one input column, in which case the single input column is just called
	 * "Input". There are as many output columns as input columns, and are
	 * numbered in a similar fashion except with the prefix "Output" instead of
	 * "Input". The last column is always reserved for the result.
	 * 
	 * @param column
	 *            the number of the column to get the name for
	 * @return the name of the column
	 */
	public String getColumnName(int column) {
		int count = getColumnCount();
		if (column == count - 1)
			return "Result";
		int offset = 0;
		if (isMultiple) {
			offset = 1;
			if (column == 0)
				return "File";
		}

		String word = "";
		if (column <= (getInputCount() - 1 + offset) && column >= (offset)) {
			word = "Input";
		} else if (column > (getInputCount() - 1 + offset)) {
			word = "Output";
			column -= getInputCount();
		}
		if (getInputCount() == 1)
			return word;
		return word + " " + (column + 1 - offset);
	}

	/**
	 * This returns an array of the inputs for the table. The input is organized
	 * by arrays of arrays of strings. The first index of the array is the row
	 * of the input. The second index of the array is the particular input,
	 * which will be a single element array for most machines but an <i>n</i>
	 * element array for an <i>n</i>-tape Turing machine.
	 * 
	 * @return an array of inputs, the first index corresponds directly to the
	 *         row, the second to the column
	 */
	public String[][] getInputs() {
		String[][] inputs = new String[getRowCount() - 1][getInputCount()];
		for (int r = 0; r < inputs.length; r++) {
			int begin = 0;
			if (isMultiple)
				begin = 1;
			for (int c = 0; c < inputs[r].length; c++)
				inputs[r][c] = (String) getValueAt(r, c + begin);
		}
		return inputs;
	}

	/**
	 * This returns if a cell is editable. In this model, a cell is editable if
	 * it's anything other than the last column, which is where the results are
	 * reported.
	 * 
	 * @param row
	 *            the row to check for editableness
	 * @param column
	 *            the column to check for editableness
	 * @return by default this returns <CODE>true</CODE> if this is any column
	 *         other than the last column; in that instance this returns
	 *         <CODE>false</CODE>
	 */
	public boolean isCellEditable(int row, int column) {
		if (isMultiple)
			return (column < getInputCount() && column > 0);
		return column < getInputCount();
	}

	/**
	 * Returns the number of inputs needed for this type of automaton.
	 * 
	 * @param automaton
	 *            the automaton to pass in
	 * @return the number of input strings needed for this automaton; e.g., n
	 *         for an n-tape turing machine, 1 for most anything else
	 */
	public static int inputsForMachine(Automaton automaton) {
		return automaton instanceof MultiTapeTuringMachine ? ((MultiTapeTuringMachine) automaton)
				.getNumTapes() : 1;
	}

	/**
	 * This returns the number of inputs this table model supports.
	 * 
	 * @return the number of inputs for this table model
	 */
	public int getInputCount() {
		int columnCount = getColumnCount();
		if (isMultiple)
			columnCount -= 1;
		return columnCount / 2;
	}

	/**
	 * This returns the cached table model for an automaton of this type. It is
	 * desirable that automatons, upon asking to run input, should be presented
	 * with the same data in the same table since multiple inputs tables are oft
	 * used to test the same sets of input on different automaton's again and
	 * again. In the event that there are multiple models active, this method
	 * returns the last table model that was modified. If there have been no
	 * applicable table models cached yet, then a blank table model is created.
	 * 
	 * @param automaton
	 *            the automaton to get a model for
	 * @return a copy of the model that was last edited with the correct number
	 *         of inputs for this automaton
	 */
	public static InputTableModel getModel(Automaton automaton,
			boolean multipleFile) {
		InputTableModel model = (InputTableModel) INPUTS_TO_MODELS
				.get(new Integer(inputsForMachine(automaton)));
		if (model != null && (model.isMultiple == multipleFile)) {
			model = new InputTableModel(model);
			// Clear out the results column.
			for (int i = 0; i < (model.getRowCount() - 1); i++)
				model.setResult(i, "", null);
		} else {
			int add = 0;
			if (multipleFile)
				add = 1;
			model = new InputTableModel(automaton, add);
		}
		model.addTableModelListener(LISTENER);
		if (multipleFile) {
			model.isMultiple = true;

		}
		return model;
	}

	public static InputTableModel getModel(Grammar gram, boolean multipleFile) {
		InputTableModel model = (InputTableModel) INPUTS_TO_MODELS
				.get(new Integer(1));
		if (model != null) {
			model = new InputTableModel(model);
			// Clear out the results column.
			for (int i = 0; i < (model.getRowCount() - 1); i++)
				model.setResult(i, "", null);
		} else {
			int add = 0;
			if (multipleFile)
				add = 1;
			model = new InputTableModel(gram, add);
		}
		model.addTableModelListener(LISTENER);
		if (multipleFile) {
			model.isMultiple = true;

		}
		return model;
	}

	/**
	 * Sets the result string for a particular row. If you wanted to clear a row
	 * of all results, you would call this function <CODE>setResult(row, "",
	 * null)</CODE>.
	 * 
	 * @param row
	 *            the row to set the result of
	 * @param result
	 *            the result to put in the result column
	 * @param config
	 *            the associated configuration, or <CODE>null</CODE> if you wish
	 *            to not have a configuration associated with a row
	 */
	public void setResult(int row, String result, ConfigurationChain config) {
		int halfway = getInputCount();

		if (config != null
				&& config.getCurrentConfiguration() instanceof TMConfiguration) {
			TMConfiguration c = (TMConfiguration) config
					.getCurrentConfiguration();
			int tapes = c.getNumOfSecondary();

			if (config.isAccept())
				for (int i = 0; i < tapes; i++) {
					int index = c.getPositionForIndex(i);
					SymbolString symbols = c.getStringForIndex(i);
					String put = symbols.subList(index, symbols.size() - JFLAPPreferences.getDefaultTMBufferSize())
							.toString();
					setValueAt(put, row, halfway + i);
				}
			else
				for (int i = 0; i < halfway; i++)
					setValueAt("", row, halfway + i);
		} else
			for (int i = 0; (halfway + i) < this.columns; i++)
				setValueAt("", row, halfway + i);
		setValueAt(result, row, getColumnCount() - 1);

		if (config == null)
			rowToAssociatedConfiguration.remove(new Integer(row));
		else
			rowToAssociatedConfiguration.put(new Integer(row), config);
	}

	@Override
	public void setValueAt(Object newdata, int row, int column) {
		String data = (String) newdata;
		if (column < getInputCount()) {
			SymbolString val = Symbolizers.symbolize(data, myDefinition);
			data = val.toString();
		}
		super.setValueAt(data, row, column);
	}

	/**
	 * This initializes the table so that it is completely blank except for
	 * having one row. The number of columns remains unchanged.
	 */
	public void clear() {
		if (rowToAssociatedConfiguration != null)
			rowToAssociatedConfiguration.clear();
		super.clear();
	}

	/**
	 * Returns the configuration associated with a row. The configuration would
	 * have been input via the {@link #setResult} method.
	 * 
	 * @param row
	 *            the row for which we want the associated accepting
	 *            configuration
	 * @return the accepting configuration associated with a row, or
	 *         <CODE>null</CODE> if there is no associated accepting
	 *         configuration
	 */
	public ConfigurationChain getAssociatedConfigurationForRow(int row) {
		return rowToAssociatedConfiguration.get(new Integer(row));
	}

	/** The static table model listener for caching inputs. */
	protected final static TableModelListener LISTENER = new TableModelListener() {
		public void tableChanged(TableModelEvent event) {
			InputTableModel model = (InputTableModel) event.getSource();
			if (event.getColumn() != TableModelEvent.ALL_COLUMNS
					&& event.getColumn() >= model.getInputCount())
				return; // If the inputs weren't changed, don't bother.
			Integer inputs = new Integer(model.getInputCount());
			INPUTS_TO_MODELS.put(inputs, model);
		}
	};

	public boolean isMultiple = false;

	/**
	 * The map of number of inputs (stored as integers) to input table models.
	 */
	protected final static Map INPUTS_TO_MODELS = new HashMap();

	/**
	 * The map of row to the associated configuration. If this row does not have
	 * an associated configuration, this map should not hold an entry.
	 */
	private final Map<Integer, ConfigurationChain> rowToAssociatedConfiguration = new HashMap<Integer, ConfigurationChain>();
}
