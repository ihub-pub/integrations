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

import java.lang.reflect.Method;

/**
 * 增强器接口
 *
 * @author henry
 * @since 2024/4/2
 */
public interface IHubEnhancer extends IAspectEnhancer, IAspectDefinition {

	/**
	 * 增强类默认实现
	 *
	 * @return 增强类匹配器
	 */
	@Override
	default ElementMatcher.Junction<TypeDescription> enhanceClass() {
		return null;
	}

	/**
	 * 增强方法默认实现
	 *
	 * @return 方法匹配器
	 */
	@Override
	default ElementMatcher<MethodDescription> getMethodsMatcher() {
		return null;
	}

	/**
	 * 增强方法默认实现
	 *
	 * @return 增强类名
	 */
	@Override
	default String getMethodsEnhancer() {
		return null;
	}

	/**
	 * 方法执行前
	 *
	 * @param objInst        实例
	 * @param method         方法
	 * @param allArguments   参数
	 * @param argumentsTypes 参数类型
	 * @param result         结果
	 * @throws Throwable 异常
	 */
	@Override
	default void beforeMethod(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object result) throws Throwable {

	}

	/**
	 * 方法执行后
	 *
	 * @param objInst        实例
	 * @param method         方法
	 * @param allArguments   参数
	 * @param argumentsTypes 参数类型
	 * @param ret            结果
	 * @return 结果
	 * @throws Throwable 异常
	 */
	@Override
	default Object afterMethod(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable {
		return null;
	}

	/**
	 * 方法执行异常
	 *
	 * @param objInst        实例
	 * @param method         方法
	 * @param allArguments   参数
	 * @param argumentsTypes 参数类型
	 * @param t              异常
	 */
	@Override
	default void handleMethodException(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {

	}

}
