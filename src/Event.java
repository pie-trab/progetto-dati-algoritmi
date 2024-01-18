/*
    Trabuio Pietro, 2066686
 */
public class Event implements Comparable<Event> {
    private boolean isArrival;
    private int catIndex;
    private double time;
    private double serviceTime = 0.0;
    private int idServer = -1;

    /**
     * Parametric constructor
     * @param isArrival boolean true: is arrival, false: is execution
     * @param time double event time
     * @param catIndex int category
     */
    public Event(boolean isArrival, double time, int catIndex) {
        this.isArrival = isArrival;
        this.catIndex = catIndex;
        this.time = time;
    }

    /**
     * Returns the category of the event
     * @return category of the event
     */
    public int getCat() {
        return catIndex;
    }

    /**
     * Returns the time of the event
     * @return time of the event
     */
    public double getTime() {
        return time;
    }

    /**
     * Returns the id of the server containing the event
     * @return id of the server containing the event
     */
    public int getIdServer() {
        return idServer;
    }

    /**
     * Return if is arrival of execution
     * @return boolean true: is arrival, false: is execution
     */
    public boolean isArrival() {
        return isArrival;
    }

    /**
     * Rerun service time
     * @return service time
     */
    public double getServiceTime() {
        return serviceTime;
    }

    /**
     * Sets the service time for this event
     * @param serviceTime  service time to set
     */
    public void setServiceTime(double serviceTime) {
        this.serviceTime = serviceTime;
    }


    /**
     * Sets the id of the server containing this event
     * @param idServer  id of the server
     */
    public void setIdServer(int idServer) {
        this.idServer = idServer;
    }

    /**
     * Implementation of compareTo method to implement the Comparable interface
     * @param o the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
                is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(Event o) {
        return Double.compare(this.getTime(), o.getTime());
    }

    /**
     * Override of toString to print a Server
     * @return to string
     */
    @Override
    public String toString() {
        return time + "," + serviceTime + "," + catIndex;
    }
}
