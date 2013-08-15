package de.yadrone.base.command;

/* Very little is documented about the CTRL command and modes
 * See https://github.com/bklang/ARbDrone/wiki/UndocumentedCommands
 */
public enum ControlMode {
	/* Doing nothing */
	NONE,
	/*
	 * Deprecated - Ardrone software update reception (update is done next run) After event completion, card should
	 * power off
	 */
	ARDRONE_UPDATE,
	/*
	 * Ardrone PIC software update reception (update is done next run) After event completion, card should power off
	 */
	PIC_UPDATE,
	/*
	 * Send previous run's logs
	 */
	LOGS_GET,
	/* Send active configuration file to a client through the 'control' socket UDP 5559 */
	CFG_GET,
	/* Reset command mask in navdata */
	ACK,
	/**/
	CUSTOM_CFG_GET;
}