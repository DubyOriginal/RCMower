package hr.duby.rcmower.network;

import org.json.JSONObject;

/**
 * Created by sobadic on 18.02.16..
 */
public interface AsyncHttpDownloadListener {

    public void onDownloadDone(String filePath);

    public void onDownloadDone(JSONObject object);

    public void onDownloadProgress(int progress);

    public void onError(Exception e);
}
