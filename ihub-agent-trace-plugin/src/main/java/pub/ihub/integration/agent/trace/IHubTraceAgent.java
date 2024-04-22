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
package pub.ihub.integration.agent.trace;

import io.opentelemetry.sdk.trace.export.SpanExporter;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import pub.ihub.integration.agent.core.IHubEnhancer;
import pub.ihub.integration.agent.core.IHubTransformer;
import pub.ihub.integration.agent.trace.context.IHubTraceContext;
import pub.ihub.integration.agent.trace.enhancer.TomcatEnhancer;
import pub.ihub.integration.core.Logger;

import java.lang.instrument.Instrumentation;
import java.util.*;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author henry
 * @since 2024/4/21
 */
public class IHubTraceAgent {

	/**
	 * v1
	 */
	private static final String TRANSFORMER_V_1 = "v1";
	/**
	 * v2
	 */
	private static final String TRANSFORMER_V_2 = "v2";

	/**
	 * The premain method to load the ihub agent
	 *
	 * @param agentArgs the agent arguments
	 * @param inst      the instrumentation instance
	 */
	public static void premain(String agentArgs, Instrumentation inst) {
		Logger.info("The ihub agent start to load...");

		// load the tracing context
		List<SpanExporter> spanExporterList = new ArrayList<>();
		spanExporterList.add(new LoggingSpanExporter());
		IHubTraceContext.initTraceContext(spanExporterList);

		// load the tracing aspect
		final ByteBuddy byteBuddy = new ByteBuddy().with(TypeValidation.of(false));

		AgentBuilder agentBuilder = new AgentBuilder.Default(byteBuddy).ignore(
			nameStartsWith("net.bytebuddy.")
				.or(nameStartsWith("org.slf4j."))
				.or(nameStartsWith("org.groovy."))
				.or(nameContains("javassist"))
				.or(nameContains(".asm."))
				.or(nameContains(".reflectasm."))
				.or(nameStartsWith("sun.reflect"))
				.or(ElementMatchers.isSynthetic()));

		Listener listener = new Listener();

		Properties systemProperties = System.getProperties();
		String transformerVer = systemProperties.getProperty("agent.transformer.version");
		if (transformerVer == null) {
			transformerVer = TRANSFORMER_V_1;
		}

		if (TRANSFORMER_V_1.equals(transformerVer)) {
			List<IHubEnhancer> enhancerList = new ArrayList<>();
			enhancerList.add(new TomcatEnhancer());

			Logger.info("load transformer v1.");
			for (IHubEnhancer enhancer : enhancerList) {
				ElementMatcher.Junction matcher = enhancer.enhanceClass();
				agentBuilder.type(matcher)
					.transform(IHubTransformer.build(enhancer))
					.with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
					.with(listener)
					.installOn(inst);
			}
		} else if (TRANSFORMER_V_2.equals(transformerVer)) {
			Map<String, String> aspectContexts = new HashMap<>(1);
			aspectContexts.put("org.apache.catalina.core.StandardHostValve", "pub.ihub.integration.agent.trace.enhancer.TomcatEnhancer");

			Logger.info("load transformer v2.");
			for (Map.Entry<String, String> aspectEntry : aspectContexts.entrySet()) {
				String enhanceAspect = aspectEntry.getKey();
				String enhanceClass = aspectEntry.getValue();
				ElementMatcher.Junction matcher = named(enhanceAspect).and(not(isInterface()));
				agentBuilder.type(matcher)
					.transform(IHubTransformer.build(enhanceClass))
					.with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
					.with(listener)
					.installOn(inst);
			}
		} else {
			Map<String, String> aspectContexts = new HashMap<>(1);
			aspectContexts.put("org.apache.catalina.core.StandardHostValve", "pub.ihub.integration.agent.trace.enhancer.TomcatEnhancer");

			Logger.info("load transformer v3.");
			for (Map.Entry<String, String> aspectEntry : aspectContexts.entrySet()) {
				String enhanceClass = aspectEntry.getKey();
				String enhanceAspect = aspectEntry.getValue();
				ElementMatcher.Junction matcher = named(enhanceClass).and(not(isInterface()));
				agentBuilder.type(matcher)
					.transform(IHubTransformer.buildWithEnhancerInstanceLoader(enhanceAspect))
					.with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
					.with(listener)
					.installOn(inst);
			}
		}

		Logger.info("The ihub agent has been loaded.");
	}

}
