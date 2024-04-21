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
package pub.ihub.integration.agent.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author henry
 * @since 2024/3/30
 */
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.SOURCE)
public @interface AspectDefinition {

	String ENHANCE_METHOD = "invoke";

	/**
	 * 定义需增强的类
	 *
	 * @return 类全称
	 */
	String enhanceClass();

	/**
	 * 定义需增强的方法
	 *
	 * @return 方法名
	 */
	String enhanceMethod() default ENHANCE_METHOD;

	/**
	 * 切面增强类
	 *
	 * @return 表示类名，类实例必须是 IAspectEnhancer 的实例。
	 */
	String interceptClass();

}
