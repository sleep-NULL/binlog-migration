package com.sleep.binlog.protocol.event;

/**
 * @author huangyafeng
 * 
 *         <a href=
 *         "http://dev.mysql.com/doc/internals/en/binlog-event-type.html">
 */
public enum EVENT_TYPE {

	UNKNOWN_EVENT(0x00), START_EVENT_V3(0x01), QUERY_EVENT(0x02), STOP_EVENT(0x03), ROTATE_EVENT(0x04), INTVAR_EVENT(
			0x05), LOAD_EVENT(0x06), SLAVE_EVENT(0x07), CREATE_FILE_EVENT(0x08), APPEND_BLOCK_EVENT(
					0x09), EXEC_LOAD_EVENT(0x0a), DELETE_FILE_EVENT(0x0b), NEW_LOAD_EVENT(0x0c), RAND_EVENT(
							0x0d), USER_VAR_EVENT(0x0e), FORMAT_DESCRIPTION_EVENT(0x0f), XID_EVENT(
									0x10), BEGIN_LOAD_QUERY_EVENT(0x11), EXECUTE_LOAD_QUERY_EVENT(
											0x12), TABLE_MAP_EVENT(0x13), WRITE_ROWS_EVENTv0(0x14), UPDATE_ROWS_EVENTv0(
													0x15), DELETE_ROWS_EVENTv0(0x16), WRITE_ROWS_EVENTv1(
															0x17), UPDATE_ROWS_EVENTv1(0x18), DELETE_ROWS_EVENTv1(
																	0x19), INCIDENT_EVENT(0x1a), HEARTBEAT_EVENT(
																			0x1b), IGNORABLE_EVENT(
																					0x1c), ROWS_QUERY_EVENT(
																							0x1d), WRITE_ROWS_EVENTv2(
																									0x1e), UPDATE_ROWS_EVENTv2(
																											0x1f), DELETE_ROWS_EVENTv2(
																													0x20), GTID_EVENT(
																															0x21), ANONYMOUS_GTID_EVENT(
																																	0x22), PREVIOUS_GTIDS_EVENT(
																																			0x23);

	private int type;

	public int getType() {
		return type;
	}

	private EVENT_TYPE(int type) {
		this.type = type;
	}

	public static EVENT_TYPE valueOf(int type) {
		return EVENT_TYPE.values()[type];
	}

}
