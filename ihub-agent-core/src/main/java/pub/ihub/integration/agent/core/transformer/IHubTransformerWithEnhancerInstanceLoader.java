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
package pub.ihub.integration.agent.core.transformer;

import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.utility.JavaModule;
import pub.ihub.integration.agent.core.IAspectDefinition;
import pub.ihub.integration.agent.core.IHubEnhancer;
import pub.ihub.integration.agent.core.IHubTransformer;
import pub.ihub.integration.agent.core.loader.EnhancerInstanceLoader;
import pub.ihub.integration.core.Logger;

import java.security.ProtectionDomain;

/**
 * 通过当前Transformer对类进行字节码增强，通过EnhancerInstanceLoader创建enhancer实例，解决了NoClassDefFoundError问题。
 *
 * @author henry
 * @since 2024/4/21
 */
public record IHubTransformerWithEnhancerInstanceLoader(String aspectClass) implements IHubTransformer {

	@Override
	public DynamicType.Builder<?> transform(DynamicType.Builder<?> builder, TypeDescription typeDescription,
											ClassLoader loader, JavaModule module, ProtectionDomain protectionDomain) {
		Logger.info("transform with enhancer instance loader %s...", typeDescription.getTypeName());
		IHubEnhancer enhancer = null;
		try {
			IAspectDefinition aspectDefinition = EnhancerInstanceLoader.load(this.aspectClass, loader);
			enhancer = EnhancerInstanceLoader.load(aspectDefinition.getMethodsEnhancer(), loader);
		} catch (Exception e) {
			Logger.error("failed to initialized the proxy %s", e.toString());
		}

		return IHubTransformer.transform(builder, enhancer);
	}

}
