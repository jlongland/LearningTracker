
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import javax.jws.soap.SOAPBinding;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Created by Ioana on 4/7/2016.
 */
public class MetricComputation {

    public enum Edition { PREVIOUS, CURRENT};

    static HashMap<String, UserMetricComputation> users;
    static List<String> graduates;
    static HashMap<String, Integer> problems;
    static HashMap<Integer, Integer> maximums;

    static HashMap<Integer, BiFunction<UserMetricComputation, Integer, Integer>> metricMethods;

    public static void computeMetrics(String course, int week, String path) throws IOException, ParseException {
        String thresholdPath = path + "thresholds\\";
        String dataPath = path + "week" + week + "\\data\\";
        String metricsPath = path + "week" + week + "\\metrics";

        System.out.println("Initializing course for week " + week + " ... " + course.toUpperCase());
        initialize();

        System.out.println("Loading data for week " + week + "... " + course.toUpperCase());
        //readUsers(graduates, "data\\0. old MOOC\\data\\" + course + "\\" + course.toUpperCase() + "_graduates.csv");
        readUsers(users, path + course.toUpperCase() + "_active.csv");
        loadData(course, dataPath);
        readMaximums(thresholdPath + "maximums.csv", week);

        System.out.println("Writing metrics for week " + week + "... " + course.toUpperCase());
        writeMetrics(users, week, metricsPath, metricsPath + "\\" + course.toUpperCase() + "_metrics.csv");
        System.out.println("Writing scaled metrics for week " + week + "... " + course.toUpperCase());
        writeScaledMetrics(users, week, metricsPath, metricsPath + "\\" + course.toUpperCase() + "_scaled_metrics.csv");
        //writeGraduates(users, week, "data\\0. old MOOC\\" + course.toUpperCase() + "_metrics_graduates.csv");
        //writeNonGraduates(users, week, "data\\0. old MOOC\\" + course.toUpperCase() + "_metrics_non_graduates.csv");

    }

    public static void computeThreshold(String course, int week, String path, int cutOffPercent) throws IOException, ParseException {
        String thresholdPath = path + "thresholds\\";
        String dataPath = path + "week" + week + "\\data\\";
        Edition edition = Edition.PREVIOUS;

        System.out.println("Initializing course... " + course.toUpperCase());
        initialize();

        System.out.println("Loading data... " + course.toUpperCase());
        readUsers(users, dataPath + "graduates.csv");
        loadData(course, edition, dataPath);

        System.out.println("Writing average graduate values... " + course.toUpperCase());
        writeThresholds(week, thresholdPath, "thresholds.csv", cutOffPercent);

        System.out.println("Writing maximums... " + course.toUpperCase());
        writeMaximums(week, thresholdPath, "maximums.csv", cutOffPercent);

        System.out.println("Writing average graduate scaled values... " + course.toUpperCase());
        writeScaledThresholds(week, thresholdPath, "scaled_thresholds.csv", cutOffPercent);
    }



    private static void initialize() {
        users = new HashMap<>();
        graduates = new ArrayList<>();
        problems = new HashMap<>();
        maximums = new HashMap<>();

        metricMethods = new HashMap<>();
        metricMethods.put(1, UserMetricComputation::getSessionsPerWeek);
        metricMethods.put(2, UserMetricComputation::getAverageSessionLength);
        metricMethods.put(3, UserMetricComputation::getAverageTimeBetweenSessions);
        metricMethods.put(4, UserMetricComputation::getForumSessions);
        metricMethods.put(5, UserMetricComputation::getQuizSubmissions);
        metricMethods.put(6, UserMetricComputation::getRecommendedTimeliness);
        metricMethods.put(7, UserMetricComputation::getSessions);
        metricMethods.put(8, UserMetricComputation::getVideosAccessed);
        metricMethods.put(9, UserMetricComputation:: getTimeOnPlatform);
    }

    //READ
    private static void loadData(String course, Edition edition, String path) throws IOException, ParseException {

        readSessions(course, edition, path + "sessions.csv");

        readForumSessions(course, path + "forum_sessions.csv");

        readObservations(course, path + "observations.csv");

        readProblems(path + "problems.csv");
        readSubmissions(course, path + "submissions.csv");

    }

