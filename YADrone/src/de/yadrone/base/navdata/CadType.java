package de.yadrone.base.navdata;

import java.util.ArrayList;

/**
 * @brief Values for the detection type on drone cameras.
 */
public enum CadType {
	/* Deprecated */
	HORIZONTAL,
	/* Deprecated */
	VERTICAL,
	/* Detection of 2D horizontal tags on drone shells */
	VISION,
	/* Detection disabled */
	NONE,
	/* Detects a roundel under the drone */
	COCARDE,
	/* Detects an oriented roundel under the drone */
	ORIENTED_COCARDE,
	/* Detects a uniform stripe on the ground */
	STRIPE,
	/* Detects a roundel in front of the drone */
	H_COCARDE,
	/* Detects an oriented roundel in front of the drone */
	H_ORIENTED_COCARDE,
	/* The drone uses several detections at the same time */
	STRIPE_V,
	/* */
	MULTIPLE_DETECTION_MODE,
	/* Detects a Cap orange and green in front of the drone */
	TYPE_CAP,
	/* Detects the black and white roundel */
	ORIENTED_COCARDE_BW,
	/* Detects 2nd version of shell/tag in front of the drone */
	VISION_V2,
	/* Detect a tower side with the front camera */
	TOWER_SIDE;

	public static CadType fromInt(int v) {
		CadType[] values = values();
		if (v < 0 || v > values.length) {
			return null;
		}
		return values[v];
	}
	
	// TODO: can the mask contain more than 1 value?
	// if so, uncommment arraylist stuff and remove return statements
	public static CadType fromMask(int types) {
		CadType[] values = values();
		//ArrayList<VisionCadType> l = new ArrayList<VisionCadType>();
		for (int n = 0; n < values.length; n++) {
			if ((types & 1) != 0) {
				//l.add(values[n]);
				return values[n];
			}
			types >>>= 1;
		}
		//return (VisionCadType[])l.toArray();
		return null;
	}
}