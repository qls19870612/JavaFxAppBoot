package com.ejjiu.common.collection;

import java.util.Iterator;

public interface ReusableIterator<E> extends Iterator<E> {
    void rewind();

    void cleanUp();
}
