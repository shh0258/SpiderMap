package net.skhu.follwme1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.OutputStream;

/**
 * Created by hansanghyeon on 2017. 12. 9..
 */

public class BlueActivity extends FragmentActivity{

    ImageView spider;
    Button left;
    Button right;
    Button map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//레이아웃 바 없애기

        setContentView(R.layout.activity_blue);//메인 액티비티의 레이아웃 가져오기

        spider = (ImageView) findViewById(R.id.spiderimage);//그림 이미지 가져와서 쓰기위해 사용

        GlideDrawableImageViewTarget gifImage = new GlideDrawableImageViewTarget(spider);// gif를 쓰기 위해 라이브러리 추가
        Glide.with(this).load(R.drawable.spiderimage).into(gifImage);

        left =(Button) findViewById(R.id.left);

        View.OnClickListener listenerObj = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("1");
            }

        };
        left.setOnClickListener(listenerObj);

        right =(Button) findViewById(R.id.right);

        View.OnClickListener listenerObj1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendData("2");
            }

        };
        right.setOnClickListener(listenerObj1);

        map = (Button) findViewById(R.id.map);

        View.OnClickListener listenerObj2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "위치를 입력하세요.", Toast.LENGTH_LONG).show();
                intentBinding();
            }

        };
        map.setOnClickListener(listenerObj2);




    }

    void sendData(String msg) {
        try{
            // outputStream을 통해서 msg를 byte[] 배열을 통해 데이터를 전송한다.
            MainActivity.myOutputStream.write(msg.getBytes());

        }catch(Exception e) {  // 문자열 전송 도중 오류가 발생한 경우
            Toast.makeText(getApplicationContext(), "데이터 전송중 오류가 발생", Toast.LENGTH_SHORT).show();
            finish();  // App 종료
        }
    }

    void intentBinding(){
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);//맵 페이지로 넘어가 네비게이션 서비스를 받음
    }





    }
