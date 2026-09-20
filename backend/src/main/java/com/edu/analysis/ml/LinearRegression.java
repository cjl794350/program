package com.edu.analysis.ml;

import java.util.ArrayList;
import java.util.List;

/**
 * 多元线性回归（梯度下降 + 特征归一化）
 * 用于根据平时成绩、作业成绩、出勤次数预测期末成绩
 */
public class LinearRegression {

    private double[] theta;        // 模型参数 [截距, w1, w2, ...]
    private double[] featureMean;  // 特征均值
    private double[] featureStd;   // 特征标准差

    /**
     * 训练模型
     *
     * @param X      特征矩阵 n×f
     * @param y      标签 n
     * @param lr     学习率
     * @param epochs 迭代次数
     * @return 每次迭代的损失（用于绘制损失曲线）
     */
    public List<Double> fit(double[][] X, double[] y, double lr, int epochs) {
        int n = X.length;
        int f = X[0].length;

        // 特征归一化
        featureMean = new double[f];
        featureStd = new double[f];
        for (int j = 0; j < f; j++) {
            double sum = 0;
            for (double[] row : X) sum += row[j];
            featureMean[j] = sum / n;
            double sq = 0;
            for (double[] row : X) sq += (row[j] - featureMean[j]) * (row[j] - featureMean[j]);
            featureStd[j] = Math.sqrt(sq / n);
            if (featureStd[j] < 1e-9) featureStd[j] = 1.0;
        }
        double[][] Xn = new double[n][f];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < f; j++) {
                Xn[i][j] = (X[i][j] - featureMean[j]) / featureStd[j];
            }
        }

        // 梯度下降
        theta = new double[f + 1];
        List<Double> losses = new ArrayList<>();
        for (int iter = 0; iter < epochs; iter++) {
            double[] grad = new double[f + 1];
            double loss = 0;
            for (int i = 0; i < n; i++) {
                double h = theta[0];
                for (int j = 0; j < f; j++) h += theta[j + 1] * Xn[i][j];
                double err = h - y[i];
                loss += err * err;
                grad[0] += err;
                for (int j = 0; j < f; j++) grad[j + 1] += err * Xn[i][j];
            }
            for (int j = 0; j <= f; j++) theta[j] -= (lr / n) * grad[j];
            if (iter % 10 == 0) {
                losses.add(loss / n);
            }
        }
        return losses;
    }

    /**
     * 预测
     */
    public double predict(double[] x) {
        double h = theta[0];
        for (int j = 0; j < x.length; j++) {
            h += theta[j + 1] * (x[j] - featureMean[j]) / featureStd[j];
        }
        return h;
    }

    /**
     * 计算决定系数 R²
     */
    public double r2(double[][] X, double[] y) {
        double ssRes = 0, ssTot = 0, yMean = 0;
        for (double v : y) yMean += v;
        yMean /= y.length;
        for (int i = 0; i < y.length; i++) {
            double pred = predict(X[i]);
            ssRes += (y[i] - pred) * (y[i] - pred);
            ssTot += (y[i] - yMean) * (y[i] - yMean);
        }
        return ssTot == 0 ? 0 : 1 - ssRes / ssTot;
    }

    public double[] getTheta() {
        return theta;
    }
}
