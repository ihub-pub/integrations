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

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.bytebuddy.description.NamedElement;
import net.bytebuddy.description.annotation.AnnotationDescription.Builder;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import org.springframework.web.bind.annotation.RestController;
import pub.ihub.integration.bytebuddy.core.IHubTypes;
import pub.ihub.integration.bytebuddy.core.IHubTypesBuilder;
import pub.ihub.integration.core.Logger;

import java.util.Map;
import java.util.stream.Collectors;

import static net.bytebuddy.matcher.ElementMatchers.hasMethodName;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

/**
 * @author liheng
 * @since 2024/1/21
 */
@IHubTypes(annotations = RestController.class)
public class IHubControllerDocPlugin implements IHubDocPlugin {

	@Override
	public DynamicType.Builder<?> apply(IHubTypesBuilder builder, TypeDescription definitions, JavaClass javaClass) {
		builder = builder.annotateTypeIfMissing(Tag.class, defineTag(javaClass, definitions));

		for (MethodDescription.InDefinedShape method : definitions.getDeclaredMethods()) {
			if (method.isConstructor()) {
				continue;
			}
			Map<String, String> params = method.getParameters().stream().collect(Collectors.toMap(NamedElement.WithRuntimeName::getName, it -> it.getType().getTypeName()));

			for (JavaMethod m : javaClass.getMethods()) {
				if (m.getName().equals(method.getInternalName())) {
					if (m.getParameters().stream().allMatch(mp -> mp.getType().toString().equals(params.get(mp.getName())))) {
						String comment = m.getComment();
						if (null == comment || comment.isBlank()) {
							Logger.warn("Can't find method comments for " + method.getInternalName());
							continue;
						}
						Map<String, String> paramTags = m.getTagsByName("param").stream()
							.collect(Collectors.toMap(it -> it.getParameters().get(0),
								it -> String.join(" ", it.getParameters().subList(1, it.getParameters().size()))));
						builder = builder.annotateMethodWith(Builder.ofType(Operation.class)
							.define("summary", comment.replaceAll("\\n.*", "").trim())
							.define("description", comment.replaceAll("\\s|<.*>|@.*", "").trim())
							.build(), hasMethodName(method.getInternalName()));

						builder = builder.annotateMethodWith(Builder.ofType(ApiResponse.class)
							.define("responseCode", "200").define("description", "200").build(), hasMethodName(method.getInternalName()));

						for (JavaParameter parameter : m.getParameters()) {
							builder = builder.annotateMethodWith(Builder.ofType(Parameter.class)
								.define("name", parameter.getName())
								.define("description", defaultIfBlank(paramTags.get(parameter.getName()), parameter.getName()).trim())
								.build(), hasMethodName(method.getInternalName()));
						}
					}
				}
			}
		}
		return builder.conclude();
	}

}
