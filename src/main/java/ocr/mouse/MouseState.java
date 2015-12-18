package ocr.mouse;

import java.util.Observable;

public class MouseState extends Observable {

	private int posX = 0;
	private int posY = 0;
	private boolean isLDown = false;
	private boolean isRDown = false;

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
		setChanged();
		notifyObservers(MouseEvent.MOVE);
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
		setChanged();
		notifyObservers(MouseEvent.MOVE);
	}

	public boolean isLDown() {
		return isLDown;
	}

	public void setLDown(boolean isLDown) {
		this.isLDown = isLDown;
		setChanged();
		notifyObservers(isLDown ? MouseEvent.LDOWN : MouseEvent.LUP);
	}

	public boolean isRDown() {
		return isRDown;
	}

	public void setRDown(boolean isRDown) {
		this.isRDown = isRDown;
		setChanged();
		notifyObservers(isRDown ? MouseEvent.RDOWN : MouseEvent.RUP);
	}

	public boolean isDown(MouseButton mouseButton) {
		return (isLDown && mouseButton == MouseButton.LEFT)
			|| (isRDown && mouseButton == MouseButton.RIGHT)
			|| ((isLDown || isRDown) && mouseButton == MouseButton.BOTH);
	}

}
