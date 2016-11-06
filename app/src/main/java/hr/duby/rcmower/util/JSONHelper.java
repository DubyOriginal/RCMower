package hr.duby.rcmower.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;


/**
 * Created by Duby on 17.2.2016..
 */
public class JSONHelper {

    //*************************************************************************************************************************************************
    public static String getJSONValue(JSONObject object, String name) {
        if (object != null) {
            try {
                return object.getString(name);
            } catch (JSONException e) {}
        }
        return null;
    }

    //************************************************************************************************************
    public static JSONObject setJSONValue(String name, String value) {
        JSONObject object = new JSONObject();
        try {
            object.put(name, value);
        } catch (JSONException e) {}
        return object;
    }

    //************************************************************************************************************
    public static JSONObject getJSONObject(JSONObject object, String name) {
        if (object != null) {
            try {
                return object.getJSONObject(name);
            } catch (JSONException e) { }
        }
        return null;
    }

    //************************************************************************************************************
    public static JSONArray getJSONArray(JSONObject object, String name) {
        if (object != null) {
            try {
                return object.getJSONArray(name);

            } catch (JSONException e) { }
        }
        return null;
    }


    //CONVERT: ["Dragan Zarić", "Pavle Vuisić", "Miodrag Petrović Čkalja", "Radmila Živković", "Jovan Janićijević Burduš", "Slavka Jerinić"]
    //TO -> Dragan Zarić, Pavle Vuisić, Miodrag Petrović Čkalja, Radmila Živković, Jovan Janićijević Burduš, Slavka Jerinić
    //**********************************************************************************************
    public static String getCSVStringFromJSONArray(JSONArray jsonArray) {
        String resultStr = "";
        if (jsonArray != null && jsonArray.length() > 0) {
            for (int i=0; i<jsonArray.length();i++){
                try {
                    resultStr += jsonArray.get(i).toString();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (i < jsonArray.length()-1){
                    resultStr += ", ";
                }
            }
        }
        return resultStr;
    }

    //USAGE: -> JSONObject object = JSONHelper.loadJSONObjectFromAsset();
    // assets dir: /Android/app/src/main/assets/listHomeItem.json
    //*************************************************************************************************************************************************
    public static JSONObject loadJSONObjectFromAsset(Context context) {
        String jsonStr = null;
        JSONObject jsonObject = null;
        try {
            //here we need context to access ASSETS --> ANDROID TUTORIAL / codeBlock - Patterns / Application.java
            InputStream is = context.getAssets().open("listHomeItem.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonStr = new String(buffer, "UTF-8");

            jsonObject = new JSONObject(jsonStr);

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return jsonObject;
    }

}
