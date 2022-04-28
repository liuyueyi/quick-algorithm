package com.github.liuyueyi.quick.avgshare;

import com.google.gson.Gson;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author yihui
 * @date 2022/4/28
 */
public class AvgShareTest {

    private final static double min = 0.01;
    private final static BigDecimal max1 = BigDecimal.valueOf(1000), max2 = BigDecimal.valueOf(1);
    private Random random = new Random();

    private BigDecimal randDecimal(BigDecimal max) {
        double val = min + ((max.doubleValue() - min) * random.nextDouble());
        return AvgShareUtil.min(new BigDecimal(val).setScale(2, RoundingMode.CEILING), max);
    }

    /**
     * 构造测试数据
     *
     * @param rowSize
     * @param colSize
     * @return
     */
    private ImmutablePair<Map<String, BigDecimal>, Map<Integer, BigDecimal>> initData(int rowSize, int colSize) {
        Map<Integer, BigDecimal> cols = new HashMap<>();
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < colSize; i++) {
            BigDecimal tmp;
            if (i == colSize - 1) {
                tmp = randDecimal(max2);
            } else {
                tmp = randDecimal(max1);
            }
            cols.put(i, tmp);
            sum = sum.add(tmp);
        }

        Map<String, BigDecimal> rows = new HashMap<>();
        for (int i = 0; i < rowSize; i++) {
            if (sum.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }

            if (i == rowSize - 1) {
                rows.put("r_" + i, sum);
            } else {
                BigDecimal tmp = randDecimal(sum);
                rows.put("r_" + i, tmp);
                sum = sum.subtract(tmp);
            }
        }

        return ImmutablePair.of(rows, cols);
    }

    private void basicTest(int rowN, int colN) {
        ImmutablePair<Map<String, BigDecimal>, Map<Integer, BigDecimal>> pair = initData(rowN, colN);
        Map<String, BigDecimal> row = pair.left;
        Map<Integer, BigDecimal> col = pair.right;
        Map<String, Map<Integer, BigDecimal>> avgList = AvgShareUtil.avgShare(row, col);
        Map<String, Object> data = new HashMap<>();
        data.put("row", row);
        data.put("col", col);
        data.put("avg", avgList);
        if (!checkCol(avgList, row) || !checkRow(avgList, col)) {
            System.out.println("不均: " + new Gson().toJson(data));
        } else {
            System.out.println("正常: " + new Gson().toJson(data));
        }
    }

    @Test
    public void testAvg() {
        int i = 0;
        while (++i < 200) {
            basicTest(3, 3);
        }

        i = 0;
        while (++i < 200) {
            basicTest(3, 5);
        }

        i = 0;
        while (++i < 200) {
            basicTest(6, 11);
        }

        i = 0;
        while (++i < 200) {
            basicTest(12, 3);
        }
    }

    /**
     * 要求每一个row中的数据 = rows中分摊数据之和
     *
     * @param rows
     * @param row
     * @return
     */
    private <T> boolean checkCol(Map<T, Map<Integer, BigDecimal>> rows, Map<T, BigDecimal> row) {
        for (Map.Entry<T, BigDecimal> entry : row.entrySet()) {
            if (entry.getValue().compareTo(AvgShareUtil.sum(rows.get(entry.getKey()).values())) != 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * 要求每一个col中的数据 = rows中对应行的数据之和
     *
     * @param rows
     * @param col
     * @return
     */
    private <T> boolean checkRow(Map<T, Map<Integer, BigDecimal>> rows, Map<Integer, BigDecimal> col) {
        for (Map.Entry<Integer, BigDecimal> entry : col.entrySet()) {
            Integer key = entry.getKey();
            BigDecimal sum = BigDecimal.ZERO;

            for (Map<Integer, BigDecimal> val : rows.values()) {
                sum = sum.add(val.get(key));
            }

            if (sum.compareTo(entry.getValue()) != 0) {
                return false;
            }
        }
        return true;
    }

    private static class ImmutablePair<L, R> {
        public final L left;
        public final R right;

        private ImmutablePair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        public static <L, R> ImmutablePair<L, R> of(L l, R r) {
            return new ImmutablePair<>(l, r);
        }
    }
}
