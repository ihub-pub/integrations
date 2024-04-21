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

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;
import pub.ihub.integration.core.Logger;

/**
 * 增强器监听器
 *
 * @author henry
 * @since 2024/4/1
 */
public interface IHubListener extends AgentBuilder.Listener {

	/**
	 * 增强类发现
	 *
	 * @param typeName    类名
	 * @param classLoader 类加载器
	 * @param module      模块
	 * @param loaded      是否已加载
	 */
	@Override
	default void onDiscovery(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
		Logger.trace("Enhanced class {%s} discovered, loaded = %s", typeName, loaded);
	}

	/**
	 * 增强类转换
	 *
	 * @param typeDescription 类描述
	 * @param classLoader     类加载器
	 * @param module          模块
	 * @param loaded          是否已加载
	 * @param dynamicType     动态类型
	 */
	@Override
	default void onTransformation(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded, DynamicType dynamicType) {
		Logger.trace("Enhanced class {%s} transformed, loaded = %s", typeDescription.getName(), loaded);
	}

	/**
	 * 增强类加载
	 *
	 * @param typeDescription 类描述
	 * @param classLoader     类加载器
	 * @param module          模块
	 * @param loaded          是否已加载
	 */
	@Override
	default void onIgnored(TypeDescription typeDescription, ClassLoader classLoader, JavaModule module, boolean loaded) {
		Logger.trace("Enhanced class {%s} ignored, loaded = %s", typeDescription, loaded);
	}

	/**
	 * 增强类错误
	 *
	 * @param typeName    类名
	 * @param classLoader 类加载器
	 * @param module      模块
	 * @param loaded      是否已加载
	 * @param throwable   异常
	 */
	@Override
	default void onError(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded, Throwable throwable) {
		Logger.trace("Enhanced class {%s} error, loaded = %s, exception msg = %s", typeName, loaded, throwable.getMessage());
	}

	/**
	 * 增强类完成
	 *
	 * @param typeName    类名
	 * @param classLoader 类加载器
	 * @param module      模块
	 * @param loaded      是否已加载
	 */
	@Override
	default void onComplete(String typeName, ClassLoader classLoader, JavaModule module, boolean loaded) {
		Logger.trace("Enhanced class {%s} completed, loaded = %s", typeName, loaded);
	}

}
