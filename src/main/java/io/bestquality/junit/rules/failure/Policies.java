package io.bestquality.junit.rules.failure;

public class Policies {

    /**
     * Utility Function that can be used to determine if the current build is executing as part
     * of a release.
     *
     * @return {@code true} if the current build is a release build; {@code false} otherwise.
     */
    public static boolean releasing() {
        String version = System.getProperty("project.version");
        return version != null && !version.endsWith("-SNAPSHOT");
    }
}
