package com.ejjiu.utils;

public class RandomNumber {

    public static int getRate() {
        return ThreadLocalRandom.current().nextInt(100);
        // return rand.nextInt(100);
    }

    public static float getFraction() {
        return ThreadLocalRandom.current().nextFloat();
        // return rand.nextFloat();
    }

    public static int getRate(int num) {
        return num != 0 ? ThreadLocalRandom.current().nextInt(Math.abs(num)) : 0;
    }

    public static int randomInt() {
        return ThreadLocalRandom.current().nextInt();
    }

    public static long getRate(long num) {
        return num != 0 ? ThreadLocalRandom.current().nextLong(Math.abs(num)) : 0;
    }

    public static long nextLong(long value) {
        return 0;
    }

    public static long randomLong() {
        return ThreadLocalRandom.current().nextLong();
    }

    /**
     * 不检查参数的合法性
     *
     * @param num
     * @return
     */
    public static int getUncheckRate(int num) {
        return ThreadLocalRandom.current().nextInt(num);
    }

    /**
     * 在最小值跟最大值之间进行随机，包括上限
     *
     * @param lower
     * @param upper
     * @return
     */
    public static int randomRange(int lower, int upper) {
        assert lower <= upper;
        if (lower == upper) {
            return lower;
        }
        return ThreadLocalRandom.current().nextInt(lower, upper + 1);
    }

    /**
     * 在最小值跟最大值之间进行随机，包括上限
     * @param lower
     * @param upper
     * @return
     */
    public static long randomRange(long lower, long upper) {
        assert lower <= upper;
        if (lower == upper) {
            return lower;
        }
        return ThreadLocalRandom.current().nextLong(lower, upper + 1);
    }
}
