package com.ejjiu.collection;

/**
 * ����Pair, ���Ƿ�int
 *
 * @author java_1
 */
public class LongPair {

    public final long left;

    public final long right;

    public LongPair(long left, long right) {
        this.left = left;
        this.right = right;
    }

    /**
     * Returns a String representation of the Pair in the form: (L,R)
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        builder.append(left);
        builder.append(",");
        builder.append(right);
        builder.append(")");
        return builder.toString();
    }

    public static LongPair of(long left, long right) {
        return new LongPair(left, right);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LongPair) {
            LongPair o = (LongPair) obj;
            return this.left == o.left && this.right == o.right;
        }
        return false;
    }
}
