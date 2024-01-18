/*
    Personalized policy
    The personalized scheduling policy is based on choosing the least "busy" server. To do so, a job is assigned to
    the server with the least total time (the sum of the time of all events in queue). To find it, it's checked the Server
    class field "totalTime" that is constantly updated every time a job is added or removed from the server, eliminating
    the need for a hypothetical loop to sum all the events in the queue.
    This approach only required an additional loop to pass all the server each time a job is assigned to a server, this
    should not be a problem given that is reasonable to think that K servers would be much smaller of R runs multiplied
    by N jobs: K << N * R

    Complexity
    This algorithm is of complexity O(R*N) with the round-robin policy and O(R*(N*K)) with the personalised policy.
    Assuming K << N * R the complexity for the second algorithm approaches the one for the first O(R*N*K) ~ O(R*N)
    It's also assumes H << N * R (a loop is used to sum all the parameters for the required metrics)
 */

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Scanner;


public class Scheduler {
    private static PriorityQueue<Event> prQueue; // priority queue of the scheduler
    private static ArrayList<Server> servers; // arraylist of servers
    public static ArrayList<Category> categories; // arraylist of categories
    private static int kServers; // number of servers
    private static int hCategories; // number of categories
    private static int nJobs; // number of Jobs
    private static int rRuns; // number of runs
    private static int pPolicy; // type of policy

    public static void main(String[] args) {
        String filePath = args[0];
    
        Scheduler.schedule(filePath);
    }

    /**
     * Simulates a scheduler based on the parameters read from a file
     *
     * @param filePath String path to the file containing the parameters
     */
    public static void schedule(String filePath) {
        // local field for the required metrics
        double tempETa = 0, ETa = 0, AQTall = 0;
        // count of the server used in the round-robin policy
        int servCount = 0;

        // accessory method to get the parameters form the file
        getParameters(filePath);

        // local arrays for the required metrics
        double[] AQTcat = new double[hCategories];
        double[] countAQTcat = new double[hCategories];
        double[] avgService = new double[hCategories];

        // prints the parameters for the output
        // K, H, N, R, P
        System.out.println(kServers + "," + hCategories + "," + nJobs + "," + rRuns + "," + pPolicy);

        // loop simulating R runs
        for (int i = 0; i < rRuns; i++) { // complexity O(R)
            // count for the generated and executes jobs, reset every run
            int generated = 0, executed = 0;

            // local arrays and parameters for the required metrics
            double tempAQTall = 0;
            double[] tempAQTcat = new double[hCategories];
            double[] tempCountAQTcat = new double[hCategories];
            double[] tempAvgService = new double[hCategories];

            // accessory method to initialize the scheduler every run
            initScheduler();

            // executing until nJobs
            while (executed != nJobs) { // complexity O(N)
                // discards jobs arrived after nJobs
                if (generated >= nJobs) {
                    while (prQueue.peek().isArrival()) {
                        prQueue.poll();
                    }
                }

                // extract a job from the scheduler priority queue
                Event event = prQueue.poll();

                // checks the type of event
                if (event.isArrival()) {
                    // check the policy
                    if (pPolicy == 0) { // round-robin
                        if (servers.get(servCount % kServers).addEvent(event)) { // if server is empty
                            Event temp = new Event(false, event.getTime() + categories.get(event.getCat()).newService(), event.getCat());
                            temp.setServiceTime(categories.get(temp.getCat()).getServiceTime());
                            temp.setIdServer(servCount % kServers); // in round-robin policy the index and id of the server are the same
                            prQueue.add(temp);
                        }
                        servCount++;
                    } else if (pPolicy == 1) { // find the server with the shortest queue
                        // saved the index for the min server
                        int tempIndex = findMinQueue(); // complexity O(K)

                        // adding an event returns true if the server is empty, so is immediately generated the execution event for the job
                        if (servers.get(tempIndex).addEvent(event)) {
                            // create the execution event
                            Event temp = new Event(false, event.getTime() + categories.get(event.getCat()).newService(), event.getCat());
                            temp.setServiceTime(categories.get(temp.getCat()).getServiceTime());
                            temp.setIdServer(tempIndex);
                            // add the event on the queue
                            prQueue.add(temp);
                        }
                    }

                    // print the event if N jobs is less than 20
                    if (nJobs < 20) System.out.println(event);

                    // generate and add a new event to the priority queue of the scheduler
                    prQueue.add(new Event(true, event.getTime() + categories.get(event.getCat()).newArrival(), event.getCat()));
                    generated++;
                }

                if (!event.isArrival()) {
                    // get the current ETa
                    tempETa = event.getTime();
                    // execute the job and saves it job to get the necessary metrics
                    Event tempEvent = servers.get(event.getIdServer()).executeJob();
                    tempAQTall += event.getTime() - event.getServiceTime() - tempEvent.getTime();
                    tempAQTcat[event.getCat()] += event.getTime() - event.getServiceTime() - tempEvent.getTime();
                    tempAvgService[event.getCat()] += event.getServiceTime();
                    tempCountAQTcat[event.getCat()]++;

                    // if the server is not empty execute the job first in queue
                    if (!servers.get(event.getIdServer()).isEmpty()) {
                        Event temp = new Event(false, event.getTime() + categories.get(servers.get(event.getIdServer()).getFirst().getCat()).newService(), servers.get(event.getIdServer()).getFirst().getCat());
                        temp.setServiceTime(categories.get(servers.get(event.getIdServer()).getFirst().getCat()).getServiceTime());
                        temp.setIdServer(event.getIdServer());
                        prQueue.add(temp);
                    }

                    // print the event if N jobs is less than 20
                    if (nJobs < 20) System.out.println(event);
                    executed++;
                }
            }

            // for each run sums the temporary parameters to the final ones, dividing appropriately for N jobs
            ETa += tempETa;
            AQTall += tempAQTall / nJobs;
            for (int j = 0; j < hCategories; j++) { // O(H)
                AQTcat[j] += tempAQTcat[j] / tempCountAQTcat[j];
                countAQTcat[j] += tempCountAQTcat[j];
                avgService[j] += tempAvgService[j] / tempCountAQTcat[j];
            }
        }

        // prints the required output
        System.out.println(ETa / rRuns);
        System.out.println(AQTall / rRuns);
        for (int j = 0; j < hCategories; j++) { // O(H)
            System.out.println(countAQTcat[j] / rRuns + "," + AQTcat[j] / rRuns + "," + avgService[j] / rRuns);
        }
    }

