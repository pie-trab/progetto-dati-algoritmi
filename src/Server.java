import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Server implements Comparable<Server> {
    private LinkedList<Event> queue;
    private double totalTime;
    private int idServer;

    /**
     * Parametric constructor
     *
     * @param idServer int unique id for each server
     */
    public Server(int idServer) {
        queue = new LinkedList<Event>();
        setIdServer(idServer);
    }

    /**
     * Adds an event to the server queue and returns true if the server was empty, false otherwise.
     *
     * @param e Event to add to the server
     * @return boolean true: is empty, false: is not empty
     */
    public boolean addEvent(Event e) {
        boolean temp = queue.isEmpty();
        totalTime += e.getTime();
        queue.add(e);
        return temp;
    }

    /**
     * Executes the first event in the server queue and returns it.
     *
     * @return Event just executed
     */
    public Event executeJob() {
        if (!queue.isEmpty()) {
            Event temp = queue.poll();
            totalTime -= temp.getTime();
            return temp;
        } else throw new NoSuchElementException("Server queue is empty");
    }

    /**
     * Returns true if the server queue is empty, false otherwise.
     *
     * @return boolean true: is empty, false: is not empty
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    /**
     * Returns the first event in the server queue.
     *
     * @return Event first event in the server queue
     */
    public Event getFirst() {
        return queue.getFirst();
    }

    /**
     * Return total time of the server
     *
     * @return double total time of the server
     */
    public double getTotalTime() {
        return totalTime;
    }

    /**
     * Returns the id of the server
     *
     * @return int id server
     */
    public int getIdServer() {
        return idServer;
    }

    /**
     * Sets the id of the server
     *
     * @param idServer int idServer
     */
    public void setIdServer(int idServer) {
        this.idServer = idServer;
    }

    /**
     * Implementation of compareTo method to implement the Comparable interface
     * @param s the object to be compared.
     * @return a negative integer, zero, or a positive integer as this object
               is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(Server s) {
        return Double.compare(this.getTotalTime(), s.getTotalTime());
    }

    /**
     * Override of toString to print a Server
     * @return to string
     */
    @Override
    public String toString() {
        String str = "";
        for (Event e : queue) {
            str += "[" + e + "]";
        }
        return str;
    }
}
