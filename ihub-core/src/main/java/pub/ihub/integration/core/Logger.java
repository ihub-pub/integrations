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

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Predicate;

import static pub.ihub.integration.core.Logger.Level.*;

/**
 * 日志记录器
 *
 * @author liheng
 */
@NoArgsConstructor
public class Logger {

	public static Boolean ENABLE_DEBUG = Boolean.FALSE;
	private static Level LEVEL = INFO;

	public static void setLevel(Level level) {
		LEVEL = level;
	}

	public static void trace(String format, Object... args) {
		logMessage(TRACE, Level::isTraceEnabled, format, args);
	}

	public static void debug(String format, Object... args) {
		logMessage(DEBUG, Level::isDebugEnabled, format, args);
	}

	public static void info(String format, Object... args) {
		logMessage(INFO, Level::isInfoEnabled, format, args);
	}

	public static void warn(String format, Object... args) {
		logMessage(WARN, Level::isWarnEnabled, format, args);
	}

	public static void error(String format, Object... args) {
		logMessage(ERROR, Level::isErrorEnabled, format, args);
	}

	public static void error(String msg, Throwable throwable) {
		logMessage(ERROR, Level::isErrorEnabled, msg + " error: [%s]", throwable.getMessage());
	}

	private static void logMessage(Level level, Predicate<Level> checkLevel, String format, Object... args) {
		if (!checkLevel.test(LEVEL)) {
			return;
		}
		String threadName = Thread.currentThread().getName();

		Date nowTime = new Date(System.currentTimeMillis());
		SimpleDateFormat sdFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		String strTime = sdFormatter.format(nowTime);

		String msg = String.format(format, args);
		String info = String.format("[%s][%s][%s] %s", strTime, threadName, level.levelStr, msg);
		System.out.println(info);
	}

	/**
	 * 日志级别
	 */
	@RequiredArgsConstructor
	@ToString(exclude = "levelInt")
	@Getter
	public enum Level {

		/**
		 * NONE
		 */
		NONE(99, "NONE"),
		/**
		 * ERROR
		 */
		ERROR(40, "ERROR"),
		/**
		 * WARN
		 */
		WARN(30, "WARN"),
		/**
		 * INFO
		 */
		INFO(20, "INFO"),
		/**
		 * DEBUG
		 */
		DEBUG(10, "DEBUG"),
		/**
		 * TRACE
		 */
		TRACE(0, "TRACE");

		private final int levelInt;
		private final String levelStr;

		boolean isTraceEnabled() {
			return this.levelInt <= TRACE.levelInt;
		}

		boolean isDebugEnabled() {
			return this.levelInt <= DEBUG.levelInt;
		}

		boolean isInfoEnabled() {
			return this.levelInt <= INFO.levelInt;
		}

		boolean isWarnEnabled() {
			return this.levelInt <= WARN.levelInt;
		}

		boolean isErrorEnabled() {
			return this.levelInt <= ERROR.levelInt;
		}

	}

}