    /**
     * Extract the initial parameters from the input file
     *
     * @param filePath String input file path
     */
    public static void getParameters(String filePath) {
        // try-catch block for unexpected or missing inputs
        try {
            File inputFile = new File(filePath);
            Scanner myReader = new Scanner(inputFile);
            Scanner firstLine = new Scanner(myReader.nextLine());
            firstLine.useDelimiter(",");

            kServers = Integer.parseInt(firstLine.next());
            servers = new ArrayList<>(kServers);
            hCategories = Integer.parseInt(firstLine.next());
            categories = new ArrayList<>(hCategories);
            nJobs = Integer.parseInt(firstLine.next());
            rRuns = Integer.parseInt(firstLine.next());
            pPolicy = Integer.parseInt(firstLine.next());
            firstLine.close();

            Scanner catsIn;
            while (myReader.hasNextLine()) {
                catsIn = new Scanner(myReader.nextLine());
                // this is important to set '.' and ',' notation for numbers
                catsIn.useLocale(Locale.US);
                catsIn.useDelimiter(",");

                Category temp = new Category(catsIn.nextDouble(), catsIn.nextDouble(), catsIn.nextInt(), catsIn.nextInt());
                categories.add(temp);

                catsIn.close();
            }
        } catch (Exception e) {
            System.err.println("Incorrect or missing file: " + e.getMessage());
        }
    }

    /**
     * Initialises the scheduler, initializing and filling with the starting jobs prQueue and the servers
     */
    public static void initScheduler() {
        servers = new ArrayList<>(kServers);
        prQueue = new PriorityQueue<>(nJobs);

        // init servers
        for (int i = 0; i < kServers; i++) {
            servers.add(new Server(i));
        }

        // init queue
        for (int i = 0; i < hCategories; i++) {
            prQueue.add(new Event(true, categories.get(i).newArrival(), i));
        }
    }

    /**
     * Returns the index of the server with the shortest queue
     *
     * @return int index of the server with the shortest queue
     */
    public static int findMinQueue() {
        double temp = Double.MAX_VALUE;
        int index = 0;
        for (Server s : servers) {
            if (s.getTotalTime() < temp) {
                index = s.getIdServer();
                temp = s.getTotalTime();
            }
        }
        return index;
    }

    /**
     * Override of toString to print a Scheduler
     * @return to string
     */
    @Override
    public String toString() {
        return "Scheduler{" +
                "kServer=" + servers +
                ", hCategories=" + categories +
                ", nJobs=" + nJobs +
                ", rRuns=" + rRuns +
                ", pPolicy=" + pPolicy +
                '}';
    }
}
