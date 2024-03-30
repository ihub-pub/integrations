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

import lombok.SneakyThrows;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;
import pub.ihub.integration.core.Logger;

import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Thread.currentThread;
import static java.nio.file.Files.readString;

/**
 * Utility methods to be used from different {@link Plugin} implementations
 *
 * @author henry
 * @since 2024/1/31
 */
public final class IHubPluginUtils {

	static {
		Logger.ENABLE_DEBUG = Boolean.valueOf(getEnableDebug());
	}

	/**
	 * Returns whether the given {@link TypeDescription} is annotated with the given annotation.
	 *
	 * @param type           must not be {@literal null}.
	 * @param annotationType must not be {@literal null}.
	 * @return result of the check.
	 */
	public static boolean isAnnotatedWith(TypeDescription type, Class<?> annotationType) {

		return type.getDeclaredAnnotations()
			.asTypeList()
			.stream()
			.anyMatch(it -> it.isAssignableTo(annotationType));
	}

	/**
	 * Returns an {@link AnnotationDescription} for an empty (i.e. no attributes defined) annotation of the given type.
	 *
	 * @param type must not be {@literal null}.
	 * @return annotation description.
	 */
	public static AnnotationDescription getAnnotation(Class<? extends Annotation> type) {
		return getAnnotation(type, it -> it);
	}

	/**
	 * Returns an {@link AnnotationDescription} for an annotation of the given type with the given attributes.
	 *
	 * @param type           must not be {@literal null}.
	 * @param annotationDesc must not be {@literal null}.
	 * @return annotation description.
	 */
	public static AnnotationDescription getAnnotation(Class<? extends Annotation> type,
													  Function<AnnotationDescription.Builder, AnnotationDescription.Builder> annotationDesc) {
		return annotationDesc.apply(AnnotationDescription.Builder.ofType(type)).build();
	}

	/**
	 * Applies the given map of source type or annotation to annotation onto the given {@link DynamicType.Builder}.
	 *
	 * @param builder  the current {@link DynamicType.Builder}.
	 * @param type     the currently described type.
	 * @param mappings the annotation or type mappings.
	 * @return tyoe builder
	 */
	public static DynamicType.Builder<?> mapAnnotationOrInterfaces(DynamicType.Builder<?> builder, TypeDescription type,
																   Map<Class<?>, Class<? extends Annotation>> mappings) {

		for (Map.Entry<Class<?>, Class<? extends Annotation>> entry : mappings.entrySet()) {

			Class<?> source = entry.getKey();

			if (source.isAnnotation() ? isAnnotatedWith(type, source) : type.isAssignableTo(source)) {
				builder = addAnnotationIfMissing(entry.getValue(), builder, type);
			}
		}

		return builder;
	}

	/**
	 * Applies the given map of source type or annotation to annotation onto the given {@link DynamicType.Builder}.
	 *
	 * @param builder  the current {@link DynamicType.Builder}.
	 * @param type     the currently described type.
	 * @param mappings the annotation or type mappings.
	 * @param log      the log to write to.
	 * @return type builder
	 */
	public static ElementMatcher<FieldDescription> defaultMapping(ElementMatcher.Junction<FieldDescription> source,
																  AnnotationDescription annotation) {

		return it -> {

			boolean matches = source.matches(it);

			if (matches) {
				Logger.debug("Defaulting %s mapping to %s.", it.getName(), abbreviate(annotation));
			}

			return matches;
		};
	}

	/**
	 * Applies the given map of source type or annotation to annotation onto the given {@link DynamicType.Builder}.
	 *
	 * @param builder  the current {@link DynamicType.Builder}.
	 * @param type     the currently described type.
	 * @param mappings the annotation or type mappings.
	 * @param log      the log to write to.
	 * @return type builder
	 */
	public static ElementMatcher<MethodDescription> defaultMethodMapping(ElementMatcher.Junction<MethodDescription> source,
																		 AnnotationDescription annotation) {

		return it -> {

			boolean matches = source.matches(it);

			if (matches) {
				Logger.debug("Defaulting %s mapping to %s.", it.getName(), abbreviate(annotation));
			}

			return matches;
		};
	}

	/**
	 * Applies the given map of source type or annotation to annotation onto the given {@link DynamicType.Builder}.
	 *
	 * @param type the currently described type.
	 * @return type builder
	 */
	public static String abbreviate(Class<?> type) {
		return abbreviate(type.getName());
	}

	/**
	 * Applies the given map of source type or annotation to annotation onto the given {@link DynamicType.Builder}.
	 *
	 * @param type the currently described type.
	 * @return type builder
	 */
	public static String abbreviate(TypeDefinition type) {
		return abbreviate(type.getTypeName());
	}

	/**
	 * Applies the given map of source type or annotation to annotation onto the given {@link DynamicType.Builder}.
	 *
	 * @param method the currently described method.
	 * @return type builder
	 */
	public static String abbreviate(MethodDescription method) {

		ParameterList<?> parameters = method.getParameters();

		return abbreviate(method.getDeclaringType())
			.concat(".")
			.concat(method.getName())
			.concat("(")
			.concat(parameters.isEmpty() ? "" : "…")
			.concat(")");
	}

