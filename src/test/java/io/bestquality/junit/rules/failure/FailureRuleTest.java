package io.bestquality.junit.rules.failure;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class FailureRuleTest {

    private FailureRule rule;

    @Mock
    private Statement mockStatement;

    @Mock
    private Description mockDescription;

    @Before
    public void setUp() {
        rule = new FailureRule();
    }

    @Test
    public void shouldExecuteStatementAndIgnoreExceptionViaPolicy()
            throws Throwable {
        willThrow(new Exception("boom"))
                .given(mockStatement).evaluate();

        rule.ignoreFailureWhen((description, throwable) -> true);
        Statement statement = rule.apply(mockStatement, mockDescription);
        statement.evaluate();

        then(mockStatement)
                .should(times(1))
                .evaluate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldExecuteStatementAndNotIgnoreExceptionViaPolicy()
            throws Throwable {
        willThrow(new IllegalArgumentException("boom"))
                .given(mockStatement).evaluate();

        rule.ignoreFailureWhen((description, throwable) -> false);
        Statement statement = rule.apply(mockStatement, mockDescription);
        statement.evaluate();

        then(mockStatement)
                .should(times(1))
                .evaluate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldExecuteStatementAndNotIgnoreExceptionWithNoPolicy()
            throws Throwable {
        willThrow(new IllegalArgumentException("boom"))
                .given(mockStatement).evaluate();

        Statement statement = rule.apply(mockStatement, mockDescription);
        statement.evaluate();

        then(mockStatement)
                .should(times(1))
                .evaluate();
    }
}
