import java.util.ArrayList;

public class
Main {
    public static void main(String[] args) {
        Scheduler s = new Scheduler();
        s.initScheduler(".\\io_files\\input_pdf.in"); // TODO check this directory outside intellij project
        s.schedule();
    }
}