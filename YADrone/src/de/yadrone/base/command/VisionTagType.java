package de.yadrone.base.command;

/**
 * @brief Values for the detection type on drone cameras.
 */
public enum VisionTagType {
	NONE, SHELL_TAG, ROUNDEL, ORIENTED_ROUNDEL, STRIPE, CAP, SHELL_TAG_V2, TOWER_SIDE, BLACK_ROUNDEL;

	public static VisionTagType fromInt(int v) {
		VisionTagType[] values = values();
		if (v < 0 || v > values.length) {
			return null;
		}
		return values[v];
	}

	public static int getMask(VisionTagType[] types) {
		int mask = 0;
		for (int n = 0; n < types.length; n++) {
			VisionTagType type = types[n];
			if (types[n] != NONE) {
				mask |= (1 << (type.ordinal() - 1));
			}
		}
		return mask;
	}

}