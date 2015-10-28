package hextostring.utils;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides methods to convert hexadecimal data into readable strings.
 *
 * @author Maxime PIA
 */
public class Hex {

	/**
	 * Converts a hexadecimal string into a readable string.
	 *
	 * @param hex
	 * 			The hexadecimal string.
	 * @param charset
	 * 			The charset used for the convertion.
	 * @return The readable string.
	 */
	public static String convertToString(String hex, Charset charset) {
		return byteListToString(HexStringToByteList(hex), charset);
	}

	private static String byteArrayToString(byte[] data, Charset charset) {
		return new String(data, charset);
	}

	private static String byteListToString(List<Byte> data, Charset charset) {
		byte[] arrayData = new byte[data.size()];
		int cmpt = 0;
		for (Byte b : data) {
			arrayData[cmpt++] = b;
		}
		return byteArrayToString(arrayData, charset);
	}

	private static List<Byte> HexStringToByteList(String hex) {
		List<Byte> byteList = new LinkedList<>();
		boolean littleEnd = false;
		int currVal = 0;
		for (int i = 0; i < hex.length(); ++i) {
			if (littleEnd) {
				currVal = currVal | (Character.digit(hex.charAt(i), 16));
			} else {
				currVal = (Character.digit(hex.charAt(i), 16)) << 4;
			}
			if (littleEnd) {
				byteList.add((byte) (currVal & 0xFF));
			}
			littleEnd = !littleEnd;
		}

		return byteList;
	}

}
