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
package pub.ihub.integration.bytebuddy.core;

import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import pub.ihub.integration.core.Logger;

import java.util.Arrays;

/**
 * @author henry
 * @since 2024/1/21
 */
public interface IHubPlugin extends Plugin {

	String CGLIB_CLASS_SEPARATOR = "$$";

	/**
	 * 应用插件
	 *
	 * @param builder     类型构建器
	 * @param definitions 类型定义
	 * @return 类型构建器
	 */
	DynamicType.Builder<?> apply(IHubTypesBuilder builder, TypeDescription definitions);

	/**
	 * Applies this plugin to a given {@link DynamicType.Builder}.
	 *
	 * @param builder     The builder to use as a basis for the applied transformation.
	 * @param definitions The type being transformed.
	 * @param fileLocator A class file locator that can locate other types in the scope of the project.
	 * @return The transformed builder.
	 */
	@Override
	default DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription definitions,
										 ClassFileLocator fileLocator) {
		return apply(IHubTypesBuilder.of(builder), definitions);
	}

	/**
	 * Closes the plugin.
	 */
	@Override
	default void close() {
		Logger.debug("Closing plugin …");
	}

	/**
	 * Matches the given type description.
	 *
	 * @param description The type description to match.
	 * @return {@code true} if this plugin matches the given type description.
	 */
	@Override
	default boolean matches(TypeDescription description) {
		// Cglib 代理类不处理
		if (description.getTypeName().contains(CGLIB_CLASS_SEPARATOR)) {
			return false;
		}
		IHubTypes types = this.getClass().getAnnotation(IHubTypes.class);
		return matchAnnotations(description, types) || matchRecord(description, types);
	}

	/**
	 * Matches the given type description.
	 *
	 * @param description The type description to match.
	 * @param types       The types to match.
	 * @return {@code true} if this plugin matches the given type description.
	 */
	private static boolean matchAnnotations(TypeDescription description, IHubTypes types) {
		return description.getDeclaredAnnotations().asTypeList().stream()
			.anyMatch(it -> Arrays.stream(types.annotations()).anyMatch(it::isAssignableTo));
	}

	/**
	 * Matches the given type description.
	 *
	 * @param description The type description to match.
	 * @param types       The types to match.
	 * @return {@code true} if this plugin matches the given type description.
	 */
	private static boolean matchRecord(TypeDescription description, IHubTypes types) {
		return types.record() && description.isRecord();
	}

}
