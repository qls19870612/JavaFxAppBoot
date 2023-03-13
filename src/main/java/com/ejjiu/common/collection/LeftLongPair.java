package com.ejjiu.common.collection;

public class LeftLongPair<R> {

    public final long left;

    public final R right;

    public LeftLongPair(long left, R right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof LeftLongPair) {
            LeftLongPair o = (LeftLongPair) obj;
            return this.left == o.left && this.right == o.right;
        }
        return false;
    }
}
