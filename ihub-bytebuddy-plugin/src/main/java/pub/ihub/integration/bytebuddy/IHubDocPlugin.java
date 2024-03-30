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
package pub.ihub.integration.bytebuddy;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import lombok.SneakyThrows;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import pub.ihub.integration.bytebuddy.core.IHubPlugin;
import pub.ihub.integration.bytebuddy.core.IHubTypesBuilder;
import pub.ihub.integration.core.Logger;

import java.nio.file.Path;
import java.util.function.Function;

import static java.nio.file.Files.exists;
import static java.nio.file.Files.readAllLines;
import static pub.ihub.integration.bytebuddy.core.IHubPluginUtils.getIntegrationonfigPath;

/**
 * IHub 文档插件
 *
 * @author henry
 * @since 2024/2/4
 */
public interface IHubDocPlugin extends IHubPlugin {

	String MODULES_SOURCES_PATHS = "modules-sources-paths";

	/**
	 * 应用文档插件
	 *
	 * @param builder     类型构建器
	 * @param definitions 类型定义
	 * @param javaClass   java 类
	 * @return 类型构建器
	 */
	DynamicType.Builder<?> apply(IHubTypesBuilder builder, TypeDescription definitions, JavaClass javaClass);

	/**
	 * 应用文档插件
	 *
	 * @param builder     类型构建器
	 * @param definitions 类型定义
	 * @return 类型构建器
	 */
	@SneakyThrows
	@Override
	default DynamicType.Builder<?> apply(IHubTypesBuilder builder, TypeDescription definitions) {
		// 创建 java 项目 builder 对象
		JavaProjectBuilder projectBuilder = new JavaProjectBuilder();
		// 添加 java 源文件
		projectBuilder.addSource(findJavaPath(definitions.getInternalName()).toFile());
		// 获得解析后的类
		JavaClass javaClass = projectBuilder.getClassByName(definitions.getName());
		return apply(builder, definitions, javaClass);
	}

	/**
	 * 查找 java 文件路径
	 *
	 * @param classInternalName 类内部名称
	 * @return java 文件路径
	 */
	@SneakyThrows
	private static Path findJavaPath(String classInternalName) {
		for (String it : readAllLines(getIntegrationonfigPath().resolve(MODULES_SOURCES_PATHS))) {
			Path path = Path.of(it.replaceAll(".*:", "")).resolve(classInternalName + ".java");
			if (exists(path)) {
				return path;
			}
		}
		throw new RuntimeException("Can't find java file for " + classInternalName);
	}

	/**
	 * 定义属性
	 *
	 * @param javaClass java 类
	 * @param type      类型描述
	 * @return 属性定义
	 */
	default Function<AnnotationDescription.Builder, AnnotationDescription.Builder> defineProperty(JavaClass javaClass, TypeDescription type) {
		return build -> {
			String comment = javaClass.getComment();
			if (null == comment || comment.isBlank()) {
				Logger.warn("Can't find comments for " + type.getInternalName());
				return build;
			}
			return build.define("description", comment.replaceAll("\\s|<.*>|@.*", "").trim());
		};
	}

	/**
	 * 定义架构
	 *
	 * @param comment 注释
	 * @param type    类型
	 * @return 架构定义
	 */
	default Function<AnnotationDescription.Builder, AnnotationDescription.Builder> defineSchema(String comment, String type) {
		return build -> {
			if (null == comment || comment.isBlank()) {
				Logger.warn("Can't find comments for " + type);
				return build;
			}
			return build.define("description", comment.replaceAll("\\s|<.*>|@.*", "").trim());
		};
	}

	/**
	 * 定义架构
	 *
	 * @param comment 注释
	 * @return 架构定义
	 */
	default Function<AnnotationDescription.Builder, AnnotationDescription.Builder> defineSchema(String comment) {
		return defineSchema(comment, null);
	}

	/**
	 * 定义架构
	 *
	 * @param javaClass java 类
	 * @param type      类型描述
	 * @return 架构定义
	 */
	default Function<AnnotationDescription.Builder, AnnotationDescription.Builder> defineSchema(JavaClass javaClass, TypeDescription type) {
		return defineSchema(javaClass.getComment(), type.getInternalName());
	}

	/**
	 * 定义标签
	 *
	 * @param javaClass java 类
	 * @param type      类型描述
	 * @return 标签定义
	 */
	default Function<AnnotationDescription.Builder, AnnotationDescription.Builder> defineTag(JavaClass javaClass, TypeDescription type) {
		return build -> {
			String comment = javaClass.getComment();
			if (null == comment || comment.isBlank()) {
				Logger.warn("Can't find comments for " + type.getInternalName());
				return build;
			}
			return build.define("name", comment.replaceAll("\\n.*", "").trim())
				.define("description", comment.replaceAll("\\s|<.*>|@.*", "").trim());
		};
	}

}
