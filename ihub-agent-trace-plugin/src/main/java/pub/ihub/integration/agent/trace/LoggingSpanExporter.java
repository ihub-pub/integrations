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

import io.opentelemetry.sdk.common.CompletableResultCode;
import io.opentelemetry.sdk.common.InstrumentationLibraryInfo;
import io.opentelemetry.sdk.trace.data.SpanData;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import pub.ihub.integration.core.Logger;

import java.util.Collection;

/**
 * @author henry
 * @since 2024/4/21
 */
public class LoggingSpanExporter implements SpanExporter {

	@Override
	public CompletableResultCode export(Collection<SpanData> spans) {
		for (SpanData span : spans) {
			InstrumentationLibraryInfo instruInfo = span.getInstrumentationLibraryInfo();
			Logger.info("%s %s - %s",
				instruInfo.getName(),
				instruInfo.getVersion(),
				span.toString());
		}
		return CompletableResultCode.ofSuccess();
	}

	@Override
	public CompletableResultCode flush() {
		return null;
	}

	@Override
	public CompletableResultCode shutdown() {
		return null;
	}
}
