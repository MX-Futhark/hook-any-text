package main.options.parser;

import java.lang.reflect.Field;

import hexcapture.HexSelection;
import hexcapture.HexSelections;
import main.options.Options;

public class HexSelectionsParser extends FullArgumentParser<HexSelections> {

	public HexSelectionsParser(Options opts, Field f, String argument) {
		super(opts, f, argument);
	}

	@Override
	protected HexSelections getArgumentValue(String arg)
		throws IncompatibleParserException {

		HexSelections selections = new HexSelections(true);

		String[] parts = checkArgumentAndGetValue(arg).split(";");
		int partCounter = 0, activeSelectionIndex = 0;

		for (String sel : parts) {
			String[] elts = sel.split(",");
			if (elts.length == 2 || elts.length == 3) {
				selections.add(new HexSelection(
					Integer.parseInt(elts[0]),
					Integer.parseInt(elts[1])
				));

				if (elts.length == 3) {
					boolean active = Boolean.parseBoolean(elts[2]);
					if (active) {
						activeSelectionIndex = partCounter;
					}
				}
			} else {
				throw new IllegalArgumentException("Unexpected number of " +
					"components in selection representation");
			}
			++partCounter;
		}

		selections.setActiveSelectionIndex(activeSelectionIndex);

		return selections;
	}
}
