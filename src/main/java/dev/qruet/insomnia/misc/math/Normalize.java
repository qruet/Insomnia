package dev.qruet.insomnia.misc.math;

public class Normalize {

    public static double[] array(long[] values, int min, int max) {
        double[] norm = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            norm[i] = normalize(values[i], values[0], values[1], min, max);
        }
        return norm;
    }

    public static double[] array(double[] values, int min, int max) {
        double[] norm = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            norm[i] = normalize(values[i], values[0], values[1], min, max);
        }
        return norm;
    }

    private static double normalize(long enteredValue, long minEntry, long maxEntry, long normalizedMin, long normalizedMax) {
        double mx = (enteredValue - minEntry) / (maxEntry - minEntry);
        double preshiftNormalized = mx * (normalizedMax - normalizedMin);
        double shiftedNormalized = preshiftNormalized + normalizedMin;

        return shiftedNormalized;
    }

    private static double normalize(double enteredValue, double minEntry, double maxEntry, double normalizedMin, double normalizedMax) {
        double mx = (enteredValue - minEntry) / (maxEntry - minEntry);
        double preshiftNormalized = mx * (normalizedMax - normalizedMin);
        double shiftedNormalized = preshiftNormalized + normalizedMin;

        return shiftedNormalized;
    }

    /**
     *
     * @param sample
     * @return [mean, y-intercept]
     */
    public static float[] bestApproximate(double[] sample) {
        int i;
        float m, c, sum_x = 0, sum_y = 0, sum_xy = 0, sum_x2 = 0;
        for (i = 0; i < sample.length; i++) {
            sum_x += sample[i];
            sum_y += i;
            sum_xy += sample[i] * i;
            sum_x2 += (i * i);
        }

        m = (sample.length * sum_xy - sum_x * sum_y) / (sample.length * sum_x2 - (sum_x * sum_x));
        c = (sum_y - m * sum_x) / sample.length;
        float[] out = new float[] {m, c};
        return out;
    }

}
