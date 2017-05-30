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

    private AnalyzerConfig config;

    @Before
    public void setup() {
        // Analyze all sources in src/main/java
        config = new AnalyzerConfig();
        config = config.withSources(new File("src/main/java/de/bmoth"), "app", "backend", "checkers", "eventbus", "exceptions", "modelchecker", "parser", "util");
        config = config.withClasses(new File("build/classes/main/de/bmoth"), "app", "backend", "checkers", "eventbus", "exceptions", "modelchecker", "parser", "util");
    }

    @Test
    public void noCycles() {
        assertThat(new ModelAnalyzer(config).analyze(), hasNoClassCycles());
        assertThat(new ModelAnalyzer(config).analyze(), hasNoPackageCycles());

    }

    @Test
    @Ignore
    public void dependency() {
        class DeBmoth extends DependencyRuler {
            // $self is de.bmoth, added _ refers to subpackages of package
            DependencyRule app,
                antlr,
                backend,
                backend_,
                checkers_,
                eventbus,
                exceptions,
                modelchecker,
                parser,
                parser_,
                util;

            @Override
            public void defineRules() {
                parser_.mayUse(parser_);
                parser_.mayUse(antlr);
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
            .withRelativeRules(new DeBmoth())
            .withExternals("java.*", "javafx.*", "com.*", "org.*");

        assertThat(new ModelAnalyzer(config).analyze(), packagesMatchExactly(rules));
    }
}
