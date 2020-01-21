package io.bestquality.junit.rules.retry;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({TYPE, METHOD})
@Inherited
public @interface Retry {

    /**
     * The number of times to retry
     *
     * @return The number of times to retry
     */
    int value() default 4;

    /**
     * The number of milliseconds to pause before another retry is attempted
     *
     * @return The number of milliseconds to wait before another retry is attempted
     */
    long pause() default 1000;

    /**
     * The pause policy applied to the pause time to determine how long to wait before each retry
     *
     * @return The pause policy applied
     */
    Progression progression() default Progression.GEOMETRIC;
}
