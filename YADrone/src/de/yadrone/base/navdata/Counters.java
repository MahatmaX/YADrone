package de.yadrone.base.navdata;

public class Counters {
	private long double_tap_counter;
	private long finish_line_counter;

	public Counters(long double_tap_counter, long finish_line_counter) {
		super();
		this.double_tap_counter = double_tap_counter;
		this.finish_line_counter = finish_line_counter;
	}

	/**
	 * @return the double_tap_counter
	 */
	public long getNrOfDoubleTaps() {
		return double_tap_counter;
	}

	/**
	 * @return the finish_line_counter
	 */
	public long getNrOfFinishLines() {
		return finish_line_counter;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Counters [double_tap_counter=");
		builder.append(double_tap_counter);
		builder.append(", finish_line_counter=");
		builder.append(finish_line_counter);
		builder.append("]");
		return builder.toString();
	}

}