    //Reads from a file with ids on the first column and creates a Hashmap of UserMetricComputation objects with the id as key
    private static void readUsers(HashMap<String, UserMetricComputation> group, String filename) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(filename));
        String [] nextLine;

        csvReader.readNext();

        while ((nextLine = csvReader.readNext()) != null)
            group.put(nextLine[0], new UserMetricComputation(nextLine[0]));

        csvReader.close();

        System.out.println("Learners read: " + users.size());
    }

    private static void readUsers(List<String> group, String filename) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(filename));
        String [] nextLine;

        csvReader.readNext();

        while ((nextLine = csvReader.readNext()) != null)
            group.add(nextLine[0]);

        csvReader.close();
    }

    private static void readSessions(String course, Edition edition, String filename) throws IOException, ParseException {
        CSVReader csvReader = new CSVReader(new FileReader(filename));
        String[] nextLine;
        int week, duration;
        String user_id, start, end;

        nextLine = csvReader.readNext();

        while ((nextLine = csvReader.readNext()) != null) {
            user_id = nextLine[1];
            start = nextLine[2];
            end = nextLine[3];
            duration = Integer.parseInt(nextLine[4]);

            if(users.containsKey(user_id)) {
                week = Utils.getWeek(course, edition, start);

                if(week == 99)
                    continue;

                users.get(user_id).addSession(week, new Session(user_id, start, end, duration));

            }
        }

        csvReader.close();
    }

    private static void readForumSessions(String course, String filename) throws IOException, ParseException {
        //session_id, course_user_id, ??, start_time, end_time, duration
        CSVReader csvReader = new CSVReader(new FileReader(filename));
        String[] nextLine, session_attr;
        int duration, week;
        String user_id, start, end;

        nextLine = csvReader.readNext();

        while ((nextLine = csvReader.readNext()) != null) {
            user_id = nextLine[1];
            start = nextLine[3];
            end = nextLine[4];
            duration = Integer.parseInt(nextLine[5]);

            if(users.containsKey(user_id)) {
                week = Utils.getWeek(course, start);

                if(week == 99)
                    continue;

                users.get(user_id).addForumSession(week, new ForumSession(user_id, start, end, duration));

            }
        }

        csvReader.close();
    }

    private static void readProblems(String filename) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(filename));
        String [] nextLine;
        int week;
        String problemId;

        nextLine = csvReader.readNext();

        while ((nextLine = csvReader.readNext()) != null) {
            problemId = nextLine[0];
            week = Integer.parseInt(nextLine[1]);
            problems.put(problemId, week);
        }

        csvReader.close();

        System.out.println("Problems read: " + problems.size());
    }

    private static void readSubmissions(String course, String filename) throws IOException, ParseException {
        CSVReader csvReader = new CSVReader(new FileReader(filename));
        String [] nextLine;
        UserMetricComputation user;
        int week;
        String user_id, problem_id, timestamp, deadline;
        int sub = 0;

        nextLine = csvReader.readNext();

        while ((nextLine = csvReader.readNext()) != null) {
            user_id = nextLine[1];
            problem_id = nextLine[2];
            timestamp = Utils.formatTimestamp(nextLine[3]);

            user = users.get(user_id);
            if (user == null)    //user are not in the active base -> ignore submission
                continue;

            if(!problems.containsKey(problem_id))   //ignore problems that are not graded
                continue;

            week = Utils.getWeek(course, timestamp);
            if(week > 10)
                continue;

            deadline = Utils.getProblemDeadline(course, problems.get(problem_id));
            user.addSubmission(week, new Submission(user_id, problem_id, timestamp, deadline));

            sub++;
        }

        csvReader.close();

        System.out.println("Submissions read: " + sub);
    }

    private static void readObservations(String course, String filename) throws IOException, ParseException {
        CSVReader csvReader = new CSVReader(new FileReader(filename));
        String [] nextLine;
        UserMetricComputation user;
        int week, duration;
        String user_id, video_id, start, end;
        int sub = 0;

        nextLine = csvReader.readNext();

        while ((nextLine = csvReader.readNext()) != null) {
            user_id = nextLine[1];

            user = users.get(user_id);
            if (user == null)    //user are not in the active set -> ignore observation
                continue;

            video_id = nextLine[2];
            start = nextLine[3];
            end = nextLine[4];
            duration = Integer.parseInt(nextLine[5]);

            week = Utils.getWeek(course, start);
            if(week > 10)
                continue;

            //String problemId, int submissionWeek, Date submissionTime, Date problemDeadline
            user.addVideoSession(week, new VideoSession(user_id, video_id, start, end, duration));

            sub++;
        }

        csvReader.close();

        System.out.println("Observations read: " + sub);
    }

    private static void readMaximums(String filename, int week) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(filename));
        String [] nextLine;
        int line = 1;

        csvReader.readNext();

        while ((nextLine = csvReader.readNext()) != null && line < week) {
            line++;
        }

        for(int i = 1; i <= metricMethods.size(); i++)
            maximums.put(i, Integer.parseInt(nextLine[i]));

        csvReader.close();
    }

    //WRITE
    private static void writeMetrics(HashMap<String, UserMetricComputation> group, int week, String metricsPath, String filename) throws IOException {

        Utils.checkForDirectory(metricsPath);

        CSVWriter output = new CSVWriter(new FileWriter(filename), ',');
        String[] toWrite;
        UserMetricComputation current;

        toWrite = "User_id#Sessions/week#Length of session#Between sessions#Forum sessions#Assignments#Until deadline#Sessions#Videos#Time on platform".split("#");

        output.writeNext(toWrite);

        for (Map.Entry<String, UserMetricComputation> entry : group.entrySet()) {
            current = entry.getValue();
            toWrite[0] = entry.getKey();

            for(int i = 1; i <= metricMethods.size(); i++)
                toWrite[i] = String.valueOf(metricMethods.get(i).apply(current, week));

            output.writeNext(toWrite);
        }
        output.close();
    }

    private static void writeScaledMetrics(HashMap<String, UserMetricComputation> group, int week, String metricsPath, String filename) throws IOException {
        Utils.checkForDirectory(metricsPath);

        CSVWriter output = new CSVWriter(new FileWriter(filename), ',');
        String[] toWrite;
        UserMetricComputation current;

        toWrite = "User_id#Sessions/week#Length of session#Between sessions#Forum sessions#Assignments#Until deadline#Sessions#Videos#Time on platform".split("#");

        output.writeNext(toWrite);

        for (Map.Entry<String, UserMetricComputation> entry : group.entrySet()) {
            current = entry.getValue();
            toWrite[0] = entry.getKey();

            for(int i = 1; i <= metricMethods.size(); i++) {
                toWrite[i] = String.format("%.1f", ScalingComputation.scaleMetricValue(metricMethods.get(i).apply(current, week), maximums.get(i)));

            }

            output.writeNext(toWrite);
        }
        output.close();
    }

    private static void writeMaximums(int week, String thresholdsPpath, String filename, int cutOffPercent) throws IOException {
        Utils.checkForDirectory(thresholdsPpath);

        CSVWriter output = new CSVWriter(new FileWriter(thresholdsPpath + filename), ',');
        String[] toWrite;

        toWrite = "#Sessions/week#Length of session#Between sessions#Forum sessions#Assignments#Until deadline#Sessions#Videos#Time on platform".split("#");

        output.writeNext(toWrite);

        for(int i = 1; i <= week; i++) {
            toWrite[0] = "Week " + i;

            for(int j = 1; j <= metricMethods.size(); j++)
                toWrite[j] = String.valueOf(AverageGraduateComputation.getMaximumInCutOffRange(users, metricMethods.get(j), i, cutOffPercent));

            output.writeNext(toWrite);
        }

        output.close();
    }

    private static void writeThresholds(int week, String thresholdsPath, String filename, int cutOffPercent) throws IOException {
        Utils.checkForDirectory(thresholdsPath);

        CSVWriter output = new CSVWriter(new FileWriter(thresholdsPath + filename), ',');
        String[] toWrite;

        toWrite = "#Sessions/week#Length of session#Between sessions#Forum sessions#Assignments#Until deadline#Sessions#Videos#Time on platform".split("#");

        output.writeNext(toWrite);

        for(int i = 1; i <= week; i++) {
            toWrite[0] = "Week " + i;

            for(int j = 1; j <= metricMethods.size() ; j++)
                toWrite[j] = String.valueOf(AverageGraduateComputation.getAverage(users, metricMethods.get(j), i, cutOffPercent));

            output.writeNext(toWrite);
        }

        output.close();
    }

    private static void writeScaledThresholds(int week, String thresholdPaths, String filename, int cutOffPercent) throws IOException {
        Utils.checkForDirectory(thresholdPaths);

        CSVWriter output = new CSVWriter(new FileWriter(thresholdPaths + filename), ',');
        String[] toWrite;

        toWrite = "#Sessions/week#Length of session#Between sessions#Forum sessions#Assignments#Until deadline#Sessions#Videos#Time on platform".split("#");

        output.writeNext(toWrite);

        for(int i = 1; i <= week; i++) {
            toWrite[0] = "Week " + i;

            for(int j = 1; j <= metricMethods.size() ; j++)
                toWrite[j] = String.format("%.1f", ScalingComputation.getScaledThresholdValue(users, metricMethods.get(j), i, cutOffPercent));

            output.writeNext(toWrite);
        }

        output.close();
    }



    private static void writeGraduates(HashMap<String, UserMetricComputation> users, int week, String filename) throws IOException {
        CSVWriter output = new CSVWriter(new FileWriter(filename), ',');
        String[] toWrite;
        UserMetricComputation current;

        toWrite = "User_id#Sessions/week#Length of session#Between sessions#Forum sessions#Assignments#Until deadline#Sessions#Videos".split("#");

        output.writeNext(toWrite);

        for (Map.Entry<String, UserMetricComputation> entry : users.entrySet()) {
            if(!graduates.contains(entry.getKey()))
                continue;

            current = entry.getValue();
            toWrite[0] = entry.getKey();
            //toWrite[1] = String.valueOf(current.getSessionsPerWeek(week));
            //toWrite[2] = String.valueOf(current.getAverageSessionLength(week));
            //toWrite[3] = String.valueOf(current.getAverageTimeBetweenSessions(week));
            toWrite[4] = String.valueOf(current.getForumSessions(week));
            //toWrite[5] = String.valueOf(current.getQuizSubmissions(week));
            //toWrite[6] = String.valueOf(current.getRecommendedTimeliness(week));
            //toWrite[7] = String.valueOf(current.getSessions(week));
            toWrite[8] = String.valueOf(current.getVideosAccessed(week));

            output.writeNext(toWrite);
        }
        output.close();
    }

    private static void writeNonGraduates(HashMap<String, UserMetricComputation> users, int week, String filename) throws IOException {
        CSVWriter output = new CSVWriter(new FileWriter(filename), ',');
        String[] toWrite;
        UserMetricComputation current;

        toWrite = "User_id#Sessions/week#Length of session#Between sessions#Forum sessions#Assignments#Until deadline#Sessions#Videos".split("#");

        output.writeNext(toWrite);

        for (Map.Entry<String, UserMetricComputation> entry : users.entrySet()) {
            if(graduates.contains(entry.getKey()))
                continue;

            current = entry.getValue();
            toWrite[0] = entry.getKey();
            //toWrite[1] = String.valueOf(current.getSessionsPerWeek(week));
            //toWrite[2] = String.valueOf(current.getAverageSessionLength(week));
            //toWrite[3] = String.valueOf(current.getAverageTimeBetweenSessions(week));
            toWrite[4] = String.valueOf(current.getForumSessions(week));
            //toWrite[5] = String.valueOf(current.getQuizSubmissions(week));
            //toWrite[6] = String.valueOf(current.getRecommendedTimeliness(week));
            //toWrite[7] = String.valueOf(current.getSessions(week));
            toWrite[8] = String.valueOf(current.getVideosAccessed(week));

            output.writeNext(toWrite);
        }
        output.close();
    }

}

