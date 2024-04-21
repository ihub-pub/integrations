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
package pub.ihub.integration.agent.core.loader;

import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 代理增强实例加载器
 *
 * @author henry
 * @since 2024/3/30
 */
public class EnhancerInstanceLoader {

	private static final ConcurrentHashMap<String, Object> INSTANCE_CACHE = new ConcurrentHashMap<>();
	private static final ReentrantLock INSTANCE_LOAD_LOCK = new ReentrantLock();
	private static final Map<ClassLoader, ClassLoader> EXTEND_PLUGIN_CLASSLOADERS = new HashMap<>();

	@SneakyThrows
	public static <T> T load(String className, ClassLoader targetClassLoader) {
		if (targetClassLoader == null) {
			targetClassLoader = EnhancerInstanceLoader.class.getClassLoader();
		}
		String instanceKey = className + "_OF_" + targetClassLoader.getClass()
			.getName() + "@" + Integer.toHexString(targetClassLoader.hashCode());

		Object inst = INSTANCE_CACHE.get(instanceKey);
		if (inst == null) {
			ClassLoader pluginLoader;
			INSTANCE_LOAD_LOCK.lock();
			try {
				pluginLoader = EXTEND_PLUGIN_CLASSLOADERS.get(targetClassLoader);
				if (pluginLoader == null) {
					pluginLoader = new AgentPluginClassLoader(targetClassLoader);
					EXTEND_PLUGIN_CLASSLOADERS.put(targetClassLoader, pluginLoader);
				}
			} finally {
				INSTANCE_LOAD_LOCK.unlock();
			}
			inst = Class.forName(className, true, pluginLoader).getDeclaredConstructor().newInstance();
			INSTANCE_CACHE.put(instanceKey, inst);
		}

		return (T) inst;
	}

}
