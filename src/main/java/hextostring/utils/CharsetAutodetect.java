package hextostring.utils;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class CharsetAutodetect extends Charset {

	public static final String NAME = "autodetect";

	private static CharsetAutodetect instance = null;

	private CharsetAutodetect() {
		super(NAME, null);
	}

	public static CharsetAutodetect getInstance() {
		if (instance == null) {
			instance = new CharsetAutodetect();
		}
		return instance;
	}

	@Override
	public boolean contains(Charset cs) {
		return false;
	}

	@Override
	public CharsetDecoder newDecoder() {
		return null;
	}

	@Override
	public CharsetEncoder newEncoder() {
		return null;
	}

}
