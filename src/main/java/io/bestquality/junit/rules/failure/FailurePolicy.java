package io.bestquality.junit.rules.failure;

import org.junit.runner.Description;

/**
 * A {@link FailurePolicy} enables customization of how the {@link FailureRule} will respond to
 * test failures. This can be useful in scenarios where there is an integration test that has
 * a dependency on an unstable 3rd party API and the desire is to not block a release based
 * on an unstable API.
 *
 * @see Policies#releasing()
 * @see FailureRule
 */
public interface FailurePolicy {
    boolean ignore(Description description, Throwable throwable);
}
