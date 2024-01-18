/*
    Trabuio Pietro, 2066686
 */

import java.util.Random;

public class Category {
    private Random arrivaGen;
    private Random serviceGen;
    private double lamArrival;
    private double lamService;
    private double lastArrival = 0;
    private double lastService = 0;

    /**
     * Parametric constructor
     * @param lamArrival double lamda arrival for the category
     * @param lamService double lamda service for the category
     * @param seedArrival int seed for arrival
     * @param seedService int seed for service
     */
    public Category(double lamArrival, double lamService, int seedArrival, int seedService) {
        this.arrivaGen = new Random(seedArrival);
        this.serviceGen = new Random(seedService);
        this.lamArrival = lamArrival;
        this.lamService = lamService;
    }

    /**
     * Generates a new arrival time following the project guidelines
     * @return lastArrival time
     */
    public double newArrival() {
        float alpha = arrivaGen.nextFloat();
        lastArrival = -(1 / lamArrival) * Math.log(1 - alpha);
        return lastArrival;
    }

    /**
     * Generates a new service time following the project guidelines
     * @return lastService time
     */
    public double newService() {
        float alpha = serviceGen.nextFloat();
        lastService = -(1 / lamService) * Math.log(1 - alpha);
        return lastService;
    }

    /**
     * Return service time
     * @return double service time
     */
    public double getServiceTime() {
        return lastService;
    }
}


