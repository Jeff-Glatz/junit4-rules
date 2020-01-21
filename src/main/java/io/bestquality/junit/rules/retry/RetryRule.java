package io.bestquality.junit.rules.retry;

import io.bestquality.lang.CheckedConsumer;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryRule
        implements TestRule {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final CheckedConsumer<Long> wait;

    public RetryRule() {
        this(Thread::sleep);
    }

    RetryRule(CheckedConsumer<Long> wait) {
        this.wait = wait;
    }

    @Override
    public Statement apply(Statement statement, Description description) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Retry retry = resolveRetry(description);
                if (retry != null) {
                    attempt(statement, description, retry);
                } else {
                    statement.evaluate();
                }
            }
        };
    }

    private Retry resolveRetry(Class<?> type) {
        if (type == Object.class) {
            return null;
        }
        if (type.isAnnotationPresent(Retry.class)) {
            return type.getAnnotation(Retry.class);
        }
        return resolveRetry(type.getSuperclass());
    }

    private Retry resolveRetry(Description description) {
        Retry retry = description.getAnnotation(Retry.class);
        if (retry != null) {
            return retry;
        }
        return resolveRetry(description.getTestClass());
    }

    private void attempt(Statement statement, Description description, Retry retry)
            throws Throwable {
        final int attempts = retry.value();
        final Progression progression = retry.progression();
        for (int attempt = 1; attempt <= attempts; attempt++) {
            try {
                statement.evaluate();
                return;
            } catch (Throwable t) {
                if (attempt + 1 <= attempts) {
                    long time = progression.next(attempt, retry.pause());
                    log.warn("Evaluation {} of {} failed, retrying {} after {} ms", attempt, attempts, description.getMethodName(), time);
                    wait.accept(time);
                } else {
                    throw t;
                }
            }
        }
    }
}
