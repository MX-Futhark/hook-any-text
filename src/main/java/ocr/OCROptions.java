package ocr;

import java.io.Serializable;

import main.options.Options;
import main.options.ValueClass;
import main.options.annotations.CommandLineArgument;
import main.options.domain.Values;
import ocr.mouse.MouseButton;

// TODO jdoc
public class OCROptions extends Options implements Serializable {

	/**
	 * Backward-compatible with 0.8.0
	 */
	private static final long serialVersionUID = 8000000L;

	private static final Boolean DEFAULT_OCR_ON = false;
	private static final MouseButton DEFAULT_OCR_BUTTON =
		MouseButton.BOTH;
	private static final Long DEFAULT_ATTACHED_PID = -1L;

	@CommandLineArgument(command = "ocr", description = "Activates the OCR.")
	private Boolean ocrOn = DEFAULT_OCR_ON;

	@CommandLineArgument(
		command = "ocrbutton",
		description = "Chooses the button(s) of the mouse used to select screen"
			+ " regions that will be fed to the OCR."
	)
	private MouseButton ocrButton = DEFAULT_OCR_BUTTON;
	public static final Values<MouseButton> OCR_BUTTON_DOMAIN =
		new Values<>(MouseButton.values());
	public static final Class<? extends ValueClass> OCR_BUTTON_VALUE_CLASS =
			MouseButton.class;

	@CommandLineArgument(
		command = "ocrpid",
		description = "PID of the process in which the region selection for the"
			+ " OCR is active."
	)
	private transient Long attachedPID = DEFAULT_ATTACHED_PID;

	public synchronized boolean getOcrOn() {
		return ocrOn;
	}

	public synchronized void setOcrOn(boolean ocrOn) {
		this.ocrOn = ocrOn;
	}

	public synchronized MouseButton getOcrButton() {
		return ocrButton;
	}

	public synchronized void setOcrButton(MouseButton ocrButton) {
		this.ocrButton = ocrButton;
	}

	public synchronized long getAttachedPID() {
		return attachedPID;
	}

	public synchronized void setAttachedPID(long attachedPID) {
		this.attachedPID = attachedPID;
	}

}
