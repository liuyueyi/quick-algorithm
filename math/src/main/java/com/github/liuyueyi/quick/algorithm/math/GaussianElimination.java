package com.github.liuyueyi.quick.algorithm.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 高斯消元算法
 *
 * @author yihui
 * @date 2021/12/3
 */
public class GaussianElimination {
    private static List<List<Double>> ary2list(double[][] d) {
        List<List<Double>> result = new ArrayList<>();
        for (double[] c : d) {
            List<Double> l = new ArrayList<>();
            for (double i : c) {
                l.add(i);
            }
            result.add(l);
        }
        return result;
    }

    /**
     * 高斯消元计算
     *
     * @param matrix
     * @return
     */
    public static List<Double> calculate(double[][] matrix) {
        return calculate(ary2list(matrix));
    }


    /**
     * 高斯消元计算：核心思路
     *
     * 第i次循环过程，确保第i -> size行，前i列都是0，然后从最后一行开始，逐一向上计算每个未知数的求解
     * - 对于非唯一解的场景，这里取其中一个未知数为0进行处理
     *
     * 原始矩阵:
     * |    3.0000,     3.0000,     1.0000  |       |    6.0000   |
     * |    2.0000,     6.0000,     3.0000  |   =   |   12.0000   |
     * |    4.0000,     1.0000,     5.0000  |       |   30.0000   |
     * 转换过程 1
     * |    3.0000,     3.0000,     1.0000  |       |    6.0000   |
     * |    0.0000,    -6.0000,    -3.5000  |   =   |  -12.0000   |
     * |    0.0000,     2.2500,    -2.7500  |       |  -16.5000   |
     * 转换过程 2
     * |    3.0000,     3.0000,     1.0000  |       |    6.0000   |
     * |    0.0000,    -6.0000,    -3.5000  |   =   |  -12.0000   |
     * |    0.0000,     0.0000,   -10.8333  |       |  -56.0000   |
     *
     *
     * @param matrix
     * @return
     */
    public static List<Double> calculate(List<List<Double>> matrix) {
        System.out.println("原始矩阵:");
        printMatrix(matrix);
        for (int i = 0; i < matrix.size() - 1; i++) {
            System.out.println("转换过程 " + (i + 1));
            matrix = doGaussianElimination(matrix, i);
            printMatrix((matrix));
        }
        return calResult(matrix);
    }

    /**
     * 高斯消元法
     *
     * @param equation
     * @param x
     */
    public static List<List<Double>> doGaussianElimination(List<List<Double>> equation, int x) {
        int row = equation.size();
        int col = equation.get(0).size();

        // 找到第一行x列对应的参数不为0的值
        List<Double> pickRow = null;

        List<List<Double>> result = new ArrayList<>(equation.subList(0, x));
        for (int i = x; i < row; i++) {
            if (equation.get(i).get(x) != 0) {
                pickRow = equation.get(i);
                result.add(pickRow);
                equation.remove(i);
                break;
            }
        }
        if (pickRow == null) {
            // 这一列没有数据，直接返回
            return equation;
        }

        // x行及之后的所有行，消去第x列，使其为0
        for (int i = x; i < row - 1; i++) {
            if (equation.get(i).get(x) == 0) {
                result.add(equation.get(i));
                continue;
            }

            List<Double> tmp = new ArrayList<>();
            double mut = -1 * pickRow.get(x) / equation.get(i).get(x);
            for (int j = 0; j < col; j++) {
                if (j < x) {
                    tmp.add(0D);
                } else {
                    tmp.add(equation.get(i).get(j) * mut + pickRow.get(j));
                }
            }
            result.add(tmp);
        }
        return result;
    }

    /**
     * 根据消元之后的矩阵，计算最终的结果
     *
     * @param list
     * @return
     */
    private static List<Double> calResult(List<List<Double>> list) {
        //Back Elimination
        int size = list.size();
        // 创建并初始化数组为0
        List<Double> ans = new ArrayList<>(Collections.nCopies(size, 0D));
        for (int i = size - 1; i >= 0; i--) {
            double sum = 0f;
            for (int j = 0; j < size; j++) {
                sum += (list.get(i).get(j)) * ans.get(j);
            }

            if (list.get(i).get(i) == 0) {
                ans.set(i, 0D);
            } else {
                ans.set(i, (list.get(i).get(size) - sum) / list.get(i).get(i));
            }
        }
        System.out.println("=====================================");
        System.out.println("Result");
        printAns(ans);
        return ans;
    }

    private static void printMatrix(List<List<Double>> matrix) {
        int row = matrix.size();
        int col = matrix.get(0).size();
        for (int i = 0; i < row; i++) {
            System.out.print("|");
            for (int j = 0; j < col; j++) {
                System.out.printf("%10.4f", matrix.get(i).get(j));
                if (j < col - 2) System.out.print(", ");
                if (j == col - 2 && i == row / 2) System.out.print("  |   =   |");
                else if (j == col - 2) System.out.print("  |       |");

            }
            System.out.print("   |");
            System.out.println("");
        }
    }

    private static void printAns(List<Double> result) {
        for (int i = 0; i < result.size(); i++) {
            System.out.printf("X_%d = %10.4f\n", (i + 1), result.get(i));
        }
    }
}
