package com.ejjiu.utils;

/**
 *
 * 创建人  liangsong
 * 创建时间 2022/09/18 20:18
 */
public class Area {
    public final int start;
    public final int end;

    public Area(int start, int end) {
        this.start = start;
        this.end = end;
    }
    public int size() {
        return end - start;
    }
}
