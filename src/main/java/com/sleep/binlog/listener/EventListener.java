package com.sleep.binlog.listener;

import com.sleep.binlog.protocol.entry.Entry;

/**
 * @author huangyafeng
 *
 */
public interface EventListener {
	
	public void onEentry(Entry entry);

}
