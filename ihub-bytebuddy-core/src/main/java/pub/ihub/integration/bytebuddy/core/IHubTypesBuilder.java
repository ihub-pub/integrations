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

import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.AsmVisitorWrapper;
import net.bytebuddy.asm.MemberAttributeExtension;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationList;
import net.bytebuddy.description.annotation.AnnotationSource;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import pub.ihub.integration.core.Logger;

import java.lang.annotation.Annotation;
import java.util.function.Function;
import java.util.stream.Stream;

import static lombok.AccessLevel.PRIVATE;
import static net.bytebuddy.matcher.ElementMatchers.not;

/**
 * A wrapper around a {@link Builder} to allow issuing bytecode manipulations working
 *
 * @author henry
 * @since 2024/1/21
 */
@RequiredArgsConstructor(access = PRIVATE, staticName = "of")
public class IHubTypesBuilder {

	private final Builder<?> builder;
	private final TypeDescription type;

	public static IHubTypesBuilder of(Builder<?> builder) {
		return of(builder, builder.toTypeDescription());
	}

	public final IHubTypesBuilder annotateTypeIfMissing(Class<? extends Annotation> annotation,
														Function<AnnotationDescription.Builder, AnnotationDescription.Builder> annotationDesc) {
		return addAnnotationIfMissing(annotation, annotationDesc);
	}

	@SafeVarargs
	public final IHubTypesBuilder annotateMethodWith(AnnotationDescription annotation,
													 ElementMatcher.Junction<MethodDescription> selector, Class<? extends Annotation>... filterAnnotations) {

		ElementMatcher.Junction<AnnotationSource> alreadyAnnotated = ElementMatchers.isAnnotatedWith(annotation.getAnnotationType());

		for (Class<? extends Annotation> filterAnnotation : filterAnnotations) {
			alreadyAnnotated = alreadyAnnotated.or(ElementMatchers.isAnnotatedWith(filterAnnotation));
		}

		AsmVisitorWrapper annotationSpec = new MemberAttributeExtension.ForMethod()
			.annotateMethod(annotation)
			.on(IHubPluginUtils.defaultMethodMapping(selector.and(not(alreadyAnnotated)), annotation));

		return IHubTypesBuilder.of(builder.visit(annotationSpec));
	}

	@SafeVarargs
	public final IHubTypesBuilder annotateFieldWith(Class<? extends Annotation> annotation,
													Function<AnnotationDescription.Builder, AnnotationDescription.Builder> annotationDesc,
													ElementMatcher.Junction<FieldDescription> selector, Class<? extends Annotation>... filterAnnotations) {
		ElementMatcher.Junction<AnnotationSource> alreadyAnnotated = ElementMatchers.isAnnotatedWith(annotation);

		for (Class<? extends Annotation> filterAnnotation : filterAnnotations) {
			alreadyAnnotated = alreadyAnnotated.or(ElementMatchers.isAnnotatedWith(filterAnnotation));
		}

		AnnotationDescription annotationDescription = IHubPluginUtils.getAnnotation(annotation, annotationDesc);
		AsmVisitorWrapper annotationSpec = new MemberAttributeExtension.ForField()
			.annotate(annotationDescription)
			.on(IHubPluginUtils.defaultMapping(selector.and(not(alreadyAnnotated)), annotationDescription));

		return IHubTypesBuilder.of(builder.visit(annotationSpec));
	}

	@SafeVarargs
	public final IHubTypesBuilder annotateFieldWith(AnnotationDescription annotation,
													ElementMatcher.Junction<FieldDescription> selector, Class<? extends Annotation>... filterAnnotations) {

		ElementMatcher.Junction<AnnotationSource> alreadyAnnotated = ElementMatchers.isAnnotatedWith(annotation.getAnnotationType());

		for (Class<? extends Annotation> filterAnnotation : filterAnnotations) {
			alreadyAnnotated = alreadyAnnotated.or(ElementMatchers.isAnnotatedWith(filterAnnotation));
		}

		AsmVisitorWrapper annotationSpec = new MemberAttributeExtension.ForField()
			.annotate(annotation)
			.on(IHubPluginUtils.defaultMapping(selector.and(not(alreadyAnnotated)), annotation));

		return IHubTypesBuilder.of(builder.visit(annotationSpec));
	}

	public Builder<?> conclude() {
		return builder;
	}

	private final IHubTypesBuilder addAnnotationIfMissing(Class<? extends Annotation> annotation,
														  Function<AnnotationDescription.Builder, AnnotationDescription.Builder> annotationDesc) {
		return addAnnotationIfMissing(a -> annotation, annotationDesc);
	}

	@SafeVarargs
	private final IHubTypesBuilder addAnnotationIfMissing(Function<TypeDescription, Class<? extends Annotation>> producer,
														  Function<AnnotationDescription.Builder, AnnotationDescription.Builder> annotationDesc,
														  Class<? extends Annotation>... exclusions) {

		AnnotationList existing = type.getDeclaredAnnotations();
		Class<? extends Annotation> annotation = producer.apply(type);

		String annotationName = IHubPluginUtils.abbreviate(annotation);

		if (existing.isAnnotationPresent(annotation)) {

			Logger.debug("Not adding @%s because type is already annotated with it.", annotationName);

			return this;
		}

		boolean existingFound = Stream.of(exclusions).anyMatch(it -> {

			boolean found = existing.isAnnotationPresent(it);

			if (found) {
				Logger.debug("Not adding @%s because type is already annotated with @%s.", annotationName,
					IHubPluginUtils.abbreviate(it));
			}

			return found;
		});

		if (existingFound) {
			return this;
		}

		Logger.debug("Adding @%s.", annotationName);

		return IHubTypesBuilder.of(builder.annotateType(IHubPluginUtils.getAnnotation(annotation, annotationDesc)));
	}

}
