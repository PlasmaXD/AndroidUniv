package com.example.alarm;

//https://tech-blog.rakus.co.jp/entry/android-studio/weather-hacks
import android.app.Activity;
import android.os.AsyncTask;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 非同期処理を行うクラス.
 */
public final class AsyncHttpRequest extends AsyncTask<URL, Void, String> {
    private int TODAY_FORCAST_INDEX = 0;
    private Activity mainActivity;



    public AsyncHttpRequest(Activity activity) {
        // 呼び出し元のアクティビティ
        this.mainActivity = activity;
    }

    /**
     * 非同期処理で天気情報を取得する.
     * @param urls 接続先のURL
     * @return 取得した天気情報
     */
    @Override
    protected String doInBackground(URL... urls) {//非同期で処理する内容

        final URL url = urls[0];
        HttpURLConnection con = null;

        try {
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");// リクエストメソッドの設定
            con.setInstanceFollowRedirects(false); // リダイレクトを自動で許可しない設定
            con.connect();// 接続

            final int statusCode = con.getResponseCode();//HTTP ステータスコード
            if (statusCode != HttpURLConnection.HTTP_OK) {//接続ができていない場合
                System.err.println("正常に接続できていません。statusCode:" + statusCode);
                return null;
            }

            // レスポンス(JSON文字列)を読み込む準備
            final InputStream in = con.getInputStream();
            String encoding = con.getContentEncoding();
            if(null == encoding){//エンコードする文字コード
                encoding = "UTF-8";
            }
            final InputStreamReader inReader = new InputStreamReader(in, encoding);
            final BufferedReader bufReader = new BufferedReader(inReader);//まとめて読み込むやつ
            StringBuilder response = new StringBuilder();//BufferedReaderで読み込んだ複数の文字列を連結するやつ
            String line = null;
            // 1行ずつ読み込む
            while((line = bufReader.readLine()) != null) {
                response.append(line);//連結
            }
            bufReader.close();
            inReader.close();
            in.close();

            // 受け取ったJSON文字列をパース
            JSONObject jsonObject = new JSONObject(response.toString());
            JSONObject todayForcasts = jsonObject.getJSONArray("forecasts").getJSONObject(TODAY_FORCAST_INDEX);
            String str=todayForcasts.getString("telop");

            //指定した文字列が存在するか確認

            return todayForcasts.getString("dateLabel") + "の天気は " + todayForcasts.getString("telop");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    /**
     * 非同期処理が終わった後の処理.
     * @param result 非同期処理の結果得られる文字列
     */
    @Override
    protected void onPostExecute(String result) {
//天気をテキストヴューに表示
        TextView tv = mainActivity.findViewById(R.id.messageTextView);
        tv.setText(result);

    }
}
