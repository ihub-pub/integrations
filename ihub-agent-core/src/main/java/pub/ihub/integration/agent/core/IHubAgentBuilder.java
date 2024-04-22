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
package pub.ihub.integration.agent.core;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.dynamic.scaffold.TypeValidation;

/**
 * 代理构建器
 *
 * @author henry
 * @since 2024/4/16
 */
public final class IHubAgentBuilder {

	/**
	 * 构建代理
	 *
	 * @return 代理构建器
	 */
	public static AgentBuilder build() {
		final ByteBuddy byteBuddy = new ByteBuddy().with(TypeValidation.of(false));
		return new AgentBuilder.Default(byteBuddy);
	}

}
