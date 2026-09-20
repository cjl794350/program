package com.edu.analysis.ml;

/**
 * K-Means 聚类算法
 * 用于根据学生多维度学习特征进行分层（优秀/良好/需关注）
 */
public class KMeans {

    /**
     * 聚类结果
     */
    public static class ClusterResult {
        public int[] labels;       // 每个样本所属簇
        public double[][] centers; // 各簇中心
        public int iterations;     // 实际迭代次数
    }

    /**
     * K-Means 聚类
     *
     * @param X       数据矩阵 n×f
     * @param k       簇数量
     * @param maxIter 最大迭代次数
     */
    public static ClusterResult fit(double[][] X, int k, int maxIter) {
        int n = X.length;
        int f = X[0].length;

        // 初始化簇中心：均匀选取 k 个样本
        double[][] centers = new double[k][f];
        for (int c = 0; c < k; c++) {
            int idx = (int) ((long) c * n / k);
            centers[c] = X[idx].clone();
        }

        int[] labels = new int[n];
        int iterations = 0;
        for (int iter = 0; iter < maxIter; iter++) {
            iterations = iter + 1;
            boolean changed = false;

            // 分配簇
            for (int i = 0; i < n; i++) {
                int best = 0;
                double bestDist = Double.MAX_VALUE;
                for (int c = 0; c < k; c++) {
                    double d = 0;
                    for (int j = 0; j < f; j++) {
                        double diff = X[i][j] - centers[c][j];
                        d += diff * diff;
                    }
                    if (d < bestDist) {
                        bestDist = d;
                        best = c;
                    }
                }
                if (labels[i] != best) {
                    labels[i] = best;
                    changed = true;
                }
            }

            // 更新簇中心
            double[][] sum = new double[k][f];
            int[] cnt = new int[k];
            for (int i = 0; i < n; i++) {
                cnt[labels[i]]++;
                for (int j = 0; j < f; j++) {
                    sum[labels[i]][j] += X[i][j];
                }
            }
            for (int c = 0; c < k; c++) {
                for (int j = 0; j < f; j++) {
                    centers[c][j] = cnt[c] > 0 ? sum[c][j] / cnt[c] : centers[c][j];
                }
            }

            if (!changed) {
                break;
            }
        }

        ClusterResult r = new ClusterResult();
        r.labels = labels;
        r.centers = centers;
        r.iterations = iterations;
        return r;
    }
}
