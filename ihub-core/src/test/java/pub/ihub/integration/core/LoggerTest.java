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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @author henry
 * @since 2024/3/30
 */
@DisplayName("日志测试")
class LoggerTest {

	@Test
	void info() {
		Logger.info("log info {}", "test");
	}

	@Test
	void warn() {
		Logger.warn("log warn {}", "test");
	}

	@Test
	void error() {
		Logger.error("log error {}", "test");
	}

	@Test
	void errorThrowable() {
		Logger.error("log error", new RuntimeException("test"));
	}

	@ParameterizedTest
	@ValueSource(booleans = {true, false})
	void debug(Boolean enableDebug) {
		Logger.ENABLE_DEBUG = enableDebug;
		Logger.debug("log debug {}", "test");
	}

}
