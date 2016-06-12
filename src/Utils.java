import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import MetricComputation.Edition;

/**
 * Created by Ioana on 6/8/2016.
 */
public class Utils {

    public static int getWeek(String course, Edition edition, String startTime) {
        if(edition == Edition.PREVIOUS)
            switch (course) {
                case "st": return ST_getWeekPrevious(startTime);
                case "ri": return RI_getWeekPrevious(startTime);
                case "dw": return DW_getWeekPrevious(startTime);
            }
        else
            switch (course) {
                case "st": return ST_getWeekPrevious(startTime);
                case "ri": return RI_getWeekPrevious(startTime);
                case "dw": return DW_getWeekPrevious(startTime);
            }

        return 0;
    }

    private static int ST_getWeekPrevious(String startTime) {
        if(startTime.compareTo("2015-01-27") > 0 && startTime.compareTo("2015-02-03") < 0)
            return 1;
        if(startTime.compareTo("2015-02-03") > 0 && startTime.compareTo("2015-02-10") < 0)
            return 2;
        if(startTime.compareTo("2015-02-10") > 0 && startTime.compareTo("2015-02-17") < 0)
            return 3;
        if(startTime.compareTo("2015-02-17") > 0 && startTime.compareTo("2015-02-24") < 0)
            return 4;
        if(startTime.compareTo("2015-02-24") > 0 && startTime.compareTo("2015-03-03") < 0)
            return 5;
        if(startTime.compareTo("2015-03-03") > 0 && startTime.compareTo("2015-03-10") < 0)
            return 6;
        if(startTime.compareTo("2015-03-10") > 0 && startTime.compareTo("2015-03-17") < 0)
            return 7;
        if(startTime.compareTo("2015-03-17") > 0 && startTime.compareTo("2015-03-24") < 0)
            return 8;
        if(startTime.compareTo("2015-03-24") > 0 && startTime.compareTo("2015-03-31") < 0)
            return 9;
        if(startTime.compareTo("2015-03-31") > 0 && startTime.compareTo("2015-04-08") < 0)
            return 10;
        return 99;
    }

    private static int RI_getWeekPrevious(String startTime) {
        if(startTime.compareTo("2014-11-25") > 0 && startTime.compareTo("2014-12-02") < 0)
            return 1;
        if(startTime.compareTo("2014-12-02") > 0 && startTime.compareTo("2014-12-09") < 0)
            return 2;
        if(startTime.compareTo("2014-12-09") > 0 && startTime.compareTo("2014-12-16") < 0)
            return 3;
        if(startTime.compareTo("2014-12-16") > 0 && startTime.compareTo("2014-12-23") < 0)
            return 4;
        if(startTime.compareTo("2014-12-23") > 0 && startTime.compareTo("2014-12-30") < 0)
            return 4;
        if(startTime.compareTo("2014-12-30") > 0 && startTime.compareTo("2015-01-06") < 0)
            return 4;
        if(startTime.compareTo("2015-01-06") > 0 && startTime.compareTo("2015-01-13") < 0)
            return 5;
        if(startTime.compareTo("2015-01-13") > 0 && startTime.compareTo("2015-01-20") < 0)
            return 6;
        if(startTime.compareTo("2015-01-20") > 0 && startTime.compareTo("2015-01-27") < 0)
            return 7;
        if(startTime.compareTo("2015-01-27") > 0 && startTime.compareTo("2015-02-03") < 0)
            return 8;
        if(startTime.compareTo("2015-02-03") > 0 && startTime.compareTo("2015-02-14") < 0)
            return 9;
        return 99;
    }

    private static int DW_getWeekPrevious(String startTime) {
        if(startTime.compareTo("2014-10-28") > 0 && startTime.compareTo("2014-11-04") < 0)
            return 1;
        if(startTime.compareTo("2014-11-04") > 0 && startTime.compareTo("2014-11-11") < 0)
            return 2;
        if(startTime.compareTo("2014-11-11") > 0 && startTime.compareTo("2014-11-18") < 0)
            return 3;
        if(startTime.compareTo("2014-11-18") > 0 && startTime.compareTo("2014-11-25") < 0)
            return 4;
        if(startTime.compareTo("2014-11-25") > 0 && startTime.compareTo("2014-12-02") < 0)
            return 5;
        if(startTime.compareTo("2014-12-02") > 0 && startTime.compareTo("2014-12-09") < 0)
            return 6;
        if(startTime.compareTo("2014-12-09") > 0 && startTime.compareTo("2014-12-16") < 0)
            return 7;
        if(startTime.compareTo("2014-12-16") > 0 && startTime.compareTo("2014-12-23") < 0)
            return 8;
        if(startTime.compareTo("2014-12-23") > 0 && startTime.compareTo("2014-12-30") < 0)
            return 9;
        if(startTime.compareTo("2014-12-30") > 0 && startTime.compareTo("2015-01-06") < 0)
            return 10;
        if(startTime.compareTo("2015-01-06") > 0 && startTime.compareTo("2015-01-13") < 0)
            return 11;
        return 99;
    }

