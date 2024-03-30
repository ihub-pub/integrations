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
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperties;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationDescription.Builder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import pub.ihub.integration.bytebuddy.core.IHubTypes;
import pub.ihub.integration.bytebuddy.core.IHubTypesBuilder;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author liheng
 * @since 2024/1/21
 */
@IHubTypes(record = true)
public class IHubRecordDocPlugin implements IHubDocPlugin {

	@Override
	public DynamicType.Builder<?> apply(IHubTypesBuilder builder, TypeDescription definitions, JavaClass javaClass) {
		return builder.annotateTypeIfMissing(Schema.class, defineSchema(javaClass, definitions))
			.annotateTypeIfMissing(SchemaProperties.class, defineSchemaProperties(javaClass))
			.conclude();
	}

	private Function<Builder, Builder> defineSchemaProperties(JavaClass javaClass) {
		Map<String, String> paramTags = javaClass.getTagsByName("param").stream()
			.collect(Collectors.toMap(it -> it.getParameters().get(0),
				it -> String.join(" ", it.getParameters().subList(1, it.getParameters().size()))));
		return build -> build.defineAnnotationArray("value", TypeDescription.ForLoadedType.of(SchemaProperty.class),
			paramTags.entrySet().stream()
				.map(it -> Builder.ofType(SchemaProperty.class)
					.define("name", it.getKey())
					.define("schema", Builder.ofType(Schema.class).define("description", it.getValue()).build())
					.build()
				).toArray(AnnotationDescription[]::new));
	}

}
