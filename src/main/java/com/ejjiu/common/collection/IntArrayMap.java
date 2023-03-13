/**
 *
 */
package com.ejjiu.common.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author liwei
 *
 */
public class IntArrayMap<E> {

    static final Entry[] EMPTY_ARRAY = new Entry[0];

    @SuppressWarnings("unchecked")
    private volatile Entry<E>[] array = EMPTY_ARRAY;

    public IntArrayMap() {
    }

    @SuppressWarnings("unchecked")
    public IntArrayMap(IntHashMap<E> map) {

        List<Entry<E>> list = new ArrayList<>(map.size());
        for (IntHashMap.Entry<E> e : map.entrySet()) {
            list.add(new Entry<E>(e.getKey(), e.getValue()));
        }

        Entry<E>[] newArray = list.toArray(EMPTY_ARRAY);
        Arrays.sort(newArray, comp);

        assert !checkNotSortedObject(newArray);
        setArray(newArray);
    }

    private void setArray(Entry<E>[] e) {
        this.array = e;
    }

    private Entry<E>[] getArray() {
        return array;
    }

    /**
     * 这个数组不能乱用，谁要是改了里面东西，砍死你
     * @return
     */
    public Entry<E>[] originalEntries() {
        return getArray();
    }

    public Entry<E>[] entries() {
        Entry<E>[] array = getArray();
        return Arrays.copyOf(array, array.length);
    }

    public E put(int key, E toAdd) {
        Entry<E> obj = getOrCreate(key);
        E e = obj.e;

        obj.e = toAdd;

        return e;
    }

    public E get(int key) {
        Entry<E> obj = getOrCreate(key);
        return obj.e;
    }

    public E remove(int key) {
        Entry<E> obj = getOrCreate(key);
        E e = obj.e;

        obj.e = null;

        return e;
    }

    @SuppressWarnings("unchecked")
    public void clear() {
        setArray(EMPTY_ARRAY);
    }

    Entry<E> getOrCreate(int key) {

        Entry<E>[] array = getArray();
        int size = array.length;

        int low = 0;
        int high = size - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            Entry<E> midVal = array[mid];

            if (midVal.key < key) {
                low = mid + 1;
            } else if (midVal.key > key) {
                high = mid - 1;
            } else {
                return midVal; // key found
            }
        }

        int index = low; // key not found. insert game.point
        assert index >= 0 && index <= size;

        Entry<E> obj = new Entry<E>(key, null);

        Entry<E>[] newArray = Arrays.copyOf(array, size + 1);

        System.arraycopy(newArray, index, newArray, index + 1, size - index);
        newArray[index] = obj;

        // set
        setArray(newArray);

        return obj;
    }

    // Never make these public
    static final int LEFT_IS_GREATER = 1;
    static final int RIGHT_IS_GREATER = -1;
    private static final Comparator<Entry> comp = new Comparator<Entry>() {
        @Override
        public int compare(Entry left, Entry right) {

            if (left == right) {
                return 0;
            }
            if (left == null) {
                return LEFT_IS_GREATER;
            }
            if (right == null) {
                return RIGHT_IS_GREATER;
            }

            return left.key - right.key;
        }
    };

    public static class Entry<E> {

        final int key;

        volatile E e;

        Entry(int key, E e) {
            this.key = key;
            this.e = e;
        }

        public E get() {
            return e;
        }
    }

    // --------- check bug -----------

    public boolean hasEmptyObject() {
        Entry<E>[] array = getArray();

        for (Entry<E> so : array) {
            if (so == null) {
                return true;
            }
        }

        return false;
    }

    public boolean hasNotSortedObject() {
        return checkNotSortedObject(getArray());
    }

    private boolean checkNotSortedObject(Entry<E>[] array) {

        for (int i = 1; i < array.length; i++) {
            Entry<E> prev = array[i - 1];
            Entry<E> cur = array[i];
            if (prev.key >= cur.key) {
                return true;
            }
        }

        return false;
    }
}
