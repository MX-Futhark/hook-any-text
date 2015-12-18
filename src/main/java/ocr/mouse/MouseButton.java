package ocr.mouse;

import main.options.ValueClass;
import main.options.annotations.CommandLineValue;

// TODO jdoc
public enum MouseButton implements ValueClass {
	@CommandLineValue(
		value = "l",
		description = "Uses the left button to select screen regions."
	)
	LEFT,

	@CommandLineValue(
		value = "r",
		description = "Uses the right button to select screen regions."
	)
	RIGHT,

	@CommandLineValue(
		value = "r",
		description = "Uses either the left or the right button to select"
			+ "screen regions."
	)
	BOTH
}
