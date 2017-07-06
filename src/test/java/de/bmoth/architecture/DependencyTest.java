package de.bmoth.architecture;

import guru.nidi.codeassert.config.AnalyzerConfig;
import guru.nidi.codeassert.dependency.DependencyRule;
import guru.nidi.codeassert.dependency.DependencyRuler;
import guru.nidi.codeassert.dependency.DependencyRules;
import guru.nidi.codeassert.model.ModelAnalyzer;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static guru.nidi.codeassert.junit.CodeAssertMatchers.*;
import static org.junit.Assert.assertThat;

public class DependencyTest {
    private String[] packages = new String[] { "app", "backend", "checkers", "eventbus", "modelchecker", "parser" };
    private AnalyzerConfig config;

    @Before
    public void setup() {
        // Analyze all sources in src/main/java
        config = new AnalyzerConfig();
        config = config.withSources(new File("src/main/java/de/bmoth"), packages);
        config = config.withClasses(new File("build/classes/main/de/bmoth"), packages);
    }

    @Test
    public void noCycles() {
        assertThat(new ModelAnalyzer(config).analyze(), hasNoClassCycles());

        // cycles in packages
        String[][] exceptions2DimArray = new String[][] {
                new String[] { "de.bmoth.parser", "de.bmoth.parser.ast", "de.bmoth.parser.cst" },
                new String[] { "de.bmoth.parser.ast.nodes", "de.bmoth.parser.ast.nodes.ltl" } };
        @SuppressWarnings("unchecked")
        Set<String>[] exceptions = Arrays.stream(exceptions2DimArray)
                .map(a -> Arrays.stream(a).collect(Collectors.toSet())).collect(Collectors.toList())
                .toArray((Set<String>[]) new Set<?>[exceptions2DimArray.length]);
        assertThat(new ModelAnalyzer(config).analyze(), hasNoPackgeCyclesExcept(exceptions));
    }

    @Test
    public void dependency() {
        class ExternalPackages extends DependencyRuler {
            private DependencyRule comMicrosoftZ3;
            private DependencyRule comMicrosoftZ3Enumerations;
            private DependencyRule deBmothApp;
            private DependencyRule deBmothBackendZ3;
            private DependencyRule deBmothModelchecker;
            private DependencyRule deBmothModelchecker_;
            private DependencyRule deBmothCheckers_;
            private DependencyRule deSaxsysMvvmfx;

            @Override
            public void defineRules() {
                deBmothApp.mayUse(comMicrosoftZ3, comMicrosoftZ3Enumerations, deSaxsysMvvmfx);
                deBmothBackendZ3.mustUse(comMicrosoftZ3);
                deBmothBackendZ3.mayUse(comMicrosoftZ3Enumerations);
                deBmothModelchecker.mustUse(comMicrosoftZ3);
                deBmothModelchecker_.mustUse(comMicrosoftZ3);
                deBmothCheckers_.mustUse(comMicrosoftZ3);
            }
        }

        class InternalPackages extends DependencyRuler {
            private DependencyRule deBmothParser;
            private DependencyRule deBmothParserAst;
            private DependencyRule deBmothParserAst_;
            private DependencyRule deBmothParserCst;
            private DependencyRule deBmothParserAstNodes;
            private DependencyRule deBmothParserAstNodesLtl;
            private DependencyRule deBmothParserAstTypes;
            private DependencyRule deBmothParserAstVisitors;

            @Override
            public void defineRules() {
                deBmothParserAst.mustUse(deBmothParserAst_, deBmothParserCst);
                deBmothParserCst.mustUse(deBmothParser);
                deBmothParserAstNodes.mustUse(deBmothParserAstTypes, deBmothParserAstNodesLtl);
                deBmothParserAstVisitors.mustUse(deBmothParserAstNodes, deBmothParserAstNodesLtl);
                deBmothParserAstNodesLtl.mustUse(deBmothParserAstNodes);
            }
        }

        class DeBmoth extends DependencyRuler {
            // $self is de.bmoth, added _ refers to subpackages of package
            private DependencyRule app;
            private DependencyRule antlr;
            private DependencyRule backend;
            private DependencyRule backend_;
            private DependencyRule checkers_;
            private DependencyRule eventbus;
            private DependencyRule modelchecker;
            private DependencyRule modelchecker_;
            private DependencyRule parser;
            private DependencyRule parser_;
            private DependencyRule preferences;
            private DependencyRule parserAst;

            @Override
            public void defineRules() {
                app.mayUse(backend_, checkers_, eventbus, modelchecker, modelchecker_, parser, parser_, preferences);

                backend_.mayUse(preferences, backend, backend_, parser, parser_);

                checkers_.mayUse(backend, backend_, parser, parser_);

                modelchecker.mayUse(backend, backend_, parser_);
                modelchecker_.mayUse(backend, backend_, modelchecker, parser_, preferences);

                parser.mayUse(antlr, parser_);
                parser_.mayUse(antlr);

                parserAst.mayUse(backend);
            }
        }

        // All dependencies are forbidden, except the ones defined above
        // java, javafx, com and org are ignored
        // maybe we should include com.microsoft again and check who is
        // allowed to directly speak to z3
        DependencyRules rules = DependencyRules.denyAll().withAbsoluteRules(new InternalPackages())
                .withAbsoluteRules(new ExternalPackages()).withRelativeRules(new DeBmoth())
                .withExternals("com.google.*", "java.*", "javafx.*", "org.*");

        assertThat(new ModelAnalyzer(config).analyze(), packagesMatchExactly(rules));
    }
}
