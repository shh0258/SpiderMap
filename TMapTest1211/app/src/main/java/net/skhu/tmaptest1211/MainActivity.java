package net.skhu.tmaptest1211;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapData;
import com.skt.Tmap.TMapPoint;
import com.skt.Tmap.TMapPolyLine;
import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {
    String Xval;
    String Yval;
    String Xend;
    String Yend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try{
                URL tmapurl = new URL("https://api2.sktelecom.com/tmap/routes/pedestrian?version=1&callback=tmaptest&startX="+Xval+"&startY="+Yval+"&endX="+Xend+"&endY"+Yend);
                    HttpsURLConnection myConnection = (HttpsURLConnection) tmapurl.openConnection();
                    myConnection.setRequestProperty("User-Agent", "tmaptest");

                    if (myConnection.getResponseCode() == 200) {
                        // Success
                        // Further processing here
                        InputStream responseBody =
                                myConnection.getInputStream();
                        InputStreamReader responseBodyReader =
                                new InputStreamReader(responseBody, "UTF-8");
                        JsonReader jsonReader = new JsonReader(responseBodyReader);

                        jsonReader.beginObject(); // Start processing the JSON object

                        while (jsonReader.hasNext()) { // Loop through all keys
                            String key = jsonReader.nextName(); // Fetch the next key

                            if (key.equals("turnType")) { // Check if desired key
                                // Fetch the value as a String
                                String value = jsonReader.nextString();

                                System.out.println("++++++++++++++++++++++++++++++++++++++="+value+"+++++++++++++++");

                                // Do something with the value
                                // ...

                                break; // Break out of the loop
                            } else {
                                jsonReader.skipValue(); // Skip values of other keys
                            }
                        }
                        jsonReader.close();
                        myConnection.disconnect();

                    } else {
                        // Error handling code goes here
                    }
            } catch(Exception e) {
                    e.printStackTrace();
                }

            }
        });





    }
}
