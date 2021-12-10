package com.github.liuyueyi.quick.algorighm.leetcode.l0001;

import java.util.*;

/**
 * 给定一个整数数组 nums 和一个目标值 target，请你在该数组中找出和为目标值的那 两个 整数，并返回他们的数组下标。
 * <p>
 * 你可以假设每种输入只会对应一个答案。但是，你不能重复利用这个数组中同样的元素。
 * <p>
 * 示例:
 * <p>
 * 给定 nums = [2, 7, 11, 15], target = 9
 * <p>
 * 因为 nums[0] + nums[1] = 2 + 7 = 9
 * 所以返回 [0, 1]
 *
 * @author yihui
 * @date 2021/12/10
 */
public class TwoSum {

    private void printTitle(int[] nums, int target) {
        System.out.println("初始化数据： nums = " + Arrays.toString(nums) + " \t 目标值: target = " + target);
    }

    private void printMap(int[]nums, int target, int index, Map<Integer, List<Integer>> map) {
        System.out.println("\nsubVal 初始化，i = " + index);
        System.out.println("subVal = target - nums[i] = " + target + " - " + nums[index]);
        for (Map.Entry<Integer, List<Integer>> entry: map.entrySet()) {
            String prefix = "[ ]";
            if (entry.getKey() == nums[index]) {
                prefix = "[+]";
            }
            System.out.println(prefix + " subValue -> indexList: " + entry.getKey() + " -> " + entry.getValue());
        }
    }

    private void printMap(Map<Integer, List<Integer>> map) {
        for (Map.Entry<Integer, List<Integer>> entry: map.entrySet()) {
            System.out.println("target - nums[]: subValue -> indexList: " + entry.getKey() + " -> " + entry.getValue());
        }
    }

    private void printCheck(int[] nums, int target, int i, int j, Map<Integer, List<Integer>> subValue, List<Integer> list) {
        System.out.println();
        if (list == null) {
            System.out.println("遍历寻找目标值: i = " + i);
            System.out.println("nums[i] in map.keys() -> " + nums[i] + " in " + subValue.keySet() + " => false");
            System.out.println("continue loop...");
            return;
        }

        System.out.println("遍历寻找目标值: i = " + i);
        System.out.println("nums[i] in map.keys() -> " + nums[i] + " in " + subValue.keySet() + " => true");
        System.out.println("选中目标队列：" + list + " ，准备遍历寻找目标值: " + (target - nums[i]));
        System.out.println("遍历序号: j = " + j);
        if (j != i) {
            System.out.println(">> 命中: j = " + j + "; nums[i] + nums[j] = target => " + nums[i] + " + " + nums[j] + " = " + target);
        } else {
            System.out.println(">> i == j  == " + j + " 同一个数据，忽略!" );
        }
    }

    private void printLine() {
        System.out.println("-----------------------------------------------------------");
    }

    public int[] twoSum(int[] nums, int target) {
        // 输出日志
        printTitle(nums, target);
        printLine();

        Map<Integer, List<Integer>> subValue = new HashMap<>(nums.length);
        for (int i = 0; i < nums.length; i++) {
            int sub = target - nums[i];
            List<Integer> indexList = subValue.get(sub);
            if (indexList == null) {
                indexList = new ArrayList<>();
                subValue.put(sub, indexList);
            }
            indexList.add(i);

            // 日志输出
            printTitle(nums, target);
            printMap(nums, target, i, subValue);
            printLine();
        }

        for (int i = 0; i < nums.length; i++) {
            List<Integer> list = subValue.get(nums[i]);
            if (list == null) {
                printTitle(nums, target);
                printMap(subValue);
                printCheck(nums, target, i, -1, subValue, null);
                printLine();

                continue;
            }


            for (int j : list) {
                printTitle(nums, target);
                printMap(subValue);
                printCheck(nums, target, i, j, subValue, list);
                printLine();

                if (j != i) {
                    return new int[]{i, j};
                }
            }
        }
        System.out.println("miss, continue loop...");
        return new int[]{};
    }

    public static void main(String[] args) {
        TwoSum twoSum = new TwoSum();
//        int[] ans = twoSum.twoSum(new int[]{3, 3}, 6);
//        System.out.println(Arrays.toString(ans));
//
        int[] ans2 = twoSum.twoSum(new int[]{10, 10, 5, 1, 3, 8, 7, 2}, 10);
        System.out.println(Arrays.toString(ans2));
    }

}
