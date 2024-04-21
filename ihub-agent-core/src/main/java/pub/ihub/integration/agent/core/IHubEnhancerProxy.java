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

import net.bytebuddy.implementation.bind.annotation.*;
import pub.ihub.integration.core.Logger;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * 切面增强代理
 *
 * @author henry
 * @since 2024/4/1
 */
public class IHubEnhancerProxy {

	public IAspectEnhancer enhancer;

	@IgnoreForBinding
	public void setEnhancer(IAspectEnhancer enhancer) {
		this.enhancer = enhancer;
	}

	/**
	 * 使用 buddyByte 拦截目标实例方法
	 *
	 * @param obj          目标类实例
	 * @param allArguments 所有方法参数
	 * @param method       方法描述
	 * @param zuper        原始调用引用
	 * @return 目标实例方法的返回值
	 * @throws Exception 仅因zuper.call()或sky-walking中的意外异常而抛出异常（如果触发此条件，则这是一个错误）。
	 */
	@RuntimeType
	@BindingPriority(value = 1)
	public Object intercept(@This Object obj, @AllArguments Object[] allArguments, @SuperCall Callable<?> zuper,
							@Origin Method method) throws Throwable {
		Object result = null;
		try {
			enhancer.beforeMethod(obj, method, allArguments, method.getParameterTypes(), result);
		} catch (Throwable t) {
			Logger.error("IHubEnhancerProxy failure - beforeMethod, [%s].[%s], msg = %s", obj.getClass(), method.getName(), t.toString());
		}

		Object ret = null;
		try {
			if (null != result) {
				ret = result;
			} else {
				ret = zuper.call();
			}
		} catch (Throwable t) {
			try {
				enhancer.handleMethodException(obj, method, allArguments, method.getParameterTypes(), t);
			} catch (Throwable t2) {
				Logger.error("IHubEnhancerProxy failure - handleMethodException, [%s].[%s], msg = %s", obj.getClass(), method.getName(), t2.toString());
			}
			throw t;
		} finally {
			try {
				enhancer.afterMethod(obj, method, allArguments, method.getParameterTypes(), ret);
			} catch (Throwable t3) {
				Logger.error("IHubEnhancerProxy failure - afterMethod, [%s].[%s], msg = %s", obj.getClass(), method.getName(), t3.toString());
			}
		}

		return ret;
	}

}