    public static int getMaximumGradedAssignments(String course) {
        switch (course) {
            case "dw": return 25;
            case "st": return 35;
            case "ri": return 76;
        }
        return 0;
    }

    public static int getMaximumNonGradedAssignments(String course) {
        switch (course) {
            case "dw": return 63;
            case "st": return 261;
            case "ri": return 0;
        }
        return 0;
    }

    public static int getMaximumVideos(String course) {
        switch (course) {
            case "dw": return 58;
            case "st": return 81;
            case "ri": return 53;
        }
        return 0;
    }

    public static String getProblemDeadline(String course, int problemWeek) {
        switch (course) {
            case "st": return ST_getProblemDeadline(problemWeek);
            case "ri": return RI_getProblemDeadline(problemWeek);
            case "dw": return DW_getProblemDeadline(problemWeek);
        }

        return "";
    }

    private static String ST_getProblemDeadline(int problemWeek) {
        String deadline;

        switch (problemWeek) {
            case 2:
                deadline = "2015-02-17";
                break;
            case 3:
                deadline = "2015-02-24";
                break;
            case 4:
                deadline = "2015-03-03";
                break;
            case 5:
                deadline = "2015-03-10";
                break;
            case 6:
                deadline = "2015-03-17";
                break;
            case 7:
                deadline = "2015-03-24";
                break;
            default:
                deadline = "2015-04-07";
        }

        return deadline + " 12:00:00";
    }

    private static String RI_getProblemDeadline(int problemWeek) {
        String deadline;

        switch (problemWeek) {
            case 1:
                deadline = "2014-12-02";
                break;
            case 2:
                deadline = "2014-12-09";
                break;
            case 3:
                deadline = "2014-12-16";
                break;
            case 4:
                deadline = "2014-12-23";
                break;
            case 5:
                deadline = "2015-01-13";
                break;
            case 6:
                deadline = "2015-01-20";
                break;
            default:
                deadline = "2015-01-27";
        }

        return deadline + " 12:00:00";
    }

    private static String DW_getProblemDeadline(int problemWeek) {
        String deadline;

        switch (problemWeek) {
            case 1:
                deadline = "2014-11-10";
                break;
            case 2:
                deadline = "2014-11-17";
                break;
            case 3:
                deadline = "2014-11-24";
                break;
            case 4:
                deadline = "2014-12-01";
                break;
            case 5:
                deadline = "2014-12-08";
                break;
            case 6:
                deadline = "2014-12-15";
                break;
            case 7:
                deadline = "2014-12-22";
                break;
            case 8:
                deadline = "2014-12-29";
                break;
            default:
                deadline = "2015-01-13";
        }

        return deadline + " 23:59:59Z";
    }

    public static void checkForDirectory(String filepath) {
        File theDir = new File(filepath);

        // if the directory does not exist, create it
        if (!theDir.exists()) {
            System.out.println("Creating directory: " + filepath);
            boolean result = false;

            try{
                theDir.mkdir();
                result = true;
            }
            catch(SecurityException se){
                System.out.println(se);
            }
            if(result) {
                System.out.println("Directory created: " + filepath);
            }
        }
    }

    public static String formatTimestamp(String timestamp) {
        //2015-01-29T14:44:39.721793+00:00
        String date = timestamp.split("\\.")[0];
        return date.replace("T", " ");
    }

    private static Date getDateFromString(String dateString) {
        //input date: "2014-11-11 12:00:00"
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return format.parse(dateString);
        }
        catch (ParseException e) {
            System.out.println("Invalid date");
            return new Date();
        }
    }

    public static long differenceBetweenDatesInHours(String deadline, String submission){
        long diff = getDateFromString(deadline).getTime() - getDateFromString(submission).getTime();

        if(diff > 0)
            return TimeUnit.MILLISECONDS.toHours(diff);

        return 0;
    }


}


