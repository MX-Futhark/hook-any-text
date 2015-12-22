package gui.utils;

import javax.swing.ImageIcon;

/**
 * Contains the images used by the GUI.
 *
 * @author Maxime PIA
 */
public class Images {

	public static final ImageIcon DEFAULT_ICON =
		new ImageIcon(Images.class.getResource("/img/icon.png"));
	public static final ImageIcon DEFAULT_LOGO =
		new ImageIcon(Images.class.getResource("/img/logo.png"));

	public static final ImageIcon TRIANGLE =
		new ImageIcon(Images.class.getResource("/img/triangle.png"));
	public static final ImageIcon INVERTED_TRIANGLE =
		new ImageIcon(Images.class.getResource("/img/inverted_triangle.png"));

	public static ImageIcon resize(ImageIcon icon, int width, int height) {
		return
			new ImageIcon(icon.getImage().getScaledInstance(width, height, 0));
	}

}
