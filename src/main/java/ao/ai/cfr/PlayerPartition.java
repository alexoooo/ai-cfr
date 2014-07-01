package ao.ai.cfr;


import com.google.common.base.Preconditions;

public abstract class PlayerPartition
{
    public static PlayerPartition nature() {
        return Nature.INSTANCE;
    }

    public static PlayerPartition rational(int index) {
        return Rational.of(index);
    }


    public abstract boolean isRational();

    public abstract boolean isNature();

    public abstract int index();


    private final static class Rational extends PlayerPartition {
        private static final Rational ZERO = new Rational(0);
        private static final Rational ONE = new Rational(1);

        public static Rational of(int index) {
            switch (index) {
                case 0: return ZERO;
                case 1: return ONE;
                default: return new Rational(index);
            }
        }

        private final int index;

        private Rational(int index) {
            Preconditions.checkArgument(index >= 0);
            this.index = index;
        }


        @Override
        public boolean isRational() {
            return true;
        }

        @Override
        public boolean isNature() {
            return false;
        }

        @Override
        public int index() {
            return 0;
        }
    }

    private final static class Nature extends PlayerPartition {
        public static final Nature INSTANCE = new Nature();

        @Override
        public boolean isRational() {
            return false;
        }

        @Override
        public boolean isNature() {
            return true;
        }

        @Override
        public int index() {
            throw new UnsupportedOperationException();
        }
    }
}
