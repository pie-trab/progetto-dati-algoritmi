// End Time: ET(A) ultimo tempo di esecuzione di un job tra gli N arrivati, quanto tempo server al sistema per eseguire)
// tutti gli N job arrivati

// Average Queue Time: ATQ-All(A) tempo medio di attesa in coda di un job A

// Average Queue Time: ATQ-All(A, r) tempo medio di attesa in coda di un job A di categoria r

import java.io.File;
import java.util.*;

public class Scheduler {
    private static PriorityQueue<Event> prQueue;
    private static ArrayList<Server> servers;
    public static ArrayList<Category> categories;
    private static int kServers;
    private static int hCategories;
    private static int nJobs;
    private static int rRuns;
    private static int pPolicy;

    public static void main(String[] args) {
        Scheduler s = new Scheduler();


        //s.schedule(".\\io_files\\input_pdf.in");
        s.schedule(".\\io_files\\input_K4_H3_N50_R10_P0.in");
        // TODO check this directory outside intellij project
    }

    public static void schedule(String filePath) {
        double tempETa = 0, ETa = 0, AQTall = 0;
        int servCount = 0;

        // gets parameters form file
        getParameters(filePath);


        double[] AQTcat = new double[hCategories];
        double[] countAQTcat = new double[hCategories];
        double[] avgService = new double[hCategories];


        // prints the parameters for the output
        // K, H, N, R, P
        System.out.println(kServers + "," + hCategories + "," + nJobs + "," + rRuns + "," + pPolicy);

        for (int i = 0; i < rRuns; i++) {
            int generated = 0, executed = 0;
            double tempAQTall = 0;
            double[] tempAQTcat = new double[hCategories];
            int[] tempCountAQTcat = new int[hCategories];
            double[] tempAvgService = new double[hCategories];

            // initialize the scheduler every run
            initScheduler();

            // executing until nJobs are executed
            while (executed != nJobs) {
                ArrayList<Double> eventTimes = new ArrayList<>();
                // discards jobs arrived after the nJobs spec
                if (generated >= nJobs) {
                    while (prQueue.peek().isArrival()) {
                        prQueue.poll();
                    }
                }

                Event event = prQueue.poll();

                if (event.isArrival()) {
                    if (servers.get(servCount % kServers).addEvent(event)) { // if server is empty
                        Event temp = new Event(false, event.getTime() + categories.get(event.getCat()).newService(), event.getCat());
                        temp.setServiceTime(categories.get(temp.getCat()).getServiceTime());
                        temp.setIdServer(servCount % kServers);
                        prQueue.add(temp);
                    }

                    servCount++;

                    if (nJobs < 20) System.out.println(event);
                    prQueue.add(new Event(true, event.getTime() + categories.get(event.getCat()).newArrival(), event.getCat()));
                    generated++;
                }

                if (!event.isArrival()) {
                    tempETa = event.getTime();
                    Event tempEvent = servers.get(event.getIdServer()).executeJob();
                    if (event.getTime() - event.getServiceTime() >= tempEvent.getTime()) {
                        tempAQTall += event.getTime() - event.getServiceTime() - tempEvent.getTime();
                        tempAQTcat[event.getCat()] += event.getTime() - event.getServiceTime() - tempEvent.getTime();
                    } else {
                        tempAQTall += tempEvent.getTime();
                        tempAQTcat[event.getCat()] += tempEvent.getTime();
                    }
                    // tempAQTall += event.getTime() - event.getServiceTime() - tempEvent.getTime();
                    // tempAQTcat[event.getCat()] += event.getTime() - event.getServiceTime() - tempEvent.getTime();
                    tempAvgService[event.getCat()] += event.getServiceTime();
                    tempCountAQTcat[event.getCat()]++;

                    if (!servers.get(event.getIdServer()).isEmpty()) {
                        Event temp = new Event(false, event.getTime() + categories.get(servers.get(event.getIdServer()).getFirst().getCat()).newService(), servers.get(event.getIdServer()).getFirst().getCat());
                        temp.setServiceTime(categories.get(servers.get(event.getIdServer()).getFirst().getCat()).getServiceTime());
                        temp.setIdServer(event.getIdServer());
                        prQueue.add(temp);

                    }

                    if (nJobs < 20) System.out.println(event);
                    executed++;

                }
            }
            // sum the last time of execution
            ETa += tempETa;
            AQTall += tempAQTall / nJobs;
            for (int j = 0; j < hCategories; j++) {
                AQTcat[j] += tempAQTcat[j];
                countAQTcat[j] += tempCountAQTcat[j];
                avgService[j] += tempAvgService[j];
            }

        }
        System.out.println(ETa / rRuns);
        System.out.println(AQTall / rRuns);
        for (int j = 0; j < hCategories; j++) {
            System.out.println(countAQTcat[j] / rRuns + "," + ((AQTcat[j]) / countAQTcat[j]) + "," + (avgService[j] / countAQTcat[j]));
        }
    }

    /**
     * Extract the initial parameters from the input file
     *
     * @param filePath input file path
     */
    public static void getParameters(String filePath) {
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
                catsIn.useLocale(Locale.US);
                catsIn.useDelimiter(",");

                Category temp = new Category(catsIn.nextDouble(), catsIn.nextDouble(), catsIn.nextInt(), catsIn.nextInt());
                categories.add(temp);

                catsIn.close();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage() + " " + e.getCause());
        }
    }

    /**
     * Initialises the scheduler initializing and filling with the starting jobs the prQueue and initializing the servers
     */
    public static void initScheduler() {
        servers = new ArrayList<>(kServers);
        prQueue = new PriorityQueue<>(nJobs);

        // init servers
        for (int i = 0; i < kServers; i++) {
            servers.add(new Server());
        }

        // init queue
        prQueue = new PriorityQueue<Event>();
        for (int i = 0; i < hCategories; i++) {
            prQueue.add(new Event(true, categories.get(i).newArrival(), i));
        }
    }


    public double getGreaterTime(double time) {
        double temp = 0;
        for (Event e : prQueue) {
            if (e.getTime() > time && e.isArrival()) {
                temp += e.getTime();
            }
        }

        return temp;
    }

    public String printPrQueue() {
        String str = "";
        for (Event e : prQueue) {
            str += "[t:" + e.getTime() + ", isAr: " + e.isArrival() + ", cat: " + e.getCat() + "]";
        }
        return str;
    }

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
