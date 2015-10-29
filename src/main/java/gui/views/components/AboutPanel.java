package gui.views.components;

import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import gui.utils.GUIErrorHandler;
import gui.utils.Images;
import main.utils.ProjectProperties;

/**
 * Content of the "about" dialog.
 *
 * @author Maxime PIA
 */
@SuppressWarnings("serial")
public class AboutPanel extends JPanel {

	public AboutPanel() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		JLabel logo = new JLabel(Images.DEFAULT_LOGO);

		try {
			JLabel version = new JLabel(
				"Hook Any Text version "
					+ ProjectProperties.get(ProjectProperties.KEY_VERSION)
			);
			ClickableURL urlButton = new ClickableURL(
				ProjectProperties.get(ProjectProperties.KEY_WEBSITE),
				"GitHub home page"
			);
			JLabel mail = new JLabel(
				"Contact me at: "
					+ ProjectProperties.get(ProjectProperties.KEY_MAIL)
			);

			add(logo);
			add(version);
			add(urlButton);
			add(mail);
		} catch (IOException er) {
			new GUIErrorHandler(er);
		}
	}
}
