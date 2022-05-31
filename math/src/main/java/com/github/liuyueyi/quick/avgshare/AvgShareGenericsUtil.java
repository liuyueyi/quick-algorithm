package com.github.liuyueyi.quick.avgshare;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * 基于业务泛型的均摊支持
 *
 * @author yihui
 * @date 2022/4/28
 */
public class AvgShareGenericsUtil {

    public static class AvgParam<K, T> {
        private T data;
        private transient Function<T, K> key;
        private transient Function<T, BigDecimal> val;

        public AvgParam(T data, Function<T, K> key, Function<T, BigDecimal> val) {
            this.data = data;
            this.key = key;
            this.val = val;
        }

        public BigDecimal getValue() {
            return val.apply(data);
        }

        public K getKey() {
            return key.apply(data);
        }

        @Override
        public String toString() {
            return "{\"" + getKey() + "\": \"" + getValue() + "\"}";
        }
    }

    /**
     * 一维均摊策略
     *
     * @param col
     * @param target
     * @param <R>
     * @param <D>
     * @return
     */
    public static <R, D> Map<R, BigDecimal> avgShare(List<AvgParam<R, D>> col, AvgParam target) {
        final BigDecimal totalAmount = sum(col);
        final BigDecimal targetVal = totalAmount.compareTo(target.getValue()) > 0 ? target.getValue() : totalAmount;
        BigDecimal remainAmount = targetVal;
        int size = col.size();
        Map<R, BigDecimal> result = new HashMap<>(size);
        for (AvgParam<R, D> entry : col) {
            R key = entry.getKey();
            BigDecimal cell = entry.getValue();
            if (--size == 0 || remainAmount.compareTo(BigDecimal.ZERO) == 0) {
                result.put(key, remainAmount);
            } else {
                BigDecimal sharedAmount = cell.multiply(targetVal).divide(totalAmount, 2, RoundingMode.CEILING);
                sharedAmount = min(sharedAmount, remainAmount);
                result.put(key, sharedAmount);
                remainAmount = remainAmount.subtract(sharedAmount);
            }
        }
        return result;
    }

    /**
     * 二维的均摊策略
     * 将col列的每个数据，根据 row[x] / sum(row[]) 等比例均摊
     * <p>
     * col\row   4              |   16
     * 5     1 = 5 * 4 / (4+16) |    4
     * 15    3 = 15 * 4/(4+16)  |   12
     *
     * @param col
     * @param row
     * @return 最外层key = row数据的key, value表示col列均摊到 row[key] 上的值
     */
    public static <R, D, C, T> Map<R, Map<C, BigDecimal>> avgShares(List<AvgParam<R, D>> row, List<AvgParam<C, T>> col) {
        Map<R, Map<C, BigDecimal>> result = new HashMap<>(row.size());
        BigDecimal rowSum = sum(row);
        Map<C, BigDecimal> remainCol = new HashMap<>(col.size());
        for (AvgParam<C, T> colCell : col) {
            remainCol.put(colCell.getKey(), colCell.getValue());
        }

        int rowSize = row.size();
        for (AvgParam<R, D> rowCell : row) {
            if (--rowSize == 0) {
                result.put(rowCell.getKey(), remainCol);
            } else {
                result.put(rowCell.getKey(), shareAvg(col, remainCol, rowCell.getValue(), rowSum));
            }
        }
        return result;
    }

    /**
     * 实现col列的数据在具体的row[i]上进行均摊
     *
     * @param col
     * @param remainCol
     * @param amount
     * @param totalAmount
     * @return
     */
    public static <C, T> Map<C, BigDecimal> shareAvg(List<AvgParam<C, T>> col, Map<C, BigDecimal> remainCol, final BigDecimal amount, final BigDecimal totalAmount) {
        int size = col.size();
        Map<C, BigDecimal> result = new HashMap<>(size);
        BigDecimal remainAmount = amount;
        for (AvgParam<C, T> entry : col) {
            C key = entry.getKey();
            BigDecimal cell = entry.getValue();
            if (--size == 0 || remainAmount.compareTo(BigDecimal.ZERO) == 0) {
                // 最后一个，或者剩余金额为0时，直接补齐
                result.put(key, remainAmount);
                remainCol.put(key, remainCol.get(key).subtract(remainAmount));
            } else {
                BigDecimal sharedAmount = cell.multiply(amount).divide(totalAmount, 2, RoundingMode.CEILING);
                sharedAmount = min(sharedAmount, amount);
                result.put(key, sharedAmount);
                remainAmount = remainAmount.subtract(sharedAmount);
                remainCol.put(key, remainCol.get(key).subtract(sharedAmount));
            }
        }
        return result;
    }


    /**
     * 取和值
     *
     * @param col
     * @param <R>
     * @param <D>
     * @return
     */
    public static <R, D> BigDecimal sum(Collection<AvgParam<R, D>> col) {
        return col.stream().map(AvgParam::getValue).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 取小值
     *
     * @param left
     * @param right
     * @return
     */
    public static BigDecimal min(BigDecimal left, BigDecimal right) {
        return left.compareTo(right) < 0 ? left : right;
    }
}
