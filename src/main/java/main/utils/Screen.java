package main.utils;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.List;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;

// TODO jdoc
public class Screen {

	private static User32 user32 = User32.INSTANCE;
	private static Rectangle screen = null;
	private static Rectangle empty = new Rectangle();

	public static Rectangle get() {
		if (screen == null) {
			int width = 0;
			int height = 0;
			GraphicsEnvironment ge =
				GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice[] gs = ge.getScreenDevices();
			for (GraphicsDevice curGs : gs) {
				DisplayMode mode = curGs.getDisplayMode();
				width += mode.getWidth();
				height = mode.getHeight();
			}
			screen = new Rectangle(0, 0, width, height);
		}
		return screen;
	}

	// full screen = 0, nothing = -1, other = window of the process
	public static List<Rectangle> getRegionsFromPID(long pid) {
		List<Rectangle> res = new LinkedList<>();
		if (pid == -1) {
			res.add(empty);
			return res;
		}
		if (pid == 0) {
			res.add(get());
			return res;
		}
		// TODO: remove the window decorations from the rectangle
		List<HWND> windows = WindowsUtils.findByPID(pid);
		for (HWND window : windows) {
			RECT windowRegion = new RECT();
			user32.GetWindowRect(window, windowRegion);
			res.add(windowRegion.toRectangle());
		}
		return res;
	}

	public static Rectangle union(List<Rectangle> rects) {
		if (rects.isEmpty()) return new Rectangle(0, 0, 0, 0);
		Rectangle res = rects.get(0);
		for (Rectangle rect : rects) {
			// TODO: avoid elt1 U elt1
			res.union(rect);
		}
		return res;
	}

}
