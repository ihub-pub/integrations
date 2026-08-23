/*
 * Copyright (c) 2026 the original author or authors.
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
package pub.ihub.integration.migrate.analyzer;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pub.ihub.integration.migrate.core.AnalysisReport;
import pub.ihub.integration.migrate.core.AnalysisResult;
import pub.ihub.integration.migrate.core.ProjectContext;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 旧系统分析 Demo：对 demo/legacy-app（Spring Boot 2.7 + Java 8 遗留项目）
 * 运行 IHub 迁移分析器，输出结构化迁移报告。
 *
 * <p>这是「真实项目分析」的可运行示例，演示
 * 解析构建文件 → 构建 ProjectContext → 运行规则 → 生成报告 的完整链路。
 */
class LegacySystemAnalysisDemoTest {

    @Test
    void analyzeLegacyAppAndPrintReport() throws Exception {
        File pom = locateFixturePom();
        assertTrue(pom.exists(), "fixture pom.xml should exist: " + pom);

        // 1. 解析遗留项目的 pom.xml
        Map<String, String> dependencies = parsePomDependencies(pom);
        String javaVersion = parseJavaVersion(pom);

        // 2. 构建项目上下文
        ProjectContext context = new ProjectContext(
            "legacy-app", pom.getParentFile().getAbsolutePath(),
            "maven", javaVersion, dependencies, Map.of());

        // 3. 运行分析（默认规则：过时依赖 + Java 版本）
        ProjectAnalyzer analyzer = new ProjectAnalyzer();
        AnalysisReport report = analyzer.analyze(context);

        // 4. 输出迁移报告
        printReport(report, context);

        // 5. 断言：遗留项目应当检出问题（Java 8 过低 / fastjson 1.x 过时等）
        assertFalse(report.results().isEmpty(), "report should contain rule results");
        assertTrue(report.results().stream().anyMatch(AnalysisResult::hasIssues),
            "legacy project should be flagged with issues");
    }

    private File locateFixturePom() {
        File dir = new File("").getAbsoluteFile();
        while (dir != null && !new File(dir, "demo/legacy-app/pom.xml").exists()) {
            dir = dir.getParentFile();
        }
        return new File(dir, "demo/legacy-app/pom.xml");
    }

    private Map<String, String> parsePomDependencies(File pom) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pom);
        String parentVersion = textOfFirst(doc, "parent", "version");

        Map<String, String> deps = new LinkedHashMap<>();
        NodeList nodes = doc.getElementsByTagName("dependency");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element dep = (Element) nodes.item(i);
            String group = childText(dep, "groupId");
            String artifact = childText(dep, "artifactId");
            String version = childText(dep, "version");
            if (version == null || version.isEmpty()) {
                version = parentVersion; // 由 parent BOM 管理
            }
            if (group != null && artifact != null && version != null) {
                deps.put(group + ":" + artifact, version);
            }
        }
        return deps;
    }

    private String parseJavaVersion(File pom) throws Exception {
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pom);
        NodeList nodes = doc.getElementsByTagName("java.version");
        return nodes.getLength() > 0 ? nodes.item(0).getTextContent() : null;
    }

    private String textOfFirst(Document doc, String parentTag, String childTag) {
        NodeList parents = doc.getElementsByTagName(parentTag);
        if (parents.getLength() == 0) {
            return null;
        }
        return childText((Element) parents.item(0), childTag);
    }

    private String childText(Element parent, String tag) {
        NodeList nodes = parent.getElementsByTagName(tag);
        return nodes.getLength() > 0 ? nodes.item(0).getTextContent() : null;
    }

    private void printReport(AnalysisReport report, ProjectContext ctx) {
        System.out.println("""

            # 迁移分析报告（Demo：demo/legacy-app）

            ## 项目概况
            - 项目名：%s · 构建工具：%s · Java：%s
            """.formatted(ctx.projectName(), ctx.buildTool(), ctx.javaVersion()));
        ctx.dependencies().forEach((k, v) -> System.out.println("  - " + k + " : " + v));
        System.out.println("\n## 发现的问题");
        for (AnalysisResult result : report.results()) {
            for (AnalysisResult.Issue issue : result.issues()) {
                System.out.printf("- [%s] %s%n    修复建议：%s%n",
                    issue.severity(), issue.description(), issue.fix());
            }
            result.suggestions().forEach(s -> System.out.println("  💡 " + s));
        }
    }
}
