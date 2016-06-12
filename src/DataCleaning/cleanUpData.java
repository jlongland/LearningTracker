package DataCleaning;

//select for analysis only the active users from both groups
//Active - >5 mins on the platform

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Ioana on 5/25/2016.
 */
public class cleanUpData {


    public static void main(String[] args) throws IOException {


        //extractDate("data\\sessions.csv", "data\\sessions_week1.csv");
        //extractDateObservations("data\\0. old MOOC\\data\\dw\\observations.csv", "data\\0. old MOOC\\data\\dw\\observations_new.csv");

        //extractField("data\\old_MOOC\\data\\ri\\forum_sessions.csv", "data\\old_MOOC\\data\\ri\\forum_sessions_new.csv", 1, 1);

        //ST
        //sessions.csv
        //addField("data\\old_MOOC\\data\\st\\sessions.csv", "data\\old_MOOC\\data\\st\\sessions_new.csv", 0, 3);
        //addField("data\\old_MOOC\\data\\st\\sessions_new.csv", "data\\old_MOOC\\data\\st\\sessions_new_new.csv", 0, 4);

        //quiz_session.csv
        //extractField("data\\old_MOOC\\data\\st\\quiz_sessions.csv", "data\\old_MOOC\\data\\st\\quiz_sessions_new.csv", 1, 1);

        //observation.csv
        //extractField("data\\old_MOOC\\data\\st\\observations.csv", "data\\old_MOOC\\data\\st\\observations_n.csv", 1, 1);

        //submissions.csv
        //extractField("data\\old_MOOC\\data\\st\\submissions.csv", "data\\old_MOOC\\data\\st\\submissions_n.csv", 1, 1);
        //filterField("data\\old_MOOC\\st\\data\\submissions.csv", "data\\old_MOOC\\st\\data\\submissions_n.csv", 3, "problem_graded");

        //DWT
        //addField("data\\old_MOOC\\dw\\data\\sessions.csv", "data\\old_MOOC\\dw\\data\\sessions_new.csv", 2, 3);
        //addField("data\\old_MOOC\\dw\\data\\sessions_new.csv", "data\\old_MOOC\\dw\\data\\sessions_new_new.csv", 2, 4);

        //extractField("data\\old_MOOC\\dw\\data\\observations.csv", "data\\old_MOOC\\dw\\data\\observations_n.csv", 1, 1);
        //extractField("data\\old_MOOC\\dw\\data\\submissions.csv", "data\\old_MOOC\\dw\\data\\submissions_n.csv", 1, 1);

        int week = 8;
        cleanST(week);

    }

    private static void cleanST(int week) throws IOException{
        //sessions.csv
        addField("data\\old_MOOC\\st\\week" + week + "\\data\\sessions.csv", "data\\old_MOOC\\st\\week" + week + "\\data\\sessions_s.csv", 0, 3);
        addField("data\\old_MOOC\\st\\week" + week + "\\data\\sessions_s.csv", "data\\old_MOOC\\st\\week" + week + "\\data\\sessions_ss.csv", 0, 4);
        extractField("data\\old_MOOC\\st\\week" + week + "\\data\\sessions_ss.csv", "data\\old_MOOC\\st\\week" + week + "\\data\\sessions.csv", 1, 1);

        //quiz_session.csv
        extractField("data\\old_MOOC\\st\\week" + week + "\\data\\quiz_sessions.csv", "data\\old_MOOC\\st\\week" + week + "\\data\\quiz_sessions_s.csv", 1, 1);

        //forum_session.csv
        extractField("data\\old_MOOC\\st\\week" + week + "\\data\\forum_sessions.csv", "data\\old_MOOC\\st\\week" + week + "\\data\\forum_sessions_s.csv", 1, 1);

        //observation.csv
        extractField("data\\old_MOOC\\st\\week" + week + "\\data\\observations.csv", "data\\old_MOOC\\st\\week" + week + "\\data\\observations_s.csv", 1, 1);

        //submissions.csv
        extractField("data\\old_MOOC\\st\\week" + week + "\\data\\submissions.csv", "data\\old_MOOC\\st\\week" + week + "\\data\\submissions_s.csv", 1, 1);
        filterField("data\\old_MOOC\\st\\week" + week + "\\data\\submissions_s.csv", "data\\old_MOOC\\st\\week" + week + "\\data\\submissions.csv", 3, "problem_graded");
    }

    private static void extractDate(String input, String output) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(input));
        CSVWriter csvWriter = new CSVWriter(new FileWriter(output));
        String [] nextLine, toWrite = new String[4];

        while ((nextLine = csvReader.readNext()) != null) {
            for(int i = 0; i < nextLine.length; i++)
                toWrite[i] = nextLine[i];

            toWrite[nextLine.length] = nextLine[0].split("_")[3];

            csvWriter.writeNext(toWrite);
        }

        csvReader.close();
        csvWriter.close();
    }

    private static void addField(String input, String output, int csv_field, int string_field) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(input));
        CSVWriter csvWriter = new CSVWriter(new FileWriter(output));
        String [] nextLine, toWrite;

        nextLine = csvReader.readNext();
        toWrite = new String[nextLine.length + 1];

        for(int i = 0; i < nextLine.length; i++)
            toWrite[i] = nextLine[i];
        csvWriter.writeNext(toWrite);

        while ((nextLine = csvReader.readNext()) != null) {
            for(int i = 0; i < nextLine.length; i++)
                toWrite[i] = nextLine[i];

            toWrite[nextLine.length] = nextLine[csv_field].split("_")[string_field];
            csvWriter.writeNext(toWrite);
        }

        csvReader.close();
        csvWriter.close();
    }

    //input file, output file, the field in the CSV files that needs to be extracted, the field (separated by '_') in the string that needs to be extracted
    private static void extractField(String input, String output, int csv_field, int string_field) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(input));
        CSVWriter csvWriter = new CSVWriter(new FileWriter(output));
        String [] nextLine;

        nextLine = csvReader.readNext();
        csvWriter.writeNext(nextLine);

        while ((nextLine = csvReader.readNext()) != null) {
            nextLine[csv_field] = nextLine[csv_field].split("_")[string_field];
            csvWriter.writeNext(nextLine);
        }

        csvReader.close();
        csvWriter.close();

    }

    private static void filterField(String input, String output, int csv_field, String field_value) throws IOException {
        CSVReader csvReader = new CSVReader(new FileReader(input));
        CSVWriter csvWriter = new CSVWriter(new FileWriter(output));
        String [] nextLine;

        nextLine = csvReader.readNext();
        csvWriter.writeNext(nextLine);

        while ((nextLine = csvReader.readNext()) != null) {
            if(nextLine[csv_field].compareTo(field_value) != 0)
                continue;

            csvWriter.writeNext(nextLine);
        }

        csvReader.close();
        csvWriter.close();

    }

}
