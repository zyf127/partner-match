package com.zyf.utils;

import com.zyf.common.ErrorCode;
import com.zyf.exception.BusinessException;

import java.util.Collections;
import java.util.List;

/**
 * 算法工具类
 *
 * @author zyf
 */
public class AlgorithmUtils {


    /**
     * 编辑距离算法（用于计算最相似的两个列表）
     * 原理：https://blog.csdn.net/tianjindong0804/article/details/115803158
     * 
     * @param list1 标签列表1
     * @param list2 标签列表2
     * @return 编辑距离
     */
    public static int minDistance(List<String> list1, List<String> list2) {
        if (list1 == null || list2 == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "标签列表不能为 null");
        }

        int[][] dp = new int[list1.size() + 1][list2.size() + 1];
        //初始化DP数组
        for (int i = 0; i <= list1.size(); i++) {
            dp[i][0] = i;
        }
        for (int i = 0; i <= list2.size(); i++) {
            dp[0][i] = i;
        }
        int cost;
        for (int i = 1; i <= list1.size(); i++) {
            for (int j = 1; j <= list2.size(); j++) {
                if (list1.get(i - 1).equals(list2.get(j - 1))) {
                    cost = 0;
                } else {
                    cost = 1;
                }
                dp[i][j] = min(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + cost);
            }
        }
        return dp[list1.size()][list2.size()];
    }

    private static int min(int x, int y, int z) {
        return Math.min(x, Math.min(y, z));
    }
}