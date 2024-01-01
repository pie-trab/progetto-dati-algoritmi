public class Event implements Comparable<Event> {
    private boolean isArrival;
    private boolean toPrint;
    private int catIndex;
    private double time;
    private double serviceTime = 0.0;
    private int idServer = -1;

    public Event(boolean isArrival, double time, int catIndex) {
        this.isArrival = isArrival;
        this.toPrint = false;
        this.catIndex = catIndex;
        this.time = time;
    }
    public Event(boolean isArrival, boolean toPrint, double time, int catIndex) {
        this.isArrival = isArrival;
        this.toPrint = toPrint;
        this.catIndex = catIndex;
        this.time = time;
    }

    public int getCat() {
        return catIndex;
    }

    public double getTime() {
        return time;
    }

    public int getIdServer() {
        return idServer;
    }

    public boolean isArrival() {
        return isArrival;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getServiceTime() {
        return serviceTime;
    }

    public boolean isToPrint() {
        return toPrint;
    }

    public void setToPrint(boolean toPrint) {
        this.toPrint = toPrint;
    }

    public void setServiceTime(double serviceTime) {
        this.serviceTime = serviceTime;
    }



    public void setIdServer(int idServer) {
        this.idServer = idServer;
    }

    @Override
    public int compareTo(Event o) {
        if (this.getTime() < o.getTime()) return -1;
        if (this.getTime() > o.getTime()) return 1;
        return 0;
        // return Double.compare(this.getTime() - o.getTime(), 0.0);
        //System.out.println("Event: " + (this.getTime() - o.getTime()) + " " + temp);
    }

    @Override
    public String toString() {
        return time + "," + serviceTime + "," + catIndex;
    }
}
