package hextostring.tests;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import hextostring.ConvertOptions;
import hextostring.convert.Converter;
import hextostring.convert.ConverterFactory;
import hextostring.debug.DebuggableStrings;
import hextostring.format.Formatter;
import hextostring.format.FormatterFactory;
import hextostring.replacement.Replacements;
import hextostring.utils.Charsets;
import hextostring.utils.IndentablePrintStream;
import main.MainOptions;
import main.options.domain.ValueOutOfDomainException;

/**
 * Main test class.
 *
 * A test consists of an input and a corresponding expected,
 * manually written output.
 *
 * Every input file is converted and compared to the expected output.
 *
 * Test files are put into the "tests" directory at the root of the project.
 * Its architecture is as follows:
 *
 *   tests
 *   |-sjis (contains tests for games using Shift JIS)
 *   | |-handmade (contains tests crafted without relying on a game)
 *   | |-[game1]
 *   | | |-cmd.txt (contains description of options optimized for this game)
 *   | | |-input
 *   | | | |-0001.txt
 *   | | | |-0002.txt
 *   | | | |-...
 *   | | |-expected_output
 *   | |   |-0001.txt
 *   | |   |-0002.txt
 *   | |   |-...
 *   | |-[game2]
 *   | |-...
 *   |-utf16-be (contains tests for games using UTF-16 Big Endian)
 *   | |-[gameA]
 *   | | |-cmd.txt
 *   | | |-input
 *   | | | |-0001.txt
 *   | | | |-0002.txt
 *   | | | |-...
 *   | | |-expected_output
 *   | |   |-0001.txt
 *   | |   |-0002.txt
 *   | |   |-...
 *   | |-[gameB]
 *   | |-...
 *   |-utf16-le (contains tests for games using UTF-16 Little Endian)
 *   | |-...
 *   |-utf8 (contains tests for games using UTF-8)
 *     |-...
 *
 * It is assumed that all .txt files are encoded in UTF-8, without the BOM.
 *
 * @author Maxime PIA
 */
public class TestsLauncher {

	private static Formatter formatter =
		FormatterFactory.getFormatterInstance(true);
	private static Converter currentConverter;
	private static IndentablePrintStream out = new IndentablePrintStream();

	private static final String INPUT_DIR = "input";
	private static final String EXPECTED_OUTPUT_DIR = "expected_output";
	private static final String CMD_FILE = "cmd.txt";

	private static final String SJIS_DIR = "sjis";
	private static final String UTF16BE_DIR = "utf16-be";
	private static final String UTF16LE_DIR = "utf16-le";
	private static final String UTF8_DIR = "utf8";

	private static Logger logger = LogManager.getLogger(TestsLauncher.class);

	private static File[] listSortedFiles(File f) {
		File[] files = f.listFiles();
		Arrays.sort(files);
		return files;
	}

	private static ConvertOptions getConvertOptions(File cmdFile)
		throws IOException, ValueOutOfDomainException {

		String cmd = new String(
			Files.readAllBytes(cmdFile.toPath()),
			Charsets.UTF8
		);
		return new MainOptions(cmd.split(" ")).getConvertOptions();
	}

