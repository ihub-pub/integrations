/*
 * Copyright (c) 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pub.ihub.integration.core;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志记录器
 *
 * @author liheng
 */
public class Logger {

	public static Boolean ENABLE_DEBUG = Boolean.FALSE;

	public static void info(String format, Object... args) {
		String msg = String.format(format, args);
		info(msg);
	}

	public static void info(String msg) {
		logMessage(msg, "INFO");
	}

	public static void warn(String format, Object... args) {
		String msg = String.format(format, args);
		warn(msg);
	}

	public static void warn(String msg) {
		logMessage(msg, "WARN");
	}

	public static void error(String format, Object... args) {
		String msg = String.format(format, args);
		error(msg);
	}

	public static void error(String msg) {
		logMessage(msg, "ERROR");
	}

	public static void error(String msg, Throwable throwable) {
		logMessage(msg, "ERROR");
	}

	public static void debug(String format, Object... args) {
		if (ENABLE_DEBUG) {
			String msg = String.format(format, args);
			logMessage(msg, "DEBUG");
		}
	}

	private static void logMessage(String msg, String type) {
		String threadName = Thread.currentThread().getName();

		Date nowTime = new Date(System.currentTimeMillis());
		SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss-SSS");
		String strTime = sdFormatter.format(nowTime);

		String info = String.format("[%s][%s][%s] %s", strTime, threadName, type, msg);
		System.out.println(info);
	}

}
