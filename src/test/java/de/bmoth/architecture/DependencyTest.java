package de.bmoth.architecture;

import guru.nidi.codeassert.config.AnalyzerConfig;
import guru.nidi.codeassert.dependency.DependencyRule;
import guru.nidi.codeassert.dependency.DependencyRuler;
import guru.nidi.codeassert.dependency.DependencyRules;
import guru.nidi.codeassert.model.ModelAnalyzer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;

import static guru.nidi.codeassert.junit.CodeAssertMatchers.*;
import static org.junit.Assert.assertThat;

public class DependencyTest {
    private String[] packages = new String[]{"app", "backend", "checkers", "eventbus", "modelchecker", "parser"};
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
        assertThat(new ModelAnalyzer(config).analyze(), hasNoPackageCycles());
    }

    @Test
    @Ignore
    public void dependency() {
        class ExternalPackages extends DependencyRuler {
            DependencyRule comMicrosoftZ3,
                comMicrosoftZ3Enumerations,
                deBmothApp,
                deBmothBackendZ3,
                deBmothModelchecker,
                deBmothCheckers_;

            @Override
            public void defineRules() {
                deBmothBackendZ3.mustUse(comMicrosoftZ3);
                deBmothModelchecker.mustUse(comMicrosoftZ3);
                deBmothCheckers_.mustUse(comMicrosoftZ3);
            }
        }

        class DeBmoth extends DependencyRuler {
            // $self is de.bmoth, added _ refers to subpackages of package
            DependencyRule app,
                antlr,
                backend,
                backend_,
                checkers_,
                eventbus,
                modelchecker,
                parser,
                parser_,
                preferences;

            @Override
            public void defineRules() {
                app.mayUse(checkers_, eventbus, modelchecker, parser, parser_, preferences);

                backend_.mayUse(preferences, backend, parser, parser_);

                checkers_.mayUse(backend, backend_, parser, parser_);

                modelchecker.mayUse(backend, backend_, parser_, preferences);

                parser.mayUse(antlr, parser_);
                parser_.mayUse(antlr, eventbus, parser_);

                //$self.mayUse(util, dependency_);
                //dependency_.mustUse(model);
                //model.mayUse(util).mustNotUse($self);
            }
        }

        // All dependencies are forbidden, except the ones defined above
        // java, javafx, com and org are ignored
        // maybe we should include com.microsoft again and check who is
        // allowed to directly speak to z3
        DependencyRules rules = DependencyRules.denyAll()
            .withAbsoluteRules(new ExternalPackages())
            .withRelativeRules(new DeBmoth())
            .withExternals("com.google.*", "java.*", "javafx.*", "org.*");

        assertThat(new ModelAnalyzer(config).analyze(), packagesMatchExactly(rules));
    }
}