	private static boolean compare(File inputFile, File expectedOutputFile,
		int indentLevel, ConvertOptions opts) {

		try {
			String input = new String(
				Files.readAllBytes(inputFile.toPath()),
				Charsets.UTF8
			);
			String expectedOutput = new String(
				Files.readAllBytes(expectedOutputFile.toPath()),
				Charsets.UTF8
			);

			DebuggableStrings dInput = currentConverter.convert(input);
			formatter.format(dInput.getValidLineList());
			String actualOutput = dInput.toString(
				opts == null
					? ConvertOptions.DEFAULT_DEBUGGING_FLAGS
					: opts.getDebuggingFlags(),
				opts == null
					? ConvertOptions.DEFAULT_STRICTNESS
					: opts.getStrictness()
			);
			out.print(inputFile.getName(), indentLevel);

			if (expectedOutput.equals(actualOutput)) {
				out.println(" OK");
				return true;
			} else {
				out.println(" FAILURE");
				out.println("expected:", indentLevel + 1);
				out.println(expectedOutput, indentLevel + 2);
				out.println("instead of:", indentLevel + 1);
				out.println(actualOutput, indentLevel + 2);
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static Map<Boolean, Integer> compareAll(File inputDirectory,
		File expectedOutputDirectory, int indentLevel, ConvertOptions opts) {

		File[] inputs = listSortedFiles(inputDirectory);
		File[] expectedOutputs = listSortedFiles(expectedOutputDirectory);

		Map<Boolean, Integer> testResult = new HashMap<>();
		testResult.put(false, 0);
		testResult.put(true, 0);

		for (int i = 0; i < inputs.length && i < expectedOutputs.length; ++i) {
			boolean eq =
				compare(inputs[i], expectedOutputs[i], indentLevel, opts);
			testResult.put(eq, testResult.get(eq) + 1);
		}

		return testResult;
	}

	private static boolean isEncoding(File f, String encodingName) {
		return f == null
			? false
			: f.getName().equals(encodingName)
				|| isEncoding(f.getParentFile(), encodingName);
	}

	private static void setEncoding(File f, boolean detectEncoding,
		Replacements r) {

		if (detectEncoding) {
			currentConverter =
				ConverterFactory.getConverterInstance(Charsets.DETECT, r);
		} else if (isEncoding(f, SJIS_DIR)) {
			currentConverter =
				ConverterFactory.getConverterInstance(Charsets.SHIFT_JIS, r);
		} else if (isEncoding(f, UTF16BE_DIR)) {
			currentConverter =
				ConverterFactory.getConverterInstance(Charsets.UTF16_BE, r);
		} else if (isEncoding(f, UTF16LE_DIR)) {
			currentConverter =
				ConverterFactory.getConverterInstance(Charsets.UTF16_LE, r);
		} else if (isEncoding(f, UTF8_DIR)) {
			currentConverter =
				ConverterFactory.getConverterInstance(Charsets.UTF8, r);
		}
	}

	private static File getFileByName(File[] files, String name) {
		for(File f : files) {
			if (f.getName().equals(name)) {
				return f;
			}
		}
		return null;
	}

	private static Map<Boolean, Integer> goThrough(File f, int indentLevel,
		boolean detectEncoding) throws IOException, ValueOutOfDomainException {

		Map<Boolean, Integer> dirResult = new HashMap<>();
		dirResult.put(false, 0);
		dirResult.put(true, 0);

		out.println(f.getName(), indentLevel);

		if (f.isDirectory()) {
			File[] files = listSortedFiles(f);
			boolean goFurther = true;
			List<Map<Boolean, Integer>> comparisonResults = new LinkedList<>();

			if (indentLevel == 2) {
				ConvertOptions convOpts = null;
				File cmd = getFileByName(files, CMD_FILE);
				if (cmd != null) {
					convOpts = getConvertOptions(cmd);
				}

				setEncoding(f, detectEncoding, convOpts == null
					? null
					: convOpts.getReplacements());

				comparisonResults.add(compareAll(
					getFileByName(files, INPUT_DIR),
					getFileByName(files, EXPECTED_OUTPUT_DIR),
					indentLevel + 1,
					convOpts
				));
				goFurther = false;
			}

			// recursively go through every directory
			if (goFurther) {
				for (File children : files) {
					comparisonResults.add(
						goThrough(children, indentLevel + 1, detectEncoding)
					);
				}
			}

			for (Map<Boolean, Integer> result : comparisonResults) {
				dirResult.put(false, dirResult.get(false) + result.get(false));
				dirResult.put(true, dirResult.get(true) + result.get(true));
			}
		}

		displayTestResult(dirResult, f.getName(), indentLevel);
		return dirResult;
	}

	private static void displayTestResult(Map<Boolean, Integer> result,
		String testName, int indentLevel) {

		double percentageOk =
			result.get(true) * 100.0
			/ (result.get(true) + result.get(false));
		DecimalFormat df = new DecimalFormat("##0.00");
		df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
		out.println("Total " + testName + ": "
			+ df.format(percentageOk) + "% OK", indentLevel);
	}

	private static final String NODETECT_MSG = "Without encoding detection:\n";
	private static final String DETECT_MSG = "With encoding detection:\n";
	private static final String SEPARATOR = "\n----------\n\n";

	private static void goThrough(String path) throws IOException,
		ValueOutOfDomainException {

		File f = new File(path);
		out.println(NODETECT_MSG);
		goThrough(f, 0, false);
		out.println(SEPARATOR + DETECT_MSG);
		goThrough(f, 0, true);
	}

	private static void compare(String inputPath, String expectedOutputPath,
		ConvertOptions convOpts) {

		File input = new File(inputPath);
		if (!input.isFile()) {
			throw new IllegalArgumentException(
				input.getPath() + " doesn't exist."
			);
		}
		File expectedOutput = new File(expectedOutputPath);
		out.println(NODETECT_MSG);
		setEncoding(input.getParentFile(), false, convOpts.getReplacements());
		compare(input, expectedOutput, 0, convOpts);
		out.println(SEPARATOR + DETECT_MSG);
		setEncoding(input.getParentFile(), true, convOpts.getReplacements());
		compare(input, expectedOutput, 0, convOpts);
	}

	/**
	 * Starts the test campaign.
	 *
	 * @param args
	 * 			args[0] = directory to test, without initial an final "/"
	 * 			args[1] = number identifying a test, without ".txt" (optional)
	 * @throws ValueOutOfDomainException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException,
		ValueOutOfDomainException {

		logger.info("Starting conversion tests...");
		String testsDirectory =
			TestsLauncher.class.getResource("/tests").getPath();
		if (args.length > 0) {
			String directory = testsDirectory + args[0];
			if (args.length > 1) {
				String testFile = args[1] + ".txt";
				File cmd = new File(directory + "/" + CMD_FILE);
				ConvertOptions convOpts = cmd.exists()
					? getConvertOptions(cmd)
					: null;
				compare(
					directory + "/" + INPUT_DIR + "/" + testFile,
					directory + "/" + EXPECTED_OUTPUT_DIR + "/" + testFile,
					convOpts
				);
			} else {
				goThrough(directory);
			}
		} else {
			goThrough(testsDirectory);
		}
		logger.info("Conversion tests over.");
	}

}
