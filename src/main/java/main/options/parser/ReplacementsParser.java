package main.options.parser;

import java.lang.reflect.Field;
import java.util.NoSuchElementException;
import java.util.Scanner;

import hextostring.replacement.Replacement;
import hextostring.replacement.ReplacementType;
import hextostring.replacement.Replacements;
import main.options.Options;

/**
 * LL1 parser for command-line replacements.
 *
 * @author Maxime PIA
 */
public class ReplacementsParser extends FullArgumentParser<Replacements> {

	public ReplacementsParser(Options opts, Field f, String argument) {
		super(opts, f, argument);
	}

	@Override
	protected Replacements getArgumentValue(String arg)
		throws IncompatibleParserException {

		Replacements replacements = null;
		Scanner sc = new Scanner(checkArgumentAndGetValue(arg));
		try {
			replacements = readReplacements(sc);
		} catch (NoSuchElementException | IllegalArgumentException e) {
			e.printStackTrace();
			throw e;
		} finally {
			sc.close();
		}
		return replacements;
	}

	private Replacements readReplacements(Scanner sc) {
		Replacements replacements = new Replacements();
		while (sc.hasNext()) {
			replacements.add(readReplacement(sc));
			if (sc.hasNext()) {
				sc.findInLine(",");
			}
		}
		return replacements;
	}

	private Replacement readReplacement(Scanner sc) {
		boolean isSequenceHexadecimal = readIsHexadecimal(sc);
		String sequence = readString(sc);
		sequence = sequence.substring(1, sequence.length() - 1);

		sc.findInLine(">");

		boolean isReplacementHexadecimal = readIsHexadecimal(sc);
		String replacement = readString(sc);
		replacement = replacement.substring(1, replacement.length() - 1);

		if (!isSequenceHexadecimal && isReplacementHexadecimal) {
			throw new IllegalArgumentException("Hexadecimal cannot be a "
				+ "replacement of string");
		}
		ReplacementType type = isReplacementHexadecimal
			? ReplacementType.HEX2HEX
			: isSequenceHexadecimal
				? ReplacementType.HEX2STR
				: ReplacementType.STR2STR;

		boolean esc = readIsEscapeChar(sc);
		boolean reg = readIsRegexp(sc);
		return new Replacement(sequence, replacement, esc, reg, type);
	}

	private boolean readIsHexadecimal(Scanner sc) {
		return checkNextAndRead(sc, "0x");
	}

	private String readString(Scanner sc) {
		return sc.findInLine("([\"]([^\\\\\"]*([\\\\].)*)*[\"])");
	}

	private boolean readIsEscapeChar(Scanner sc) {
		return checkNextAndRead(sc, "e");
	}

	private boolean readIsRegexp(Scanner sc) {
		return checkNextAndRead(sc, "r");
	}

	private boolean checkNextAndRead(Scanner sc, String s) {
		// s must not be a pattern
		return sc.findWithinHorizon(s, s.length()) != null;
	}

}
