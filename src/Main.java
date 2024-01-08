public class
Main {
    public static void main(String[] args) {
        Scheduler s = new Scheduler();

        s.schedule(".\\io_files\\input_pdf.in");
        //s.schedule(".\\io_files\\input_K3_H2_N8_R1_P0.in");
        // s.schedule(".\\io_files\\input_K6_H2_N6_R1_P0.in");
        // s.schedule(".\\io_files\\input_K4_H3_N50_R10_P0.in");

        // s.initScheduler(".\\io_files\\input_K6_H2_N6_R1_P0.in"); // TODO check this directory outside intellij project
        // s.initScheduler(".\\io_files\\input_K4_H3_N50_R10_P0.in"); // TODO check this directory outside intellij project
        // s.schedule(".\\io_files\\input_pdf.in");
        // s.while_schedule(".\\io_files\\input_pdf.in");
        // s.schedule(".\\io_files\\input_K6_H2_N6_R1_P0.in");
        // s.schedule(".\\io_files\\input_K4_H3_N50_R10_P0.in");
        // s.testSchedule(".\\io_files\\input_K4_H3_N50_R10_P0.in");
        // s.schedule(".\\io_files\\input_K100_H3_N100000_R5_P0.in");
    }
}