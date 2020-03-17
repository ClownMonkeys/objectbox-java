package io.objectbox.query;

import java.util.Date;

import io.objectbox.Property;
import io.objectbox.query.QueryBuilder.StringOrder;

/**
 * {@link Property} based query conditions with implementations split by number and type of values,
 * such as {@link LongCondition LongCondition}, {@link LongLongCondition LongLongCondition},
 * {@link LongArrayCondition LongArrayCondition} and the general {@link NullCondition NullCondition}.
 * <p>
 * Each condition implementation has a set of operation enums, e.g. EQUAL/NOT_EQUAL/..., which represent the actual
 * query condition passed to the native query builder.
 */
public abstract class PropertyQueryConditionImpl<T> extends QueryConditionImpl<T> implements PropertyQueryCondition<T> {
    protected final Property<T> property;
    private String alias;

    PropertyQueryConditionImpl(Property<T> property) {
        this.property = property;
    }

    @Override
    public QueryCondition<T> alias(String name) {
        this.alias = name;
        return this;
    }

    @Override
    void apply(QueryBuilder<T> builder) {
        applyCondition(builder);
        if (alias != null && alias.length() != 0) {
            builder.parameterAlias(alias);
        }
    }

    abstract void applyCondition(QueryBuilder<T> builder);

    public static class NullCondition<T> extends PropertyQueryConditionImpl<T> {
        private final Operation op;

        public enum Operation {
            IS_NULL,
            NOT_NULL
        }

        public NullCondition(Property<T> property, Operation op) {
            super(property);
            this.op = op;
        }

        @Override
        void applyCondition(QueryBuilder<T> builder) {
            switch (op) {
                case IS_NULL:
                    builder.isNull(property);
                    break;
                case NOT_NULL:
                    builder.notNull(property);
                    break;
                default:
                    throw new UnsupportedOperationException(op + " is not supported");
            }
        }
    }

    public static class IntArrayCondition<T> extends PropertyQueryConditionImpl<T> {
        private final Operation op;
        private final int[] value;

        public enum Operation {
            IN,
            NOT_IN
        }

        public IntArrayCondition(Property<T> property, Operation op, int[] value) {
            super(property);
            this.op = op;
            this.value = value;
        }

        @Override
        void applyCondition(QueryBuilder<T> builder) {
            switch (op) {
                case IN:
                    builder.in(property, value);
                    break;
                case NOT_IN:
                    builder.notIn(property, value);
                    break;
                default:
                    throw new UnsupportedOperationException(op + " is not supported for int[]");
            }
        }
    }

    public static class LongCondition<T> extends PropertyQueryConditionImpl<T> {
        private final Operation op;
        private final long value;

        public enum Operation {
            EQUAL,
            NOT_EQUAL,
            GREATER,
            LESS
        }

        public LongCondition(Property<T> property, Operation op, long value) {
            super(property);
            this.op = op;
            this.value = value;
        }

        public LongCondition(Property<T> property, Operation op, boolean value) {
            this(property, op, value ? 1 : 0);
        }

        public LongCondition(Property<T> property, Operation op, Date value) {
            this(property, op, value.getTime());
        }

        @Override
        void applyCondition(QueryBuilder<T> builder) {
            switch (op) {
                case EQUAL:
                    builder.equal(property, value);
                    break;
                case NOT_EQUAL:
                    builder.notEqual(property, value);
                    break;
                case GREATER:
                    builder.greater(property, value);
                    break;
                case LESS:
                    builder.less(property, value);
                    break;
                default:
                    throw new UnsupportedOperationException(op + " is not supported for String");
            }
        }
    }

    public static class LongLongCondition<T> extends PropertyQueryConditionImpl<T> {
        private final Operation op;
        private final long leftValue;
        private final long rightValue;

        public enum Operation {
            BETWEEN
        }

        public LongLongCondition(Property<T> property, Operation op, long leftValue, long rightValue) {
            super(property);
            this.op = op;
            this.leftValue = leftValue;
            this.rightValue = rightValue;
        }

        public LongLongCondition(Property<T> property, Operation op, Date leftValue, Date rightValue) {
            this(property, op, leftValue.getTime(), rightValue.getTime());
        }

        @Override
        void applyCondition(QueryBuilder<T> builder) {
            if (op == Operation.BETWEEN) {
                builder.between(property, leftValue, rightValue);
            } else {
                throw new UnsupportedOperationException(op + " is not supported with two long values");
            }
        }
    }

    public static class LongArrayCondition<T> extends PropertyQueryConditionImpl<T> {
        private final Operation op;
        private final long[] value;

        public enum Operation {
            IN,
            NOT_IN
        }

