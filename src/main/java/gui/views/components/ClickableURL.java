package gui.views.components;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JButton;

import gui.utils.GUIErrorHandler;

/**
 * Button associated to an URL and opening the user's web browser on click.
 *
 * @author Maxime PIA
 */
@SuppressWarnings("serial")
public class ClickableURL extends JButton {

	public ClickableURL(final String url, String text) {
		setText("<html><a href='" + url + "'>" + text + "</a></html>");
		setOpaque(false);
		setContentAreaFilled(false);
		setBorderPainted(false);

		addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ev) {
				try {
					Desktop.getDesktop().browse(new URI(url));
				} catch (URISyntaxException | IOException er) {
					new GUIErrorHandler(er);
				}
			}
		});
	}

}
