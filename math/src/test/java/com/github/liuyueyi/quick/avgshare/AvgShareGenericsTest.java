package com.github.liuyueyi.quick.avgshare;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author yihui
 * @date 2022/4/28
 */
public class AvgShareGenericsTest extends AvgShareTest {


    private void initAndCheck(int size) {
        List<AvgShareGenericsUtil.AvgParam<Integer, BigDecimal>> rows = new ArrayList<>();
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < size; i++) {
            BigDecimal tmp;
            if (i == size - 1) {
                tmp = randDecimal(max2);
            } else {
                tmp = randDecimal(max1);
            }
            int finalI = i;
            rows.add(new AvgShareGenericsUtil.AvgParam(tmp, o -> finalI, (Function) o -> tmp));
            sum = sum.add(tmp);
        }

        BigDecimal target = randDecimal(sum);
        Map<Integer, BigDecimal> avg = AvgShareGenericsUtil.avgShare(rows, new AvgShareGenericsUtil.AvgParam(target, o -> "T", o -> target));
        Map data = new HashMap();
        data.put("avg", avg);
        data.put("target", target);
        data.put("rows", rows);
        if (target.compareTo(AvgShareUtil.sum(avg.values())) != 0) {
            System.out.println("不均: " + data);
        } else {
            boolean checkFailed = false;
            for (AvgShareGenericsUtil.AvgParam<Integer, BigDecimal> param : rows) {
                if (avg.get(param.getKey()).compareTo(param.getValue()) > 0) {
                    // 构造的case中，要求均摊的金额不能大于row中数据
                    checkFailed = true;
                    break;
                }
            }
            if (checkFailed) {
                System.out.println("不均: " + data);
            } else {
                System.out.println("均摊: " + data);
            }
        }
    }


    /**
     * 一维数组的分摊测试
     */
    @Test
    public void testAvgShare() {
        int i = 0;
        while (++i < 200) {
            initAndCheck(2);
        }

    }

    /**
     * 构造测试数据
     *
     * @param rowSize
     * @param colSize
     * @return
     */
    private Object initData(int rowSize, int colSize) {
        List<AvgShareGenericsUtil.AvgParam> cols = new ArrayList<>();
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < colSize; i++) {
            BigDecimal tmp;
            if (i == colSize - 1) {
                tmp = randDecimal(max2);
            } else {
                tmp = randDecimal(max1);
            }
            int finalI = i;
            cols.add(new AvgShareGenericsUtil.AvgParam(tmp, o -> finalI, (Function) o -> tmp));
            sum = sum.add(tmp);
        }

        List<AvgShareGenericsUtil.AvgParam> rows = new ArrayList<>();
        for (int i = 0; i < rowSize; i++) {
            if (sum.compareTo(BigDecimal.ZERO) == 0) {
                break;
            }
            int finalI = i;

            if (i == rowSize - 1) {
                BigDecimal finalSum = sum;
                rows.add(new AvgShareGenericsUtil.AvgParam(finalSum, o -> "r_" + finalI, o -> finalSum));
            } else {
                final BigDecimal tmp = randDecimal(sum);
                rows.add(new AvgShareGenericsUtil.AvgParam(tmp, o -> "r_" + finalI, o -> tmp));
                sum = sum.subtract(tmp);
            }
        }
        return ImmutablePair.of(rows, cols);
    }

    private void initAndCheck(int rowN, int colN) {
        ImmutablePair<List<AvgShareGenericsUtil.AvgParam<String, BigDecimal>>, List<AvgShareGenericsUtil.AvgParam<Integer, BigDecimal>>> pair =
                (ImmutablePair<List<AvgShareGenericsUtil.AvgParam<String, BigDecimal>>, List<AvgShareGenericsUtil.AvgParam<Integer, BigDecimal>>>) initData(rowN, colN);
        List<AvgShareGenericsUtil.AvgParam<String, BigDecimal>> row = pair.left;
        List<AvgShareGenericsUtil.AvgParam<Integer, BigDecimal>> col = pair.right;
        Map<String, Map<Integer, BigDecimal>> avgList = AvgShareGenericsUtil.avgShares(row, col);
        Map<String, Object> data = new HashMap<>();
        data.put("row", row);
        data.put("col", col);
        data.put("avg", avgList);
        if (!checkCol(avgList, row) || !checkRow(avgList, col)) {
            System.out.println("不均: " + data);
        } else {
            System.out.println("正常: " + data);
        }
    }

    /**
     * 二维数组分摊策略
     */
    @Test
    public void testAvgShares() {
        int i = 0;
        while (++i < 200) {
            initAndCheck(2, 2);
        }

        i = 0;
        while (++i < 200) {
            initAndCheck(3, 5);
        }

        i = 0;
        while (++i < 200) {
            initAndCheck(6, 11);
        }

        i = 0;
        while (++i < 200) {
            initAndCheck(12, 3);
        }
    }

    /**
     * 要求每一个row中的数据 = rows中分摊数据之和
     *
     * @param rows
     * @param row
     * @return
     */
    private <T> boolean checkCol(Map<String, Map<Integer, BigDecimal>> rows, List<AvgShareGenericsUtil.AvgParam<String, BigDecimal>> row) {
        for (AvgShareGenericsUtil.AvgParam<String, BigDecimal> entry : row) {
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
    private <T> boolean checkRow(Map<String, Map<Integer, BigDecimal>> rows, List<AvgShareGenericsUtil.AvgParam<Integer, BigDecimal>> col) {
        for (AvgShareGenericsUtil.AvgParam<Integer, BigDecimal> entry : col) {
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

}
