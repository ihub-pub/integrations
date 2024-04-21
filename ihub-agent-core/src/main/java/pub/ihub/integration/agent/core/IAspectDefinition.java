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

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;

/**
 * 切面定义
 *
 * @author henry
 * @since 2024/3/30
 */
public interface IAspectDefinition {

	/**
	 * 定义需增强的类
	 *
	 * @return 类全称
	 */
	ElementMatcher.Junction<TypeDescription> enhanceClass();

	/**
	 * 定义需增强的方法
	 *
	 * @return 方法匹配器
	 */
	ElementMatcher<MethodDescription> getMethodsMatcher();

	/**
	 * 切面增强类
	 *
	 * @return 表示类名，类实例必须是 IAspectEnhancer 的实例。
	 */
	String getMethodsEnhancer();

}
