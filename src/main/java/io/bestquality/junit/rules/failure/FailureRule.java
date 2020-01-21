package io.bestquality.junit.rules.failure;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

public class FailureRule
        implements TestRule {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final List<FailurePolicy> policies = new ArrayList<>();

    public FailureRule ignoreFailureWhen(FailurePolicy policy) {
        this.policies.add(policy);
        return this;
    }

    @Override
    public Statement apply(Statement statement, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                try {
                    statement.evaluate();
                } catch (Throwable t) {
                    for (FailurePolicy policy : policies) {
                        if (policy.ignore(description, t)) {
                            log.warn(format("Ignoring failure from %s", description), t);
                            return;
                        }
                    }
                    throw t;
                }
            }
        };
    }
}
