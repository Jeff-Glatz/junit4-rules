package io.bestquality.junit.rules.retry;

public enum Progression {

    ARITHMETIC() {
        @Override
        public long next(int attempt, long pause) {
            return attempt * pause;
        }
    },
    CONSTANT() {
        @Override
        public long next(int attempt, long pause) {
            return pause;
        }
    },
    GEOMETRIC() {
        @Override
        public long next(int attempt, long pause) {
            return (attempt * attempt) * pause;
        }
    };

    public abstract long next(int attempt, long pause);
}
