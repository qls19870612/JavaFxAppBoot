package com.ejjiu.utils;

/**
 * @author cyb
 * @date 2018/1/4.
 */
public interface ITimeData {
    DurationTime getNextTime(long ctime);

    DurationTime getBeforeTime(long ctime);
}
