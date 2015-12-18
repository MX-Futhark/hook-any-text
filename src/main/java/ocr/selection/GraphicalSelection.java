package ocr.selection;

import static java.awt.GraphicsDevice.WindowTranslucency.PERPIXEL_TRANSPARENT;
import static java.awt.GraphicsDevice.WindowTranslucency.TRANSLUCENT;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Rectangle2D;

import javax.swing.JDialog;

// TODO jdoc
@SuppressWarnings("serial")
public class GraphicalSelection extends JDialog {

	private static final float TRANSLUCENCY = 0.5f;

	public GraphicalSelection() {
        super();
        setLayout(new GridBagLayout());

        // It is best practice to set the window's shape in
        // the componentResized method.  Then, if the window
        // changes size, the shape will be correctly recalculated.
        addComponentListener(new ComponentAdapter() {
            // Give the window an elliptical shape.
            // If the window is resized, the shape is recalculated here.
            @Override
            public void componentResized(ComponentEvent e) {
                setShape(new Rectangle2D.Double(0,0,getWidth(),getHeight()));
            }
        });

        setUndecorated(true);
        setTransluscent();
        setAlwaysOnTop(true);
        setLocationRelativeTo(null);
        getContentPane().setBackground(Color.CYAN);
    }

	private void setTransluscent() {
        // Determine what the GraphicsDevice can support.
        GraphicsEnvironment ge =
            GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        final boolean isTranslucencySupported =
            gd.isWindowTranslucencySupported(TRANSLUCENT);

        //If shaped windows aren't supported, exit.
        if (!gd.isWindowTranslucencySupported(PERPIXEL_TRANSPARENT)) {
            System.err.println("Shaped windows are not supported");
            return;
        }

        //If translucent windows aren't supported,
        //create an opaque window.
        if (!isTranslucencySupported) {
            System.out.println(
                "Translucency is not supported, creating an opaque window");
        }

        // Set the window to 70% translucency, if supported.
        if (isTranslucencySupported) {
            setOpacity(TRANSLUCENCY);
        }
    }

}
