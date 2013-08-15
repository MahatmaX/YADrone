package de.yadrone.base.navdata;

import java.util.Arrays;

public class VideoStreamData {

	private byte quant;
	private int frame_size;
	private int frame_number;
	private int atcmd_ref_seq;
	private int atcmd_mean_ref_gap;
	private float atcmd_var_ref_gap;
	private int atcmd_ref_quality;
	private int out_bitrate;
	private int desired_bitrate;
	private int[] temp_data;
	private int tcp_queue_level;
	private int fifo_queue_level;

	public VideoStreamData(byte quant, int frame_size, int frame_number, int atcmd_ref_seq, int atcmd_mean_ref_gap,
			float atcmd_var_ref_gap, int atcmd_ref_quality, int out_bitrate, int desired_bitrate, int[] temp_data,
			int tcp_queue_level, int fifo_queue_level) {
		super();
		this.quant = quant;
		this.frame_size = frame_size;
		this.frame_number = frame_number;
		this.atcmd_ref_seq = atcmd_ref_seq;
		this.atcmd_mean_ref_gap = atcmd_mean_ref_gap;
		this.atcmd_var_ref_gap = atcmd_var_ref_gap;
		this.atcmd_ref_quality = atcmd_ref_quality;
		this.out_bitrate = out_bitrate;
		this.desired_bitrate = desired_bitrate;
		this.temp_data = temp_data;
		this.tcp_queue_level = tcp_queue_level;
		this.fifo_queue_level = fifo_queue_level;
	}

	/**
	 * @return the quant
	 */
	public byte getQuant() {
		return quant;
	}

	/**
	 * @return the frame_size
	 */
	public int getFrameSize() {
		return frame_size;
	}

	/**
	 * @return the frame_number
	 */
	public int getFrameNumber() {
		return frame_number;
	}

	/**
	 * @return the atcmd_ref_seq
	 */
	public int getAtcmdRefSeq() {
		return atcmd_ref_seq;
	}

	/**
	 * @return the atcmd_mean_ref_gap
	 */
	public int getAtcmdMeanRefGap() {
		return atcmd_mean_ref_gap;
	}

	/**
	 * @return the atcmd_var_ref_gap
	 */
	public float getAtcmdVarRefGap() {
		return atcmd_var_ref_gap;
	}

	/**
	 * @return the atcmd_ref_quality
	 */
	public int getAtcmdRefQuality() {
		return atcmd_ref_quality;
	}

	/**
	 * @return the out_bitrate
	 */
	public int getOutBitrate() {
		return out_bitrate;
	}

	/**
	 * @return the desired_bitrate
	 */
	public int getDesiredBitrate() {
		return desired_bitrate;
	}

	/**
	 * @return the temp_data
	 */
	public int[] getTempData() {
		return temp_data;
	}

	/**
	 * @return the tcp_queue_level
	 */
	public int getTcpQueueLevel() {
		return tcp_queue_level;
	}

	/**
	 * @return the fifo_queue_level
	 */
	public int getFifoQueueLevel() {
		return fifo_queue_level;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("VideoStreamData [quant=");
		builder.append(quant);
		builder.append(", frame_size=");
		builder.append(frame_size);
		builder.append(", frame_number=");
		builder.append(frame_number);
		builder.append(", atcmd_ref_seq=");
		builder.append(atcmd_ref_seq);
		builder.append(", atcmd_mean_ref_gap=");
		builder.append(atcmd_mean_ref_gap);
		builder.append(", atcmd_var_ref_gap=");
		builder.append(atcmd_var_ref_gap);
		builder.append(", atcmd_ref_quality=");
		builder.append(atcmd_ref_quality);
		builder.append(", out_bitrate=");
		builder.append(out_bitrate);
		builder.append(", desired_bitrate=");
		builder.append(desired_bitrate);
		builder.append(", temp_data=");
		builder.append(Arrays.toString(temp_data));
		builder.append(", tcp_queue_level=");
		builder.append(tcp_queue_level);
		builder.append(", fifo_queue_level=");
		builder.append(fifo_queue_level);
		builder.append("]");
		return builder.toString();
	}

}
