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
import com.thoughtworks.qdox.model.JavaField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import net.bytebuddy.description.annotation.AnnotationDescription.Builder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import pub.ihub.integration.bytebuddy.core.IHubTypes;
import pub.ihub.integration.bytebuddy.core.IHubTypesBuilder;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

/**
 * @author liheng
 * @since 2024/1/21
 */
@IHubTypes(annotations = Entity.class)
public class IHubEntityDocPlugin implements IHubDocPlugin {

	@Override
	public DynamicType.Builder<?> apply(IHubTypesBuilder builder, TypeDescription definitions, JavaClass javaClass) {
		builder = builder.annotateTypeIfMissing(Schema.class, defineSchema(javaClass, definitions));

		for (JavaField field : javaClass.getFields()) {
			builder = builder.annotateFieldWith(Builder.ofType(Schema.class)
				.define("description", defaultIfBlank(field.getComment(), field.getName()).trim())
				.build(), named(field.getName()));
		}
		return builder.conclude();
	}

}
