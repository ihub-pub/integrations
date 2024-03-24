/*
 * Copyright (c) 2021-2024 the original author or authors.
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
package pub.ihub.integration.boot;

import cn.hutool.core.map.MapUtil;
import org.junit.jupiter.api.*;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import pub.ihub.integration.core.Logger;

import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static pub.ihub.integration.boot.BaseConfigEnvironmentPostProcessor.CUSTOMIZE_PROPERTY_SOURCE_NAME;
import static pub.ihub.integration.boot.BaseConfigEnvironmentPostProcessor.ORDER;

/**
 * @author liheng
 */
@DisplayName("自定义文件集配置处理器测试")
class BaseConfigEnvironmentPostProcessorTest {

	public static final String PROPERTIES_PREFIX = "ihub";

	private static ConfigurableEnvironment environment;

	@BeforeAll
	public static void init() {
		environment = new StandardEnvironment();
	}

	@AfterAll
	public static void cleanup() {
		environment = null;
	}

	@BeforeEach
	public void tearUp() {
		Logger.info(Arrays.toString(environment.getActiveProfiles()));
		Logger.info(environment.getPropertySources().toString());
	}

	@AfterEach
	public void tearDown() {
		Logger.info(Arrays.toString(environment.getActiveProfiles()));
		Logger.info(environment.getPropertySources().toString());
	}

	@DisplayName("文件集配置处理器测试")
	@Test
	void postProcessEnvironment() {
		IHubConfig demoConfig = new IHubConfig();
		demoConfig.postProcessEnvironment(environment, null);
		assertEquals(ORDER, demoConfig.getOrder());
		assertTrue(Arrays.asList(environment.getActiveProfiles()).contains(PROPERTIES_PREFIX));

		CustomizeConfig customizeConfig = new CustomizeConfig();
		customizeConfig.postProcessEnvironment(environment, null);
		assertNotNull(environment.getPropertySources().get(CUSTOMIZE_PROPERTY_SOURCE_NAME));
	}

	private static class IHubConfig extends BaseConfigEnvironmentPostProcessor {

		@Override
		protected String getActiveProfile() {
			return PROPERTIES_PREFIX;
		}

	}

	private static class CustomizeConfig extends BaseConfigEnvironmentPostProcessor {

		@Override
		protected Map<String, Object> getCustomizeProperties() {
			return MapUtil.<String, Object>builder("pub.ihub.integration", "1.0.0").build();
		}

	}

}
