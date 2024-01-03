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

    public void getParameters(String filePath) {
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

    public void initScheduler() {
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

    public void testSchedule(String filePath) {
        int generated = 0, executed = 0, serverCount = 0;
        getParameters(filePath);
        initScheduler();

        while (executed != nJobs) {
            Event ev = prQueue.poll();

            if (ev.isArrival() && executed < nJobs) {
                // this tells me it the server is empty
                if (servers.get(serverCount % kServers).addEvent(ev)) {
                    Event temp = new Event(false, ev.getTime() + categories.get(ev.getCat()).newService(), ev.getCat());
                    temp.setServiceTime(categories.get(ev.getCat()).getServiceTime());
                    temp.setIdServer(serverCount % kServers);
                    prQueue.add(temp);
                }
                if (nJobs < 20){
                    System.out.println(ev);
                    prQueue.add(new Event(true, ev.getTime() + categories.get(ev.getCat()).newArrival(), ev.getCat()));
                }
                serverCount++;
            }

            // execute firts nJobs
            if (!ev.isArrival() && generated < nJobs) {

            }




        }
    }

    public void schedule(String filePath) {
        double tempETa = 0, ETa = 0, AQTall = 0;
        int servCount = 0;

        //prQueue = new PriorityQueue<>(nJobs);
        //servers = new ArrayList<>(kServers);
        getParameters(filePath);

        // K, H, N, R, P
        System.out.println(kServers + "," + hCategories + "," + nJobs + "," + rRuns + "," + pPolicy);

        for (int j = 0; j < rRuns; j++) {
            int i = 0, executed = 0, count = 0;
            initScheduler();
            while (i != nJobs || executed != nJobs) {
                Event event = prQueue.poll();
                //System.out.println("E: " + event);
                //System.out.println("prima: " + printPrQueue());

                if (event.isArrival()) {
                    if (servers.get(servCount % kServers).addEvent(event)) {
                        Event temp = new Event(false, event.getTime() + categories.get(event.getCat()).newService(), event.getCat());
                        temp.setServiceTime(categories.get(temp.getCat()).getServiceTime());
                        temp.setIdServer(servCount % kServers);
                        if (count < nJobs) {
                            temp.setToPrint(true);
                            count++;
                        }
                        // System.out.println("category: " + event.getCat());
                        // System.out.println("service time: " + categories.get(event.getCat()).getServiceTime());
                        // System.out.println("Exec J: " + temp);
                        prQueue.add(temp);
                    }
                    servCount++;

                    if (i < nJobs) {
                        if (nJobs < 20) System.out.println(event);
                        prQueue.add(new Event(true, event.getTime() + categories.get(event.getCat()).newArrival(), event.getCat()));
                        i++;
                    }
                }

                if (!event.isArrival()) {
                    tempETa = event.getTime(); // TODO tutti o solo quelli da stampare? in ogni caso viene sbagliato
                    if (event.isToPrint()) AQTall += event.getTime() - event.getServiceTime();
                    servers.get(event.getIdServer()).executeJob();
                    if (!servers.get(event.getIdServer()).isEmpty()) {
                        Event temp = new Event(false, event.getTime() + categories.get(servers.get(event.getIdServer()).getFirst().getCat()).newService(), servers.get(event.getIdServer()).getFirst().getCat());
                        temp.setServiceTime(categories.get(servers.get(event.getIdServer()).getFirst().getCat()).getServiceTime());
                        temp.setIdServer(event.getIdServer());
                        if (count < nJobs) {
                            temp.setToPrint(true);
                            count++;
                        }
                        prQueue.add(temp);
                    }
                    if (executed < nJobs) {
                        if (nJobs < 20) System.out.println(event);
                        executed++;
                    }
                }

                //System.out.println("toPrint: " + toPrint);
                //System.out.println("dopo: " + printPrQueue());
                //System.out.println("--------");
                //System.out.println();
            }
            //System.out.println("tmp: " + tempETa);
            ETa += tempETa;
        }
        System.out.println(ETa / rRuns);
        System.out.println(AQTall / (nJobs * rRuns));
    }

    public void while_schedule(String filePath) {
        double tempETa = 0, ETa = 0, AQTall = 0;
        int servCount = 0;

        //prQueue = new PriorityQueue<>(nJobs);
        //servers = new ArrayList<>(kServers);
        getParameters(filePath);

        // K, H, N, R, P
        System.out.println(kServers + "," + hCategories + "," + nJobs + "," + rRuns + "," + pPolicy);


        initScheduler(); // TODO prova a fare questo con la versione while (gli if per controllare se è da stampare o meno)

        int i = 0, executed = 0, count = 0, runs = 0;
        while (i != nJobs * rRuns || executed != nJobs * rRuns) {
            if (executed % nJobs == 0 && executed != 0) initScheduler();


            Event event = prQueue.poll();
            //System.out.println("E: " + event);
            //System.out.println("prima: " + printPrQueue());

            if (event.isArrival()) {
                if (servers.get(servCount % kServers).addEvent(event)) {
                    Event temp = new Event(false, event.getTime() + categories.get(event.getCat()).newService(), event.getCat());
                    temp.setServiceTime(categories.get(temp.getCat()).getServiceTime());
                    temp.setIdServer(servCount % kServers);
                    if (count < nJobs * rRuns) {
                        temp.setToPrint(true);
                        count++;
                    }
                    // System.out.println("category: " + event.getCat());
                    // System.out.println("service time: " + categories.get(event.getCat()).getServiceTime());
                    // System.out.println("Exec J: " + temp);
                    prQueue.add(temp);
                }
                servCount++;

                if (i < nJobs * rRuns) {
                    if (nJobs < 20) System.out.println(event);
                    prQueue.add(new Event(true, event.getTime() + categories.get(event.getCat()).newArrival(), event.getCat()));
                    i++;
                }
            }

            if (!event.isArrival()) {
                if (executed % nJobs == 0) {
                    tempETa = event.getTime(); // TODO tutti o solo quelli da stampare? in ogni caso viene sbagliato
                    System.out.println("tmp: " + tempETa);
                    ETa += tempETa;
                }
                if (event.isToPrint()) AQTall += event.getServiceTime();
                servers.get(event.getIdServer()).executeJob();
                if (!servers.get(event.getIdServer()).isEmpty()) {
                    Event temp = new Event(false, event.getTime() + categories.get(servers.get(event.getIdServer()).getFirst().getCat()).newService(), servers.get(event.getIdServer()).getFirst().getCat());
                    temp.setServiceTime(categories.get(servers.get(event.getIdServer()).getFirst().getCat()).getServiceTime());
                    temp.setIdServer(event.getIdServer());
                    if (count < nJobs * rRuns) {
                        temp.setToPrint(true);
                        count++;
                    }
                    prQueue.add(temp);
                }
                if (executed < nJobs * rRuns) {
                    if (nJobs < 20) System.out.println(event);
                    executed++;
                }
            }

            //int sCount = 0;
            //for (Server s : servers) {
            //    System.out.println("S" + sCount + ": " + s);
            //    sCount++;
            //}

            //System.out.println("toPrint: " + toPrint);
            //System.out.println("dopo: " + printPrQueue());
            //System.out.println("--------");
            //System.out.println();


        }
        System.out.println(ETa / rRuns);

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
