package pl.edu.agh.miss.geobarriersim.logic.map.element;

import java.util.Random;

public class Genes {

    private static final Random RANDOM = new Random();

    private static final double MUTATION_RATE = 0.1;
    private static final double MUTATION_CHANCES = 0.01;


    private static final double MIN_SPEED = 0.0;
    private static final double MAX_SPEED = 1.0;
    private static final double MIN_ROAM_TENDENCY = 0.0;
    private static final double MAX_ROAM_TENDENCY = 1.0;
    private static final double MIN_HUNGER_THRESHOLD = 0.1;
    private static final double MAX_HUNGER_THRESHOLD = 0.9;

    private static final double STANDARD_DEVIATION = 0.2;
    private static final double DEFAULT_SPEED = 0.3;
    private static final double DEFAULT_ROAM_TENDENCY = 0.2;
    private static final double DEFAULT_HUNGER_THRESHOLD = 0.9;

    public final double speed;
    public final double roamTendency;
    public final double hungerThreshold;

    private Genes(double speed, double roamTendency, double hungerThreshold) {
        this.speed = speed;
        this.roamTendency = roamTendency;
        this.hungerThreshold = hungerThreshold;
    }


    public static Genes getDefault() {
        return new Genes(getDefaultValue(DEFAULT_SPEED, MIN_SPEED, MAX_SPEED), getDefaultValue(DEFAULT_ROAM_TENDENCY, MIN_ROAM_TENDENCY, MAX_ROAM_TENDENCY), getDefaultValue(DEFAULT_HUNGER_THRESHOLD, MIN_HUNGER_THRESHOLD, MAX_HUNGER_THRESHOLD));
    }

    private static double getDefaultValue(double value, double min, double max) {
         double modified_value = RANDOM.nextGaussian(value, STANDARD_DEVIATION);
         return Math.max(min, Math.min(max, modified_value));
    }

    private static double mutate(double value, double min, double max) {
        if (Math.random() < MUTATION_CHANCES) {
            double mutation = RANDOM.nextBoolean() ? MUTATION_RATE : -MUTATION_RATE;

            return Math.max(min, Math.min(max, value + mutation));
        }
        return value;
    }

    public static Genes combine(Genes parent1, Genes parent2) {
        double speed = randomBetween(parent1.speed, parent2.speed);
        double roamTendency = randomBetween(parent1.roamTendency, parent2.roamTendency);
        double hungerThreshold = randomBetween(parent1.hungerThreshold, parent2.hungerThreshold);


        return new Genes(
            mutate(speed, MIN_SPEED, MAX_SPEED),
            mutate(roamTendency, MIN_ROAM_TENDENCY, MAX_ROAM_TENDENCY),
            mutate(hungerThreshold, MIN_HUNGER_THRESHOLD, MAX_HUNGER_THRESHOLD)
        );
    }

    private static double randomBetween(double val1, double val2) {
        double min = Math.min(val1, val2);
        double max = Math.max(val1, val2);
        if (min == max) {
            return min;
        }
        return RANDOM.nextDouble(min, max);
    }
}
