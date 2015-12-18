package ocr.mouse;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.BaseTSD.ULONG_PTR;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HMODULE;
import com.sun.jna.platform.win32.WinDef.LRESULT;
import com.sun.jna.platform.win32.WinDef.WPARAM;
import com.sun.jna.platform.win32.WinUser;
import com.sun.jna.platform.win32.WinUser.HHOOK;
import com.sun.jna.platform.win32.WinUser.MSG;

import ocr.mouse.MouseHook.WinUserX.LowLevelMouseProc;
import ocr.mouse.MouseHook.WinUserX.MSLLHOOKSTRUCT;

// TODO: jdoc, reorganize to be used with the ocr
public class MouseHook {

	private static volatile boolean quit;
	private static HHOOK hhk;
	private static LowLevelMouseProc mouseHook;

	private static MouseState mouseState = new MouseState();

	public interface WinUserX extends WinUser {
	    public int WM_LBUTTONDOWN = 0x0201;
	    public int WM_LBUTTONUP = 0x0202;
	    public int WM_MOUSEMOVE = 0x0200;
	    public int WM_MOUSEWHEEL = 0x020A;
	    public int WM_MOUSEHWHEEL = 0x020E;
	    public int WM_RBUTTONDOWN = 0x0204;
	    public int WM_RBUTTONUP = 0x0205;
	    public int WM_MBUTTONDOWN = 0x0207;

	    public interface LowLevelMouseProc extends HOOKPROC {
	        LRESULT callback(int nCode, WPARAM wParam, MSLLHOOKSTRUCT lParam);
	    }

	    public class MSLLHOOKSTRUCT extends Structure {
	        public POINT pt;
	        public int mouseData;
	        public int flags;
	        public int time;
	        public ULONG_PTR dwExtraInfo;
	    }
	}

	public static void startHook() {
		final User32 user32 = User32.INSTANCE;
		HMODULE hMod = Kernel32.INSTANCE.GetModuleHandle(null);
		mouseHook = new LowLevelMouseProc() {
			public LRESULT callback(int nCode, WPARAM wParam,
				MSLLHOOKSTRUCT info) {

				if (nCode >= 0) {
					switch (wParam.intValue()) {
					case WinUserX.WM_LBUTTONDOWN:
						mouseState.setLDown(true);
						break;
					case WinUserX.WM_LBUTTONUP:
						mouseState.setLDown(false);
						break;
					case WinUserX.WM_RBUTTONDOWN:
						mouseState.setRDown(true);
						break;
					case WinUserX.WM_RBUTTONUP:
						mouseState.setRDown(false);
						break;
					case WinUserX.WM_MOUSEMOVE:
						mouseState.setPosX(info.pt.x);
						mouseState.setPosY(info.pt.y);
						break;
					}
				}
				return user32
					.CallNextHookEx(hhk, nCode, wParam, info.getPointer());
			}
		};
		hhk = user32.SetWindowsHookEx(WinUser.WH_MOUSE_LL, mouseHook, hMod, 0);
		new Thread() {
			public void run() {
				while (!quit) {
					try {
						Thread.sleep(10);
					} catch(InterruptedException e) {}
				}
				System.out.println("unhook and exit");
				user32.UnhookWindowsHookEx(hhk);
			}
		}.start();

		// This bit never returns from GetMessage
		int result;
		MSG msg = new MSG();
		while ((result = user32.GetMessage(msg, null, 0, 0)) != 0) {
			if (result == -1) {
				System.err.println("error in get message");
				break;
			}
			else {
				System.err.println("got message");
				user32.TranslateMessage(msg);
				user32.DispatchMessage(msg);
			}
		}
		user32.UnhookWindowsHookEx(hhk);
	}

	public static void stopHook() {
		quit = true;
	}

	public static MouseState getMouseState() {
		return mouseState;
	}

}
