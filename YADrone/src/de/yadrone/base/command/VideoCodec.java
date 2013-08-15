package de.yadrone.base.command;

public enum VideoCodec {
	NULL(0x00), UVLC(0x20), P264(0x40), MP4_360P(0x80), H264_360P(0x81), MP4_360P_H264_720P(0x82), H264_720P(0x83), MP4_360P_SLRS(
			0x84), H264_360P_SLRS(0x85), H264_720P_SLRS(0x86), H264_AUTO_RESIZE(0x87), MP4_360P_H264_360P(0x88);

	private int value;

	private VideoCodec(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}