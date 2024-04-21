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
package pub.ihub.integration.agent.trace.enhancer;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapGetter;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.matcher.ElementMatcher;
import org.apache.catalina.connector.Request;
import pub.ihub.integration.agent.core.IHubEnhancer;
import pub.ihub.integration.agent.trace.context.IHubTraceContext;
import pub.ihub.integration.core.Logger;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * @author henry
 * @since 2024/4/21
 */
public class TomcatEnhancer implements IHubEnhancer {

	private static final String ENHANCE_CLASS = "org.apache.catalina.core.StandardHostValve";
	private static final String ENHANCE_METHOD = "invoke";

	private static ThreadLocal<Span> spanThreadLocal = new ThreadLocal<>();

	@Override
	public ElementMatcher.Junction enhanceClass() {
		return named(ENHANCE_CLASS).and(not(isInterface()));
	}

	@Override
	public ElementMatcher<MethodDescription> getMethodsMatcher() {
		return named(ENHANCE_METHOD);
	}

	@Override
	public String getMethodsEnhancer() {
		return getClass().getCanonicalName();
	}

	@Override
	public void beforeMethod(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object result) throws Throwable {
		Logger.info("[trace]beforeMethod(), method = %s.%s", method.getDeclaringClass().getName(), method.getName());
		Request request = (Request) allArguments[0];

		TextMapGetter<Request> getter =
			new TextMapGetter<Request>() {
				@Override
				public String get(Request carrier, String key) {
					return carrier.getHeader(key);
				}

				@Override
				public Iterable<String> keys(Request carrier) {
					List<String> keys = new ArrayList<>();
					for (final Enumeration<String> headers = carrier.getParameterNames(); headers.hasMoreElements(); ) {
						final String name = headers.nextElement();
						keys.add(name);
					}
					return keys;
				}
			};

		Context extractedContext = IHubTraceContext.textPropagator().extract(Context.current(), request, getter);
		extractedContext.makeCurrent();

		String operationName = String.format("%s %s", request.getMethod(), request.getRequestURI());
		Span span = IHubTraceContext.tracer().spanBuilder(operationName)
			.setSpanKind(SpanKind.PRODUCER)
			.startSpan()
			.setAttribute("URL", request.getRequestURL().toString())
			.setAttribute("METHOD", request.getMethod())
			.setAttribute("URI", request.getRequestURI())
			.setAttribute("RemoteAddr", request.getRemoteAddr())
			.setAttribute("RemoteHost", request.getRemoteHost())
			.setAttribute("RemotePort", request.getRemotePort())
			.setAttribute("RemoteUser", request.getRemoteUser())
			.setAttribute("Component", "Tomcat");
		span.makeCurrent();
		spanThreadLocal.set(span);
	}

	@Override
	public Object afterMethod(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Object result) throws Throwable {
		Logger.info("[trace]afterMethod(), method = %s.%s", method.getDeclaringClass().getName(), method.getName());
		endCurrenSpan();
		return null;
	}

	@Override
	public void handleMethodException(Object objInst, Method method, Object[] allArguments, Class<?>[] argumentsTypes, Throwable t) {
		Logger.info("[trace]handleMethodException(), method = %s.%s", method.getDeclaringClass().getName(), method.getName());
		endCurrenSpan();
	}

	private void endCurrenSpan() {
		Span span = spanThreadLocal.get();
		if (span != null) {
			span.end();
			spanThreadLocal.remove();
		}
	}

}
