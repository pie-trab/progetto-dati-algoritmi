import java.util.LinkedList;
import java.util.NoSuchElementException;

public class Server {
    private LinkedList<Event> queue;
    private double totalTime;

    public Server() {
        queue = new LinkedList<Event>();
    }

    public boolean addEvent(Event e) {
        boolean temp = queue.isEmpty();
        totalTime += e.getTime();
        queue.add(e);
        return temp;
    }

    public Event executeJob() {
        if (!queue.isEmpty()) return queue.poll();
        else throw new NoSuchElementException("Server queue is empty");
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public Event getFirst() {
        return queue.getFirst();
    }

    public boolean canExecute() {
        return (queue.size() == 1);
    }

    @Override
    public String toString() {
        String str = "";
        for (Event e : queue) {
            str += "[" + e + "]";
        }
        return str;
    }
}
