package hr.duby.rcmower.network;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by sobadic on 18.02.16..
 */
public class AsyncHttpClient extends AsyncTask<String, Void, String> {

    private static final int BUFFER_SIZE = 4096;

    private boolean isPost;
    private boolean isDownload;
    private boolean isDownloadError;
    private Exception e;

    private AsyncHttpListener listener;
    private AsyncHttpDownloadListener downloadListener;

    private boolean isCanceled;

    public AsyncHttpClient() {
        super();
        trustAllCertificates();
    }

    public void get(String url, AsyncHttpListener listener) {
        this.listener = listener;
        this.downloadListener = null;
        this.isDownloadError = false;

        String[] params = {url, "GET", "", ""};
        execute(params);
    }

    public void post(String url, JSONObject json, AsyncHttpListener listener) {
        this.listener = listener;
        this.downloadListener = null;
        this.isDownloadError = false;

        String parameters = "";
        if (json != null) {
            parameters = json.toString();
        }

        String[] params = {url, "POST", "application/json;charset=utf-8", parameters};
        execute(params);
    }

    public void download(String url, File f, AsyncHttpDownloadListener downloadListener) {
        this.downloadListener = downloadListener;
        this.listener = null;
        this.isDownloadError = false;

        String[] params = {url, "DOWNLOAD", "", f.getAbsolutePath()};
        execute(params);
    }

    @Override
    protected String doInBackground(String... params) {
        String strUrl = params[0];
        String method = params[1];
        String contentType = params[2];
        String parameters = params[3];

        String content = "";
        isCanceled = false;
        try {
            URL url = new URL(strUrl);

            HttpURLConnection connection;
            if ("POST".equalsIgnoreCase(method)) {
                isPost = true;
                isDownload = false;

                ByteBuffer data = Charset.forName("UTF-8").encode(parameters);
                byte[] buffer = data.array();
                int size = data.limit();
                byte[] utf8Bytes = new byte[size];
                System.arraycopy(buffer, 0, utf8Bytes, 0, size);
                connection = doPost(url, contentType, size);
                DataOutputStream wr = new DataOutputStream (connection.getOutputStream());
                wr.write(utf8Bytes, 0, size);
                wr.close();

                return read(connection);
            } else if ("GET".equalsIgnoreCase(method)) {
                isPost = false;
                isDownload = false;

                connection = doGet(url);
                return read(connection);
            } else {
                isPost = false;
                isDownload = true;

                connection = doGet(url);
                ByteArrayOutputStream stream = prereadFileResponse(connection);
                byte[] data = stream.toByteArray();

                try {
                    String strError = new String(data, "UTF-8");

                    if ("".equals(strError)) {
                        isDownloadError = true;
                    } else {
                        new JSONObject(strError); // Probamo parsati kao JSON
                        isDownloadError = true;
                    }

                    return strError;
                } catch(Exception e) {
                    //ako ne prolazi parsiranje JSONa onda pretpostavka da se radi o pdf-u
                    return writeFile(data, parameters);
                }
            }
        } catch (Exception e) {
            this.e = e;
            Log.e("MowerE", "AsyncHttpClient: Error executing request", e);
            Log.e("DTag", "AsyncHttpClient: Error executing request");
        }

        return content;
    }

    private String read(URLConnection connection) throws Exception {
        // get Response
        InputStream is = connection.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder();

        String line;
        while((line = rd.readLine()) != null) {
            response.append(line);
        }
        rd.close();

        return response.toString();
    }

    private ByteArrayOutputStream prereadFileResponse(HttpURLConnection connection) throws Exception {
        // opens an output stream to save into file
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = connection.getHeaderField("Content-Disposition");
            String contentType = connection.getContentType();
            int contentLength = connection.getContentLength();

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = connection.getInputStream();

            int totalBytesRead = 0;
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                if (downloadListener != null && contentLength > 0) {
                    int progress = (totalBytesRead * 100 / contentLength);
                    downloadListener.onDownloadProgress(progress);
                }
            }

