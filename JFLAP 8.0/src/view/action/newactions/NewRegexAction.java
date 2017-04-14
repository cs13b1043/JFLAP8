package view.action.newactions;

import model.regex.RegularExpression;

public class NewRegexAction extends NewFormalDefinitionAction<RegularExpression>{

	public NewRegexAction() {
		super("Go to RE panel");
	}

	@Override
	public RegularExpression createDefinition() {
		return new RegularExpression();
	}

}
