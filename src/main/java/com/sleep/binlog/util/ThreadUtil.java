package com.sleep.binlog.util;

import java.lang.Thread.UncaughtExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yafeng.huang
 *
 */
public class ThreadUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(ThreadUtil.class);
	
	public static void newThread(Runnable runnuable, String threadName) {
		Thread t = new Thread(runnuable, threadName);
		t.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				logger.error("Thread {} occur uncaught exception", t.getName(), e);
			}
		});
		t.start();
	}

}
