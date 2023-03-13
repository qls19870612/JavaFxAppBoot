package com.ejjiu.common.collection;

public class LeftIntPair<R> {

    @SuppressWarnings("unchecked")
    public static <R> LeftIntPair<R>[] newArray(int length) {
        return new LeftIntPair[length];
    }

    public final int left;

    public final R right;

    public LeftIntPair(int left, R right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LeftIntPair) {
            LeftIntPair o = (LeftIntPair) obj;
            return this.left == o.left && this.right == o.right;
        }
        return false;
    }
}
