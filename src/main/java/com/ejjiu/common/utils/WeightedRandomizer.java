package com.ejjiu.common.utils;

import com.ejjiu.common.collection.LeftIntPair;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Liwei
 *
 */
public class WeightedRandomizer<V> {

    private final LeftIntPair<V>[] weights;

    private final int threshold;

    // 优化只有一个值的时候
    private final V singleHolder;

    public WeightedRandomizer(List<LeftIntPair<V>> pairs) {

        checkArgument(pairs.size() > 0, "权重随机器的权重个数为0");

        LeftIntPair<V>[] weights = LeftIntPair.newArray(pairs.size());
        long threshold = 0;

        int index = 0;
        for (LeftIntPair<V> pair : pairs) {
            int weight = pair.left;
            checkArgument(weight > 0, "权重随机器的第%s 个权重为0", index);

            V v = pair.right;
            checkNotNull(v, "权重随机器的第%s 个值为null", index);

            threshold = threshold + weight;
            checkArgument(threshold <= Integer.MAX_VALUE, "权重随机器的总权重大于Integer.MAX_VALUE");

            weights[index++] = new LeftIntPair<>((int) threshold, v);
        }

        this.weights = weights;
        this.threshold = (int) threshold;

        // 优化只有一个值的时候
        singleHolder = weights.length == 1 ? weights[0].right : null;
    }

    public WeightedRandomizer(V single) {
        this.weights = LeftIntPair.newArray(1);
        weights[0] = new LeftIntPair<>(1, single);
        threshold = 1;

        // 优化只有一个值的时候
        singleHolder = single;
    }

    public int size() {
        return weights.length;
    }

    public V next() {
        // 优化只有一个值的时候
        if (singleHolder != null) {
            return singleHolder;
        }

        int rate =  RandomNumber.getUncheckRate(threshold);

        for (LeftIntPair<V> obj : weights) {
            if (rate < obj.left) {
                return obj.right;
            }
        }

        return null;
    }

    public V getSingleValue() {
        return singleHolder;
    }

    public LeftIntPair<V>[] values() {
        return weights;
    }
}
