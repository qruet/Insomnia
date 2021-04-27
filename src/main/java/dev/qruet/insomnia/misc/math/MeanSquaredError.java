package dev.qruet.insomnia.misc.math;

import org.bukkit.Bukkit;

public class MeanSquaredError {

    public static double calculate(double[] sample, double[] predicted) {
        int n = sample.length;
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            double diff = sample[i] - predicted[i];
            sum = sum + diff * diff;
        }
        double mse = sum / n;
        return Math.sqrt(mse);
    }

}
