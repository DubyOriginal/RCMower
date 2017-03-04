package hr.duby.rcmower.network.http;

import org.json.JSONObject;

/**
 * Created by sobadic on 18.02.16..
 */
public interface AsyncHttpListener {

    public void onGetDone(JSONObject object);

    public void onPostDone(JSONObject object);

    public void onError(Exception e);
}
