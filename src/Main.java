import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Ioana on 6/8/2016.
 */
public class Main {

    public static void main(String[] args) throws ParseException, IOException {
        //MetricComputation.computeThreshold("st", 10, "data\\old_MOOC\\st\\", 5);
        //MetricComputation.computeThreshold("dw", 10, "data\\old_MOOC\\dw\\", 5);

        MetricComputation.computeMetrics("st", 8, "data\\old_MOOC\\st\\");
        //MetricComputation.computeMetrics("dw", 10, "data\\old_MOOC\\dw\\");


    }
}
