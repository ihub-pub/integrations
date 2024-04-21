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
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.Morph;
import pub.ihub.integration.agent.core.transformer.IHubTransformerWithClassLoader;
import pub.ihub.integration.agent.core.transformer.IHubTransformerWithEnhancer;
import pub.ihub.integration.agent.core.transformer.IHubTransformerWithEnhancerInstanceLoader;
import pub.ihub.integration.core.Logger;

import static net.bytebuddy.matcher.ElementMatchers.isStatic;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * 通过当前Transformer对类进行字节码增强
 *
 * @author henry
 * @since 2024/4/1
 */
public interface IHubTransformer extends AgentBuilder.Transformer {

	/**
	 * 对类进行字节码增强
	 *
	 * @param builder  类构建器
	 * @param enhancer 增强器
	 * @return 增强后的类构建器
	 */
	static DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, IHubEnhancer enhancer) {
		if (enhancer == null) {
			Logger.error("enhancer is null");
			return null;
		}

		return builder.method(not(isStatic()).and(enhancer.getMethodsMatcher()))
			.intercept(MethodDelegation.withDefaultConfiguration()
				.withBinders(Morph.Binder.install(IHubOverrideCallable.class))
				.to(IHubTransformer.buildEnhancerProxy(enhancer)));
	}

	/**
	 * 构建增强器代理
	 *
	 * @param enhancer 增强器
	 * @return 增强器代理
	 */
	private static IHubEnhancerProxy buildEnhancerProxy(IAspectEnhancer enhancer) {
		IHubEnhancerProxy proxy = new IHubEnhancerProxy();
		proxy.setEnhancer(enhancer);
		return proxy;
	}

	/**
	 * 构建增强器
	 *
	 * @param enhancer 增强器
	 * @return 增强器
	 */
	static IHubTransformer build(IHubEnhancer enhancer) {
		return new IHubTransformerWithEnhancer(enhancer);
	}

	/**
	 * 构建增强器
	 *
	 * @param enhanceClass 增强类
	 * @return 增强器
	 */
	static IHubTransformer build(String enhanceClass) {
		return new IHubTransformerWithClassLoader(enhanceClass);
	}

	/**
	 * 构建增强器
	 *
	 * @param aspectClass 切面类
	 * @return 增强器
	 */
	static IHubTransformer buildWithEnhancerInstanceLoader(String aspectClass) {
		return new IHubTransformerWithEnhancerInstanceLoader(aspectClass);
	}

}