class AverageGraduateComputation {
    public static List<Integer> listMetricValues(HashMap<String, UserMetricComputation> users, BiFunction<UserMetricComputation, Integer, Integer> method, int week){

        return users.entrySet().stream()
                .map(e -> method.apply(e.getValue(), week))
                .collect(Collectors.toList());

    }

    public static double getAverage(HashMap<String, UserMetricComputation> users, BiFunction<UserMetricComputation, Integer, Integer> method, int week, int cutOffPercent) {
        List<Integer> allMetricValues = listMetricValues(users, method, week);

        double average;

        average = Math.round(getCutOffRange(allMetricValues, cutOffPercent)
                .stream()
                .mapToInt(e -> e)
                .average()
                .getAsDouble());

        if(Double.isNaN(average))
            return 0;

        return average;
    }

    private static int getMaximum(List<Integer> integers) {
        return integers.stream().max(Comparator.naturalOrder()).get();
    }

    private static int getMinimum(List<Integer> integers) {
        return integers.stream().min(Comparator.naturalOrder()).get();
    }

    public static List<Integer> getCutOffRange(List<Integer> values, int cutOffPercent) {
        int min, max, cutOffMin, cutOffMax;

        min = getMinimum(values);
        max = getMaximum(values);

        cutOffMin = min + (max - min) * cutOffPercent / 100;
        cutOffMax = max - (max - min) * cutOffPercent / 100;

        return values.stream()
                .filter(e -> e >= cutOffMin)
                .filter(e -> e <= cutOffMax)
                .collect(Collectors.toList());
    }

    public static int getMaximumInCutOffRange(HashMap<String, UserMetricComputation> users,
                                 BiFunction<UserMetricComputation, Integer, Integer> method, int week, int cutOffPercent) {

        List<Integer> metricValues = listMetricValues(users, method, week);
        return getMaximum(getCutOffRange(metricValues, cutOffPercent));
    }
}

class ScalingComputation {

    public static double getScaledThresholdValue(HashMap<String, UserMetricComputation> users, BiFunction<UserMetricComputation, Integer, Integer> method, int week, int cutOffPercent) {

        double average = AverageGraduateComputation.getAverage(users, method, week, cutOffPercent);
        double max = AverageGraduateComputation.getMaximumInCutOffRange(users, method, week, cutOffPercent);

        if(max == 0)
            return 0;

        return average * 10 / max;
    }

    public static double scaleMetricValue(int value, int maximum) {
        if(maximum == 0)
            return 0;

        if(value > maximum)
            return 10;

        return value * 10.0 / maximum;
    }

}