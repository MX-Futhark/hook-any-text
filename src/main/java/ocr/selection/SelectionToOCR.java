package ocr.selection;

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Observable;
import java.util.Observer;

import javax.swing.SwingUtilities;

import hextostring.utils.Charsets;
import main.history.History;
import main.utils.IOUtils;
import main.utils.Screen;
import ocr.OCROptions;
import ocr.mouse.MouseButton;
import ocr.mouse.MouseEvent;
import ocr.mouse.MouseHook;
import ocr.mouse.MouseState;

// TODO jdoc
public class SelectionToOCR implements Observer {

	public static final String OCR_OUTPUT_FILENAME = "ocr_output.txt";
	public static final String DEFAULT_CAPTURE2TEXT_LOCATION =
		"Capture2Text\\Capture2Text.exe";

	private static final int SELECTION_WIDTH_THRESHOLD = 10;

	private GraphicalSelection gs;
	private OCROptions opts;
	private History<Rectangle, String> history;
	private Rectangle lastCapturedRegion = null;

	private SelectionToOCR(MouseState ms, GraphicalSelection gs,
		OCROptions opts, History<Rectangle, String> history) {

		ms.addObserver(this);
		this.gs = gs;
		this.opts = opts;
		this.history = history;
	}

	public static boolean acceptDownEvent(MouseEvent evt, MouseButton btn) {
		if (evt != MouseEvent.LDOWN && evt != MouseEvent.RDOWN) return false;

		return (btn == MouseButton.BOTH)
			|| (btn == MouseButton.LEFT && evt == MouseEvent.LDOWN)
			|| (btn == MouseButton.RIGHT && evt == MouseEvent.RDOWN);
	}

	@Override
	public void update(Observable o, Object arg) {
		final MouseState ms = (MouseState) o;
		MouseEvent evt = (MouseEvent) arg;

		if (acceptDownEvent(evt, opts.getOcrButton())) {
			System.out.println("down evt accepted");
			lastCapturedRegion = Screen.union(
				Screen.getRegionsFromPID(opts.getAttachedPID())
			);
			System.out.println("captured region : " + lastCapturedRegion);
		}

		if (lastCapturedRegion != null && ms.isDown(opts.getOcrButton())
			&& lastCapturedRegion.contains(ms.getPosX(), ms.getPosY())) {

			if (!gs.isVisible()) {
				gs.setLocation(ms.getPosX(), ms.getPosY());
				gs.setSize(0, 0);
				System.out.println("-> position : " + ms.getPosX() + " - " + ms.getPosY());
				gs.setVisible(true);
			} else {
				gs.setSize(
					Math.max(0, ms.getPosX() - gs.getX()),
					Math.max(0, ms.getPosY() - gs.getY())
				);
			}
		}
		if (!ms.isDown(opts.getOcrButton()) && gs.isVisible()) {
			System.out.println("-> final size : " + (gs.getWidth()) + " " + (gs.getHeight()));
			gs.setVisible(false);
			lastCapturedRegion = null;
			new Thread(new Runnable() {
				@Override
				public void run() {
					startOCR(
						gs.getX(),
						gs.getY(),
						gs.getX() + gs.getWidth(),
						gs.getY() + gs.getHeight()
					);
				}
			}).start();
		}
	}

	private void startOCR(int xMin, int yMin, int xMax, int yMax) {
		System.out.println("-> OCR " + xMin + " " + yMin + " " + xMax + " " + yMax);
		if (xMax - xMin < SELECTION_WIDTH_THRESHOLD
			|| yMax - yMin < SELECTION_WIDTH_THRESHOLD) {

			return;
		}
		System.out.println("region is large enough");
		try {
			File outputFile =
				IOUtils.getFileInTempDirectory(OCR_OUTPUT_FILENAME);
			int outputFileCounter = 1;
			while (!outputFile.canWrite()) {
				outputFile = IOUtils.getFileInTempDirectory(
					OCR_OUTPUT_FILENAME + outputFileCounter++
				);
			}
			Process process = new ProcessBuilder(
				"\"" + IOUtils.RUNNING_JAR_DIRECTORY + "\\"
					+ DEFAULT_CAPTURE2TEXT_LOCATION + "\"",
				xMin + "", yMin + "", xMax + "", yMax + "",
				"\"" + outputFile.getAbsolutePath() + "\"").start();
			process.waitFor();
			synchronized (history) {
				history.add(
					new Rectangle(xMin, yMin, xMax, yMax),
					new String(
						Files.readAllBytes(outputFile.toPath()),
						Charsets.UTF8
					)
				);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
	}


	private static SelectionToOCR instance;
	private static MouseState mouseState = MouseHook.getMouseState();

	public static void startOCR(final OCROptions opts,
		final History<Rectangle, String> history) {

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				System.out.println("start");
				instance = new SelectionToOCR(
					mouseState,
					new GraphicalSelection(),
					opts,
					history
				);
			}
		});
		MouseHook.startHook();
	}

	public static void stopOCR() {
		System.out.println("stop");
		mouseState.deleteObserver(instance);
		MouseHook.stopHook();
	}

	// TODO : delet
	public static void main(String[] args) {
		startOCR(new OCROptions(), new History<Rectangle, String>());
	}

}