        public LongArrayCondition(Property<T> property, Operation op, long[] value) {
            super(property);
            this.op = op;
            this.value = value;
        }

        @Override
        void applyCondition(QueryBuilder<T> builder) {
            switch (op) {
                case IN:
                    builder.in(property, value);
                    break;
                case NOT_IN:
                    builder.notIn(property, value);
                    break;
                default:
                    throw new UnsupportedOperationException(op + " is not supported for long[]");
            }
        }
    }

    public static class DoubleCondition<T> extends PropertyQueryConditionImpl<T> {
        private final Operation op;
        private final double value;

        public enum Operation {
            GREATER,
            LESS
        }

        public DoubleCondition(Property<T> property, Operation op, double value) {
            super(property);
            this.op = op;
            this.value = value;
        }

        @Override
        void applyCondition(QueryBuilder<T> builder) {
            switch (op) {
                case GREATER:
                    builder.greater(property, value);
                    break;
                case LESS:
                    builder.less(property, value);
                    break;
                default:
                    throw new UnsupportedOperationException(op + " is not supported for double");
            }
        }
    }

    public static class DoubleDoubleCondition<T> extends PropertyQueryConditionImpl<T> {
        private final Operation op;
        private final double leftValue;
        private final double rightValue;

        public enum Operation {
            BETWEEN
        }

        public DoubleDoubleCondition(Property<T> property, Operation op, double leftValue, double rightValue) {
            super(property);
            this.op = op;
            this.leftValue = leftValue;
            this.rightValue = rightValue;
        }

        @Override
        void applyCondition(QueryBuilder<T> builder) {
            if (op == Operation.BETWEEN) {
                builder.between(property, leftValue, rightValue);
            } else {
                throw new UnsupportedOperationException(op + " is not supported with two double values");
            }
        }
    }

    public static class StringCondition<T> extends PropertyQueryConditionImpl<T> {
        private final Operation op;
        private final String value;
        private final StringOrder order;

        public enum Operation {
            EQUAL,
            NOT_EQUAL,
            GREATER,
            LESS,
            CONTAINS,
            STARTS_WITH,
            ENDS_WITH
        }

        public StringCondition(Property<T> property, Operation op, String value, StringOrder order) {
            super(property);
            this.op = op;
            this.value = value;
            this.order = order;
        }

        public StringCondition(Property<T> property, Operation op, String value) {
            this(property, op, value, StringOrder.CASE_INSENSITIVE);
        }

        @Override
        void applyCondition(QueryBuilder<T> builder) {
            switch (op) {
                case EQUAL:
                    builder.equal(property, value, order);
                    break;
                case NOT_EQUAL:
                    builder.notEqual(property, value, order);
                    break;
                case GREATER:
                    builder.greater(property, value, order);
                    break;
                case LESS:
                    builder.less(property, value, order);
                    break;
                case CONTAINS:
                    builder.contains(property, value, order);
                    break;
                case STARTS_WITH:
                    builder.startsWith(property, value, order);
                    break;
                case ENDS_WITH:
                    builder.endsWith(property, value, order);
                    break;
                default:
                    throw new UnsupportedOperationException(op + " is not supported for String");
            }
        }
    }

    public static class StringArrayCondition<T> extends PropertyQueryConditionImpl<T> {
        private final Operation op;
        private final String[] value;
        private final StringOrder order;

        public enum Operation {
            IN
        }

        public StringArrayCondition(Property<T> property, Operation op, String[] value, StringOrder order) {
            super(property);
            this.op = op;
            this.value = value;
            this.order = order;
        }

        public StringArrayCondition(Property<T> property, Operation op, String[] value) {
            this(property, op, value, StringOrder.CASE_INSENSITIVE);
        }

        @Override
        void applyCondition(QueryBuilder<T> builder) {
            if (op == Operation.IN) {
                builder.in(property, value, order);
            } else {
                throw new UnsupportedOperationException(op + " is not supported for String[]");
            }
        }
    }

    public static class ByteArrayCondition<T> extends PropertyQueryConditionImpl<T> {
        private final Operation op;
        private final byte[] value;

        public enum Operation {
            EQUAL,
            GREATER,
            LESS
        }

        public ByteArrayCondition(Property<T> property, Operation op, byte[] value) {
            super(property);
            this.op = op;
            this.value = value;
        }

        @Override
        void applyCondition(QueryBuilder<T> builder) {
            switch (op) {
                case EQUAL:
                    builder.equal(property, value);
                    break;
                case GREATER:
                    builder.greater(property, value);
                    break;
                case LESS:
                    builder.less(property, value);
                    break;
                default:
                    throw new UnsupportedOperationException(op + " is not supported for byte[]");
            }
        }
    }
}
