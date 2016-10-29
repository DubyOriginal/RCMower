package hr.duby.rcmower.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import hr.duby.rcmower.Const;
import hr.duby.rcmower.R;


/**
 * Created by dvrbancic on 19/08/16.
 */
public class BasicUtils {


    //****************************************************************************************************************************************************
    public static void saveSharedPreferences(Context context, String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    //****************************************************************************************************************************************************
    public static String loadLanguageFromSharedPrefs(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString("LANGUAGE", "-");  //default (if not setted yet)
    }

    //****************************************************************************************************************************************************
    public static JSONObject loadJsonObjectFromSharedPrefs(Context context, String JsonKey) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        JSONObject jsonData = null;
        String strJson = sharedPreferences.getString(JsonKey, "");
        if (strJson != null && !strJson.isEmpty()) {
            try {
                return jsonData = new JSONObject(strJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //**************************************************************************************************************************************************************
    //**************************************************************************************************************************************************************

    //**********************************************************************************************
    public static void setLOGOUTFLAGToSharedPrefs(Context context, boolean isLOGEDDOUT) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("LOGOUT_FLAG", isLOGEDDOUT);
        editor.commit();
    }

    //****************************************************************************************************************************************************
    public static boolean getLOGOUTFLAGFromSharedPrefs(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("LOGOUT_FLAG", true);
    }

    //usage: boolean isServiceRunning = BasicUtils.isServiceRunning(SongService.class.getName(), getApplicationContext());
    //**********************************************************************************************
    public static boolean isServiceRunning(String serviceName, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(serviceName.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //SHARED PREFERENCES
    //*****************************************************************************************************************************************
    //**********************************************************************************************
    public static void saveStationListToSharedPreference(Context context, JSONArray jsonData){
        if (jsonData != null && jsonData.length() > 0){
            SharedPreferences sharePreferences = PreferenceManager.getDefaultSharedPreferences(context);
            sharePreferences.edit().putString("StationList", jsonData.toString()).apply();
        }
    }

    //**********************************************************************************************
    public static JSONArray loadStationListFromSharedPreference(Context context){
        SharedPreferences  sharePreferences =  PreferenceManager.getDefaultSharedPreferences(context);
        try {
            String loadedJSON = sharePreferences.getString("StationList", null);

            if(loadedJSON == null)
                return null;

            JSONArray jsonArray = new JSONArray(loadedJSON);
            return  jsonArray;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    //**********************************************************************************************
    public static void setVALUEToSharedPrefs(Context context, String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    //****************************************************************************************************************************************************
    public static String getVALUEFromSharedPrefs(Context context, String key) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, "");
    }

    //****************************************************************************************************************************************************
    public static String getVALUEFromSharedPrefs(Context context, String key, String defValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getString(key, defValue);
    }


    //****************************************************************************************************************************************************
    /*
    public static void setCurrentTimestampToSharedPrefs(Context context) {
        String timestampValue = BasicUtils.getFormatedTimestamp();
        setVALUEToSharedPrefs(context, Const.KEY_SERVER_UPDATE_DATA_TIME, timestampValue);

    }*/

    //****************************************************************************************************************************************************
    public static String getFormatedTimestamp() {
        final Date currentTime = new Date();
        final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(currentTime);
    }

    //****************************************************************************************************************************************************
    public static boolean isDataToOld(String lastTimestamp) {
        if (lastTimestamp == null || lastTimestamp.length() == 0){
            Log.d("DTag", "BasicUtils.isDataToOld() -> invalid input lastTimestamp: " + lastTimestamp);
            return true;
        }

        //lastTimestamp = "24.08.2016 08:40:13";

        Date lastTimestampDate = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        try {
            lastTimestampDate = sdf.parse(lastTimestamp);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //compare lastTimestampDate to currentTimestamp
        Date dateNow = new Date();
        long diff = dateNow.getTime() - lastTimestampDate.getTime();
        long diffInMinutes = (long)(diff / (60000l));

        if (diffInMinutes >= 0 && diffInMinutes > 15l){
            return true;
        }else{
            return false;
        }
    }


    //**********************************************************************************************
    private JSONArray generateDummyJSONArray(){
        JSONArray jsonArray = new JSONArray();

        JSONObject object1 = new JSONObject();
        try {
            object1.put("id", 1);
            object1.put("name", "Scooter");
            object1.put("description", "album 2");
            object1.put("featured", 1);
            object1.put("slider", 0);
            object1.put("background_hash", "9a5798e40887a7dd5fb542fb95691d7d5d7217b9");
            object1.put("tracks_counter", 7);
            object1.put("total_duration", 1719014);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("DTag", "JSONException @@@@@@@@@@@@@@@");
        }
        jsonArray.put(object1);

        return jsonArray;
    }



    //USAGE:
    // CALL AS -> new BasicUtils().showAlertMessage(context, Const.MSG_TYPE_ERROR, (context != null ? context.getResources().getString(R.string.err_msg_failedToLoadData) : ""));
    // CONST: int: MSG_TYPE_ERROR = 8001 / MSG_TYPE_WARNING = 8002 / MSG_TYPE_INFO = 8003
    // STRNGS:
    //<string name="dialog_title_warning">Upozorenje!</string>
    //<string name="dialog_title_error">Pogreška!</string>
    //<string name="dialog_title_info">Info!</string>
    //**********************************************************************************************
    public void showAlertMessage(Context context, int msgTitle, String message) {
        Log.d("DTag", "context: " + context.getClass().getSimpleName());

        //Crashlytics BUG-FIX - (line 338) Unable to add window, is your activity running?
        if (context != null && ((context instanceof Activity)) && !((Activity) context).isFinishing()){
            final AlertDialog.Builder dlgAlert = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
            String msgTitleStr = context.getString(R.string.dialog_title_error);
            if (msgTitle == Const.MSG_TYPE_WARNING){
                msgTitleStr = context.getString(R.string.dialog_title_warning);
            }else if (msgTitle == Const.MSG_TYPE_INFO){
                msgTitleStr = context.getString(R.string.dialog_title_info);
            }
            dlgAlert.setTitle(msgTitleStr);
            dlgAlert.setMessage(message);
            dlgAlert.setPositiveButton(context.getString(R.string.btn_OK), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int arg1) {
                    dialog.cancel();
                }
            });
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();
        }else{
            Log.d("DTag", "BasicUtils: context: " + context.getClass().getSimpleName() + ", showAlertMessage will not be shown!");
        }
    }
}
