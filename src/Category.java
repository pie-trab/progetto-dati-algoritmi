import java.util.Random;

public class Category {
    private Random arrivaGen;
    private Random serviceGen;
    private double lamArrival;
    private double lamService;
    private double lastArrival = 0;
    private double lastService = 0;

    public Category(double lamArrival, double lamService, int seedArrival, int seedService) {
        this.arrivaGen = new Random(seedArrival);
        this.serviceGen = new Random(seedService);
        this.lamArrival = lamArrival;
        this.lamService = lamService;
    }

    public double newArrival() {
        float alpha = arrivaGen.nextFloat();
        lastArrival = -(1 / lamArrival) * Math.log(1 - alpha);
        return lastArrival;
    }

    public double newService() {
        float alpha = serviceGen.nextFloat();
        lastService = -(1 / lamService) * Math.log(1 - alpha);
        return lastService;
    }

    public double getArrivalTime() {
        return lastArrival;
    }

    public double getServiceTime() {
        return lastService;
    }
}


