package net.skhu.follwme1;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.skt.Tmap.TMapTapi;
import com.skt.Tmap.TMapView;

import android.os.Handler;
public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

//        LinearLayout linearLayoutTmap = (LinearLayout)findViewById(R.id.linearLayoutTmap);
//        linearLayoutTmap.addView(mapViewer());
        TMapTapi tmaptapi = new TMapTapi(this);
        tmaptapi.setSKPMapAuthentication ("9b65dd85-6a15-4a7e-ab8d-e4a8267112bf");
        tmaptapi.invokeTmap();

    }

//    TMapView mapViewer(){
//        TMapView tMapView = new TMapView(this);
//        tMapView.setSKPMapApiKey( "9b65dd85-6a15-4a7e-ab8d-e4a8267112bf" );
//        return tMapView;
//    }
}
