package io.bestquality.junit.rules.retry;

import io.bestquality.lang.CheckedConsumer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runners.model.Statement;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static io.bestquality.junit.rules.retry.Progression.ARITHMETIC;
import static io.bestquality.junit.rules.retry.Progression.GEOMETRIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class RetryRuleTest {

    private RetryRule rule;

    @Mock
    private Statement mockStatement;

    @Mock
    private Description mockDescription;

    @Mock
    private Retry mockRetry;

    @Mock
    private CheckedConsumer<Long> mockWait;

    @Before
    public void setUp() {
        rule = new RetryRule(mockWait);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldUseAnnotationOnTestClassWhenNotFoundOnDescription()
            throws Throwable {
        given(mockDescription.getTestClass())
                .willReturn((Class) BaseTest.class);
        given(mockDescription.getAnnotation(Retry.class))
                .willReturn(null);

        Statement statement = rule.apply(mockStatement, mockDescription);
        statement.evaluate();

        then(mockStatement)
                .should(times(1))
                .evaluate();
        then(mockWait)
                .shouldHaveZeroInteractions();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldSearchTestHierarchyForAnnotationWhenNotFoundOnDescription()
            throws Throwable {
        given(mockDescription.getTestClass())
                .willReturn((Class) ExtendedTest.class);
        given(mockDescription.getAnnotation(Retry.class))
                .willReturn(null);

        Statement statement = rule.apply(mockStatement, mockDescription);
        statement.evaluate();

        then(mockStatement)
                .should(times(1))
                .evaluate();
        then(mockWait)
                .shouldHaveZeroInteractions();
    }

    @Test
    public void shouldUseAnnotationOnDescription()
            throws Throwable {
        given(mockRetry.value())
                .willReturn(0);
        given(mockDescription.getAnnotation(Retry.class))
                .willReturn(mockRetry);

        Statement statement = rule.apply(mockStatement, mockDescription);
        statement.evaluate();

        then(mockStatement)
                .shouldHaveZeroInteractions();
        then(mockWait)
                .shouldHaveZeroInteractions();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldDirectlyExecuteStatementWhenAnnotationNotFound()
            throws Throwable {
        given(mockDescription.getTestClass())
                .willReturn((Class) Object.class);
        given(mockDescription.getAnnotation(Retry.class))
                .willReturn(null);

        Statement statement = rule.apply(mockStatement, mockDescription);
        statement.evaluate();

        then(mockStatement)
                .should(times(1))
                .evaluate();
        then(mockWait)
                .shouldHaveZeroInteractions();
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("unchecked")
    public void shouldDirectlyExecuteStatementWhenAnnotationNotFoundAndNotIgnoreException()
            throws Throwable {
        given(mockDescription.getTestClass())
                .willReturn((Class) Object.class);
        given(mockDescription.getAnnotation(Retry.class))
                .willReturn(null);
        willThrow(new IllegalArgumentException("boom"))
                .given(mockStatement).evaluate();

        Statement statement = rule.apply(mockStatement, mockDescription);
        statement.evaluate();

        then(mockStatement)
                .should(times(1))
                .evaluate();
        then(mockWait)
                .shouldHaveZeroInteractions();
    }

    @Test
    public void shouldHandleOneAttemptWithNoErrorRaised()
            throws Throwable {
        given(mockRetry.value())
                .willReturn(1);
        given(mockDescription.getAnnotation(Retry.class))
                .willReturn(mockRetry);

        Statement statement = rule.apply(mockStatement, mockDescription);
        statement.evaluate();

        then(mockStatement)
                .should(times(1))
                .evaluate();
        then(mockWait)
                .shouldHaveZeroInteractions();
    }

    @Test
    public void shouldHandleOneAttemptWithErrorRaisedByPropagatingError()
            throws Throwable {
        Exception boom = new Exception("boom");

        given(mockRetry.value())
                .willReturn(1);
        given(mockDescription.getAnnotation(Retry.class))
                .willReturn(mockRetry);
        willThrow(boom)
                .given(mockStatement).evaluate();

        Statement statement = rule.apply(mockStatement, mockDescription);

        try {
            statement.evaluate();
        } catch (Exception e) {
            assertThat(e)
                    .isSameAs(boom);
        }

        then(mockStatement)
                .should(times(1))
                .evaluate();
        then(mockWait)
                .shouldHaveZeroInteractions();
    }

    @Test
    public void shouldHandleMultipleAttemptsWithNoErrorRaised()
            throws Throwable {
        given(mockRetry.value())
                .willReturn(3);
        given(mockDescription.getAnnotation(Retry.class))
                .willReturn(mockRetry);

        Statement statement = rule.apply(mockStatement, mockDescription);
        statement.evaluate();

        then(mockStatement)
                .should(times(1))
                .evaluate();
        then(mockWait)
                .shouldHaveZeroInteractions();
    }

    @Test
    public void shouldHandleMultipleAttemptsWithErrorRaisedFollowedBySuccessfulAttempt()
            throws Throwable {
        given(mockRetry.value())
                .willReturn(3);
        given(mockRetry.pause())
                .willReturn(1000L);
        given(mockRetry.progression())
                .willReturn(ARITHMETIC);
        given(mockDescription.getAnnotation(Retry.class))
                .willReturn(mockRetry);

        willThrow(new Exception("boom"))
                .willDoNothing()
                .given(mockStatement).evaluate();

        Statement statement = rule.apply(mockStatement, mockDescription);
        statement.evaluate();

        then(mockStatement)
                .should(times(2))
                .evaluate();
        then(mockWait)
                .should(only())
                .accept(1000L);
    }

    @Test
    public void shouldHandleMultipleAttemptsWithErrorRaisedByPropagatingLastError()
            throws Throwable {
        Exception lastException = new Exception("boom3");

        given(mockRetry.value())
                .willReturn(3);
        given(mockRetry.pause())
                .willReturn(1000L);
        given(mockRetry.progression())
                .willReturn(GEOMETRIC);
        given(mockDescription.getAnnotation(Retry.class))
                .willReturn(mockRetry);

        willThrow(new Exception("boom1"))
                .willThrow(new Exception("boom2"))
                .willThrow(lastException)
                .given(mockStatement).evaluate();

        Statement statement = rule.apply(mockStatement, mockDescription);

        try {
            statement.evaluate();
        } catch (Exception e) {
            assertThat(e)
                    .isSameAs(lastException);
        }

        then(mockStatement)
                .should(times(3))
                .evaluate();
        then(mockWait)
                .should(times(1))
                .accept(1000L);
        then(mockWait)
                .should(times(1))
                .accept(4000L);
    }

    @Retry
    private static class BaseTest {
    }

    private static class ExtendedTest
            extends BaseTest {
    }
}
