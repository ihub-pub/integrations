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

import java.lang.reflect.Method;

/**
 * 切面增强定义
 *
 * @author henry
 * @since 2024/3/30
 */
public interface IAspectEnhancer {

	/**
	 * 在目标方法执行前调用
	 *
	 * @param objInst        目标对象实例
	 * @param method         目标方法
	 * @param allArguments   方法参数
	 * @param argumentsTypes 方法参数类型
	 * @param result         如果要截断方法，请更改此结果。
	 * @throws Throwable 如果发生异常，则会中断方法调用。
	 */
	void beforeMethod(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object result) throws Throwable;

	/**
	 * 在目标方法执行后调用
	 *
	 * @param objInst        目标对象实例
	 * @param method         目标方法
	 * @param allArguments   方法参数
	 * @param argumentsTypes 方法参数类型
	 * @param ret            方法的原始返回值。如果方法触发异常，则可能为 null。
	 * @return 该方法的实际返回值。
	 * @throws Throwable 如果发生异常，则会中断方法调用。
	 */
	Object afterMethod(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object ret) throws Throwable;


	/**
	 * 在目标方法有异常时调用
	 *
	 * @param objInst        目标对象实例
	 * @param method         目标方法
	 * @param allArguments   方法参数
	 * @param argumentsTypes 方法参数类型
	 * @param t              异常
	 */
	void handleMethodException(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t);

}
