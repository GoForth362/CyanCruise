package v620.cc001.cloud01.app01.mservice.datamodel;

import java.time.LocalDateTime;
import java.util.Comparator;

public final class DatamodelFieldMapper {

    private DatamodelFieldMapper() {
    }

    public static Long asLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Long) {
            return (Long) value;
        }
        if (value instanceof Number) {
            return Long.valueOf(((Number) value).longValue());
        }
        return Long.valueOf(String.valueOf(value));
    }

    public static Integer asInteger(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Number) {
            return Integer.valueOf(((Number) value).intValue());
        }
        return Integer.valueOf(String.valueOf(value));
    }

    public static String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    public static LocalDateTime asDateTime(Object value) {
        return value instanceof LocalDateTime ? (LocalDateTime) value : null;
    }

    public static boolean same(Object left, Object right) {
        if (left == null) {
            return right == null;
        }
        return left.equals(right);
    }

    public static Comparator<CosmicDatamodelRecord> dateTimeDesc(final String fieldName) {
        return new Comparator<CosmicDatamodelRecord>() {
            public int compare(CosmicDatamodelRecord left, CosmicDatamodelRecord right) {
                LocalDateTime leftValue = asDateTime(left.get(fieldName));
                LocalDateTime rightValue = asDateTime(right.get(fieldName));
                if (leftValue == null && rightValue == null) {
                    return 0;
                }
                if (leftValue == null) {
                    return 1;
                }
                if (rightValue == null) {
                    return -1;
                }
                return rightValue.compareTo(leftValue);
            }
        };
    }
}
