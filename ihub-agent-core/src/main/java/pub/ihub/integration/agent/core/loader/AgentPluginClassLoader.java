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

import pub.ihub.integration.core.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 代理插件类加载器
 *
 * @author henry
 * @since 2024/3/30
 */
public class AgentPluginClassLoader extends ClassLoader {

	private static AgentPluginClassLoader DEFAULT_LOADER;
	// TODO: This path should be configurable
	private static final String pluginFilePath = "/tmp/ihub-agent-plugin.jar";

	private final List<File> classpath;
	private final List<Jar> allJars = new LinkedList<>();

	public static void initDefaultLoader() {
		if (DEFAULT_LOADER == null) {
			synchronized (AgentPluginClassLoader.class) {
				if (DEFAULT_LOADER == null) {
					DEFAULT_LOADER = new AgentPluginClassLoader(AgentPluginClassLoader.class.getClassLoader());
				}
			}
		}
	}

	public AgentPluginClassLoader(ClassLoader parent) {
		super(parent);
		classpath = new LinkedList<>();
		classpath.add(new File(pluginFilePath));
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (allJars.isEmpty()) {
			try {
				File file = new File(pluginFilePath);
				Jar jar = new Jar(new JarFile(file), file);
				allJars.add(jar);
			} catch (IOException e) {
				Logger.error("failed to load the plugin file, msg = %s", e.getMessage());
			}
		}

		String path = name.replace('.', '/').concat(".class");
		for (Jar jar : allJars) {
			JarEntry entry = jar.jarFile.getJarEntry(path);
			if (entry == null) {
				continue;
			}
			try {
				URL classFileUrl = new URL("jar:file:" + jar.sourceFile.getAbsolutePath() + "!/" + path);
				byte[] data;
				try (final BufferedInputStream is = new BufferedInputStream(
					classFileUrl.openStream()); final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
					int ch;
					while ((ch = is.read()) != -1) {
						baos.write(ch);
					}
					data = baos.toByteArray();
				}
				return defineClass(name, data, 0, data.length);
			} catch (IOException e) {
				Logger.error("find class fail, msg = %s", e.getMessage());
			}
		}

		throw new ClassNotFoundException("Can't find " + name);
	}

	private record Jar(JarFile jarFile, File sourceFile) {
	}

}
