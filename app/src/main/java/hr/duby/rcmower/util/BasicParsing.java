package hr.duby.rcmower.util;

import java.util.concurrent.TimeUnit;

/**
 * Created by dvrbancic on 22/08/16.
 */
public class BasicParsing {

    // 5454ms ->
    //**********************************************************************************************
    public static String parseMilliseconds(long dur_ms) {
        String result = null;

        StringBuilder time = new StringBuilder();
        long seconds = 0;
        long minutes = 0;
        long hours = 0;
        long days = 0;

        if (dur_ms > 0){
            if(TimeUnit.MILLISECONDS.toDays(dur_ms) > 0) {
                days = TimeUnit.MILLISECONDS.toDays(dur_ms);
                dur_ms -= TimeUnit.DAYS.toMillis(days);
                time.append(days+"d ");
            }

            if(TimeUnit.MILLISECONDS.toHours(dur_ms) > 0) {
                hours = TimeUnit.MILLISECONDS.toHours(dur_ms);
                dur_ms -= TimeUnit.HOURS.toMillis(hours);
                time.append(hours+"h ");
            }
            minutes = TimeUnit.MILLISECONDS.toMinutes(dur_ms);
            dur_ms -= TimeUnit.MINUTES.toMillis(minutes);
            time.append(minutes+"m ");

            seconds = TimeUnit.MILLISECONDS.toSeconds(dur_ms);
            time.append(seconds+"s");

        }else if (dur_ms == 0){
            time.append("0m 0s");
        }

        return time.toString();
    }


    //**********************************************************************************************
    public static String getResponseTimeForStartTime(long startTime) {
        long endTime = System.currentTimeMillis();
        long second = (long) Math.ceil((endTime - startTime) / 1000);
        long millis = endTime - startTime;
        String res_time = String.format("%d.%d sec", second, millis);
        return res_time;
    }


    //**********************************************************************************************
    public static String parseMillisecondsOldWay(long dur_ms) {
        String result = null;
        if (dur_ms > 0){
            int dSeconds = (int) (dur_ms / 1000) % 60;
            int dMinutes = (int) ((dur_ms / (1000 * 60)) % 60);
            int dHours   = (int) dur_ms / (60 * 60 * 1000);
            int dDays   = (int) dur_ms / (60 * 60 * 1000 * 24);

            if (dDays > 0){
                //@@@.T
                result = String.format("%02dd %02dh %02dm %02ds", dHours, dMinutes, dSeconds);
                //result = "-x-";
            }else{
                if (dHours > 0){
                    result = String.format("%02dh %02dm %02ds", dHours, dMinutes, dSeconds);
                }else{
                    result = String.format("%02dm %02ds", dMinutes, dSeconds);
                }
            }

        }else if (dur_ms == 0){
            result = "0m 00s";
        }
        return result;
    }


}
