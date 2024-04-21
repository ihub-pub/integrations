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
package pub.ihub.integration.agent.trace.context;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import pub.ihub.integration.core.Logger;

import java.util.List;

/**
 * 跟踪上下文
 *
 * @author henry
 * @since 2024/3/30
 */
public class IHubTraceContext {

	private static Tracer TracerInst;
	private static OpenTelemetry TelemetryInst;

	public static void initTraceContext(List<SpanExporter> spanExporters) {
		Logger.info("The IHub core context is initializing...");

		SdkTracerProviderBuilder builder = SdkTracerProvider.builder();

		for (SpanExporter exporter : spanExporters) {
			builder.addSpanProcessor(BatchSpanProcessor.builder(exporter).build());
		}
		SdkTracerProvider tracerProvider = builder.build();

		TelemetryInst = OpenTelemetrySdk.builder()
			.setTracerProvider(tracerProvider)
			.setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
			.buildAndRegisterGlobal();

		Logger.info("The IHub core context has been initialized");
	}

	public static Tracer tracer() {
		if (TracerInst == null) {
			synchronized (IHubTraceContext.class) {
				// TODO: 2024/3/30 通过配置文件加载
				TracerInst = TelemetryInst.getTracer("demo", "1.0.0");
			}
		}
		return TracerInst;
	}

	public static TextMapPropagator textPropagator() {
		return TelemetryInst.getPropagators().getTextMapPropagator();
	}

}
