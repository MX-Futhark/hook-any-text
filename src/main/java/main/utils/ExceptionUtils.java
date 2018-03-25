package main.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Utility functions for exceptions
 *
 * @author Maxime PIA
 */
public class ExceptionUtils {

	/**
	 * Provides the string corresponding to a stack trace.
	 * @param e
	 * @return
	 */
	public static String getPrintableStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

}