	/**
	 * Applies the given map of source type or annotation to annotation onto the given {@link DynamicType.Builder}.
	 *
	 * @param annotation the currently described annotation.
	 * @return type builder
	 */
	public static String abbreviate(AnnotationDescription annotation) {

		String annotationString = annotation.toString();
		int openParenthesisIndex = annotationString.indexOf("(");

		String annotationName = annotationString.substring(1, openParenthesisIndex);

		return "@"
			.concat(abbreviate(annotationName))
			.concat(annotationString.substring(openParenthesisIndex));
	}

	/**
	 * Applies the given map of source type or annotation to annotation onto the given {@link DynamicType.Builder}.
	 *
	 * @param type the currently described type.
	 * @return type builder
	 */
	public static String abbreviate(String fullyQualifiedTypeName) {

		String abbreviatedPackage = Arrays.stream(getPackageName(fullyQualifiedTypeName)
				.split("\\."))
			.map(it -> it.substring(0, 1))
			.collect(Collectors.joining("."));

		return abbreviatedPackage.concat(getShortName(fullyQualifiedTypeName));
	}

	/**
	 * Applies the given map of source type or annotation to annotation onto the given {@link DynamicType.Builder}.
	 *
	 * @param type the currently described type.
	 * @return type builder
	 */
	@SafeVarargs
	public static DynamicType.Builder<?> addAnnotationIfMissing(Class<? extends Annotation> annotation, DynamicType.Builder<?> builder,
																TypeDescription type, Class<? extends Annotation>... exclusions) {
		return addAnnotationIfMissing(a -> annotation, builder, type, exclusions);
	}

	/**
	 * Applies the given map of source type or annotation to annotation onto the given {@link DynamicType.Builder}.
	 *
	 * @param type the currently described type.
	 * @return type builder
	 */
	@SafeVarargs
	public static DynamicType.Builder<?> addAnnotationIfMissing(Function<TypeDescription, Class<? extends Annotation>> producer,
																DynamicType.Builder<?> builder, TypeDescription type, Class<? extends Annotation>... exclusions) {

		AnnotationList existing = type.getDeclaredAnnotations();
		Class<? extends Annotation> annotation = producer.apply(type);

		String annotationName = IHubPluginUtils.abbreviate(annotation);

		boolean existingFound = Stream.of(exclusions).anyMatch(it -> {

			boolean found = existing.isAnnotationPresent(it);

			if (found) {
				Logger.debug("Not adding @%s because type is already annotated with @%s.", annotationName,
					IHubPluginUtils.abbreviate(it));
			}

			return found;
		});

		if (existingFound) {
			return builder;
		}

		Logger.debug("Adding @%s.", annotationName);

		return builder.annotateType(getAnnotation(annotation));
	}

	/**
	 * Applies the given map of source type or annotation to annotation onto the given {@link DynamicType.Builder}.
	 *
	 * @param type the currently described type.
	 * @return type builder
	 */
	private static DynamicType.Builder<?> addAnnotationIfMissing(Class<? extends Annotation> annotation, DynamicType.Builder<?> builder,
																 TypeDescription type) {

		if (isAnnotatedWith(type, annotation)) {
			Logger.debug("Not adding @%s, already present.", IHubPluginUtils.abbreviate(annotation));
			return builder;
		}

		Logger.debug("Adding @%s.", IHubPluginUtils.abbreviate(annotation));

		return builder.annotateType(getAnnotation(annotation));
	}

	/**
	 * Returns the package name of the given fully qualified type name.
	 *
	 * @param fullyQualifiedTypeName must not be {@literal null}.
	 * @return the package name.
	 */
	private static String getPackageName(String fullyQualifiedTypeName) {

		int lastDotIndex = fullyQualifiedTypeName.lastIndexOf('.');

		return lastDotIndex == -1 ? fullyQualifiedTypeName : fullyQualifiedTypeName.substring(0, lastDotIndex);
	}

	/**
	 * Returns the short name of the given fully qualified type name.
	 *
	 * @param fullyQualifiedTypeName must not be {@literal null}.
	 * @return the short name.
	 */
	private static String getShortName(String fullyQualifiedTypeName) {

		int lastDotIndex = fullyQualifiedTypeName.lastIndexOf('.');

		return lastDotIndex == -1 ? fullyQualifiedTypeName
			: fullyQualifiedTypeName.substring(lastDotIndex, fullyQualifiedTypeName.length());
	}

	/**
	 * Returns the enable debug.
	 *
	 * @return the enable debug
	 */
	@SneakyThrows
	private static String getEnableDebug() {
		return readString(getIntegrationonfigPath().resolve("enable-debug"));
	}

	/**
	 * Returns the enable debug.
	 *
	 * @return the enable debug
	 */
	private static Path findGradleConfigPath(Path basePath) {
		return basePath.endsWith(".gradle") ? basePath : findGradleConfigPath(basePath.getParent());
	}

	/**
	 * Returns the enable debug.
	 *
	 * @return the enable debug
	 */
	public static Path getIntegrationonfigPath() {
		String path = Objects.requireNonNull(currentThread().getContextClassLoader().getResource("")).getPath();
		Path gradleConfigPath = findGradleConfigPath(Path.of(path.replaceAll(".*:", "")));
		return gradleConfigPath.resolve("pub.ihub.plugin.integration");
	}

}
