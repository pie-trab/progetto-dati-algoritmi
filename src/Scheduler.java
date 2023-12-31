// End Time: ET(A) ultimo tempo di esecuzione di un job tra gli N arrivati, quanto tempo server al sistema per eseguire)
// tutti gli N job arrivati

// Average Queue Time: ATQ-All(A) tempo medio di attesa in coda di un job A

// Average Queue Time: ATQ-All(A, r) tempo medio di attesa in coda di un job A di categoria r

import java.io.File;
import java.util.*;

public class Scheduler {
    private PriorityQueue<Event> prQueue;
    private ArrayList<Server> servers;
    public ArrayList<Category> categories;
    private int kServers;
    private int hCategories;
    private int nJobs;
    private int rRuns;
    private int pPolicy;


    public void initScheduler(String filePath) {
        // data from file
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

        // init servers
        for (int i = 0; i < kServers; i++) {
            servers.add(new Server());
        }

        // init queue
        prQueue = new PriorityQueue<Event>();

        // add event to queue
        for (int i = 0; i < hCategories; i++) {
            double arrTemp = categories.get(i).newArrival();

            prQueue.add(new Event(true, arrTemp, i));
        }

        // K, H, N, R, P
        System.out.println(kServers + "," + hCategories + "," + nJobs + "," + rRuns + "," + pPolicy);

    }

    public void schedule() {
        double ETa = 0;
        for (int j = 0; j < rRuns; j++) {
            int i = 0, executed = 0, servCount = 0;

            while (i != nJobs || executed != nJobs) {
                Event event = prQueue.poll();
                //System.out.println("prima: " + printPrQueue());

                if (event.isArrival()) {
                    if (servers.get(servCount % kServers).addEvent(event)) {
                        //Event temp = new Event(false, event.getTime() + categories.get(event.getCat()).newService(), event.getCat());
                        Event temp = new Event(false, event.getTime() + categories.get(event.getCat()).newService(), event.getCat());
                        temp.setServiceTime(categories.get(temp.getCat()).getServiceTime());
                        temp.setIdServer(servCount % kServers);
                        //System.out.println("category: " + event.getCat());
                        //System.out.println("service time: " + categories.get(event.getCat()).getServiceTime());
                        //System.out.println("Exec J: " + temp);


                        prQueue.add(temp);
                    }
                    servCount++;
                    prQueue.add(new Event(true, event.getTime() + categories.get(event.getCat()).newArrival(), event.getCat()));

                    if (i < nJobs) {
                        System.out.println(event);
                        i++;
                    }
                }

                if (!event.isArrival()) {
                    ETa = event.getTime();
                    Event polled = servers.get(event.getIdServer()).executeJob();
                    if (!servers.get(event.getIdServer()).isEmpty()) {
                        Event temp = new Event(false, event.getTime() + categories.get(servers.get(event.getIdServer()).getFirst().getCat()).newService(), servers.get(event.getIdServer()).getFirst().getCat());
                        temp.setServiceTime(categories.get(servers.get(event.getIdServer()).getFirst().getCat()).getServiceTime());
                        temp.setIdServer(event.getIdServer());
                        prQueue.add(temp);
                    }
                    if (executed < nJobs) {
                        System.out.println(event);
                        executed++;
                    }
                }


                // TODO qualche problema con i server

                // System.out.println("S1: " + servers.get(0));
                // System.out.println("S2: " + servers.get(1));


                //System.out.println("dopo: " + printPrQueue());
                //System.out.println("--------");
                //System.out.println();


            }
            System.out.println(ETa);
        }

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
