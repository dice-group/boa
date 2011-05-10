package de.uni_leipzig.simba.boa.frontend.ui;

import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class EvaluationWindow extends Window {
	
	private static final String HELP_HTML_SNIPPET = "This is "
			+ "an application built during <strong><a href=\""
			+ "http://dev.vaadin.com/\">Vaadin</a></strong> "
			+ "tutorial. Hopefully it doesn't need any real help.";

	public EvaluationWindow() {
		setCaption("Boa Evaluation Window");
		addComponent(new Label(HELP_HTML_SNIPPET, Label.CONTENT_XHTML));
	}

}