            outputStream.close();
            inputStream.close();

            System.out.println("Stream read");
        } else {
            Log.e("DTag", "AsyncHttpClient: No file to download. Server replied HTTP code: " + responseCode);
        }

        connection.disconnect();

        return outputStream;
    }

    public void cancel() {
       isCanceled = true;
    }

    /*private String readFile(HttpURLConnection connection, String saveFilePath) throws Exception {
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            String fileName = "";
            String disposition = connection.getHeaderField("Content-Disposition");
            String contentType = connection.getContentType();
            int contentLength = connection.getContentLength();

            System.out.println("Content-Type = " + contentType);
            System.out.println("Content-Disposition = " + disposition);
            System.out.println("Content-Length = " + contentLength);
            System.out.println("fileName = " + fileName);

            // opens input stream from the HTTP connection
            InputStream inputStream = connection.getInputStream();

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int totalBytesRead = 0;
            int bytesRead = -1;

            byte[] buffer = new byte[BUFFER_SIZE];

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;

                if (downloadListener != null && contentLength > 0) {
                    int progress = (totalBytesRead * 100 / contentLength);
                    downloadListener.onDownloadProgress(progress);
                }
            }

            outputStream.close();
            inputStream.close();

            System.out.println("File downloaded");
        } else {
            Log.d(TAG, "No file to download. Server replied HTTP code: " + responseCode);
        }

        connection.disconnect();

        return saveFilePath;
    }*/

    private String writeFile(byte[] data, String saveFilePath) throws Exception {
        // opens an output stream to save into file
        FileOutputStream outputStream = new FileOutputStream(saveFilePath);
        outputStream.write(data);
        outputStream.close();

        return saveFilePath;
    }

    private HttpURLConnection doGet(URL url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        //Log.d("DTag", "connection.getReadTimeout -> ");
        return connection;
    }

    private HttpURLConnection doPost(URL url, String contentType, int contentLength) throws Exception {
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", contentType);
        connection.setRequestProperty("Content-Length", String.valueOf(contentLength));

        //connection.setRequestProperty("Content-Language", "en-US");

        connection.setUseCaches(false);
        connection.setDoOutput(true);

        return connection;
    }

    @Override
    public void onPostExecute(String result) {
        if (!isCanceled) {
            try {
                if (e != null) {
                    if (listener != null) {
                        listener.onError(e);
                    }
                    if (downloadListener != null) {
                        downloadListener.onError(e);
                    }
                } else {
                    if (result != null) {
                        result = result.trim();
                    }

                    if (isDownload) {
                        if (isDownloadError) {
                            if (downloadListener != null) {
                                downloadListener.onDownloadDone(getDownloadError(result));
                            }
                        } else {
                            if (downloadListener != null) {
                                downloadListener.onDownloadDone(result);
                            }
                        }
                    } else {
                        if (listener != null) {
                            if (!isPost && listener instanceof AsyncHttpListenerArray) {
                                // Pretpostavljamo da bude tu uvijek zavrsaval get (zasad)
                                JSONArray array = null;

                                try {
                                    array = new JSONArray(result);
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }

                                ((AsyncHttpListenerArray)listener).onGetArrayDone(array);
                            } else {
                                JSONObject object = null;

                                try {
                                    object = new JSONObject(result);
                                } catch (JSONException e1) {
                                    e1.printStackTrace();
                                }

                                if (isPost) {
                                    listener.onPostDone(object);
                                } else {
                                    listener.onGetDone(object);
                                }
                            }
                        }
                    }
                }
            } finally {
                isPost = false;
                isDownload = false;
                this.e = null;
            }
        }
    }

    private JSONObject getDownloadError(String strError) {
        try {
            return new JSONObject(strError);
        } catch (Exception e) {
            Log.d("DTag", "AsyncHttpClient: Error parsing error message", e);
        }
        return null;
    }

    private void trustAllCertificates() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                            return myTrustedAnchors;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {}

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception e) {
        }
    }

}
