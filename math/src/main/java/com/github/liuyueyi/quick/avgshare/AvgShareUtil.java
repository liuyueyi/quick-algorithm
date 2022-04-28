package com.github.liuyueyi.quick.avgshare;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维数组的均摊算法
 * <p>
 * 两个数组，row, col
 * - sum(row) = sum(col)
 * 均摊：
 * - col中的value，均摊到row行
 *
 * <p>
 * | col\row |        |        |      |
 * | :------ | ------ | ------ | ---- |
 * | -       | 807.6  | 153.19 | 4.28 | <-- 这一行三个表示row行
 * | 291.47  | 243.92 | 46.27  | 1.28 |
 * | 221.9   | 185.7  | 35.23  | 0.97 |
 * | 451.7   | 377.98 | 71.69  | 2.03 |
 * ^ 上面三个为col列
 *
 * @author yihui
 * @date 2022/4/28
 */
public class AvgShareUtil {
    /**
     * 二维的均摊策略
     * 将col列的每个数据，按照均摊策略，分摊到 row 对应的每个数据上
     * <p>
     * col\row      4              |           16
     * 5     1 = 5 * 4 / (4+ 16)   |    4 = 5 * 16 / (4+16)
     * 15    3 = 15 * 4 / (4+ 16)  |   12 = 15 * 16 / (4+16)
     *
     * @param row
     * @param col
     * @param <R> row行对应的key
     * @param <C> col列对应的key
     * @return
     */
    public static <R, C> Map<R, Map<C, BigDecimal>> avgShare(Map<R, BigDecimal> row, Map<C, BigDecimal> col) {
        Map<R, Map<C, BigDecimal>> result = new HashMap<>(row.size());
        BigDecimal rowSum = sum(row.values());
        int rowSize = row.size();
        Map<C, BigDecimal> remainCol = new HashMap<>(col);
        for (Map.Entry<R, BigDecimal> entry : row.entrySet()) {
            if (--rowSize == 0) {
                result.put(entry.getKey(), remainCol);
            } else {
                result.put(entry.getKey(), avgShare(col, remainCol, entry.getValue(), rowSum));
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
    private static <C> Map<C, BigDecimal> avgShare(Map<C, BigDecimal> col, Map<C, BigDecimal> remainCol, final BigDecimal amount, final BigDecimal totalAmount) {
        int size = col.size();
        Map<C, BigDecimal> result = new HashMap<>(size);
        BigDecimal remainAmount = amount;
        for (Map.Entry<C, BigDecimal> entry : col.entrySet()) {
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
     * 求和
     *
     * @param row
     * @return
     */
    public static BigDecimal sum(Collection<BigDecimal> row) {
        BigDecimal rowSum = BigDecimal.ZERO;
        for (BigDecimal sub : row) {
            rowSum = rowSum.add(sub);
        }
        return rowSum;
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
