package de.yadrone.base.navdata;

import java.util.Arrays;

public class TrackerData {
	private int[][][] trackers;

	public TrackerData(int[][][] trackers) {
		super();
		this.trackers = trackers;
	}

	/**
	 * getTrackers()[i][j][0]: locked <br>
	 * getTrackers()[i][j][1]: point.x <br>
	 * getTrackers()[i][j][2]: point.y
	 */
	public int[][][] getTrackers() {
		return trackers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrackersData [trackers=");
		for (int i = 0; i < trackers.length; i++) {
			builder.append("[");
			for (int j = 0; j < trackers[i].length; j++) {
				builder.append("[");
				builder.append(Arrays.toString(trackers[i][j]));
				builder.append("]");
			}
			builder.append("]");
		}
		builder.append("]");
		return builder.toString();
	}

}
