package de.yadrone.base.navdata;

/**
 * @brief Values for the detection type on drone cameras.
 */
public enum HDVideoState {
	STORAGE_FIFO_IS_FULL(1 << 0), USBKEY_IS_PRESENT(1 << 8), USBKEY_IS_RECORDING(1 << 9), USBKEY_IS_FULL(1 << 10);

	public final int value;

	private HDVideoState(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static HDVideoState fromInt(int v) {
		switch (v) {
		case 1 << 0:
			return STORAGE_FIFO_IS_FULL;
		case 1 << 8:
			return USBKEY_IS_PRESENT;
		case 1 << 9:
			return USBKEY_IS_RECORDING;
		case 1 << 10:
			return USBKEY_IS_FULL;
		}
		return null;
	}
}