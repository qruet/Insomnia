package dev.qruet.insomnia.data;

import dev.qruet.insomnia.misc.math.MeanSquaredError;
import dev.qruet.insomnia.misc.math.Normalize;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.type.Bed;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Stack;
import java.util.UUID;

public class BedTracker {

    private final UUID id;
    private Stack<BedDataNode> records;
    private int uniqueBeds;

    public BedTracker(UUID id) {
        this.id = id;
        this.uniqueBeds = 0;
        this.records = new Stack<>();
    }

    public boolean record(Location location) {
        records.push(new BedDataNode(location));
        records.parallelStream().forEach(r -> {
            if (r.loc.distance(location) > 35) {
                uniqueBeds++;
            }
        });
        return isSporadic();
    }

    public void resetTracker() {
        records.clear();
        this.uniqueBeds = 0;
    }

    public UUID getUUID() {
        return id;
    }

    public boolean isSporadic() {
        return testForSporadicity();
    }

    /**
     * Compares most recent sleep record for sporadic sleep patterns
     *
     * @return Is player's sleep pattern sporadic
     */
    private boolean testForSporadicity() {
        if (records.size() < 5)
            return false;


        long[] sample = new long[records.size()];

        int i = 0;
        Iterator<BedDataNode> nodeIterator = records.iterator();
        while (nodeIterator.hasNext()) {
            sample[i++] = nodeIterator.next().timestamp;
        }

        double[] n_sample = Normalize.array(sample, 0, 1);
        double[] predicted = new double[records.size()];

        float[] out = Normalize.bestApproximate(n_sample);
        float m = out[0];
        float c = out[1];

        for (int j = 0; j < predicted.length; j++) {
            predicted[j] = (m * (float) j) + c;
        }

        double[] n_predicted = Normalize.array(predicted, 0 ,1);

        double mse = MeanSquaredError.calculate(n_sample, n_predicted);
        Bukkit.broadcastMessage("m = " + m);
        Bukkit.broadcastMessage("Sample: " + Arrays.toString(n_sample));
        Bukkit.broadcastMessage("Predicted: " + Arrays.toString(n_predicted));
        Bukkit.broadcastMessage("Unique Beds: " + uniqueBeds);
        Bukkit.broadcastMessage("SMSE = " + mse);
        return Math.abs(mse) >= 0.5;
    }

    private class BedDataNode {

        public Location loc;
        public long timestamp;

        public BedDataNode(Location loc) {
            this.loc = loc;
            this.timestamp = System.currentTimeMillis();
        }

    }

}
