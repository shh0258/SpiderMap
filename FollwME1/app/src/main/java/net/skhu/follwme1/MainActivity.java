package net.skhu.follwme1;


import android.app.Activity;
import android.app.AlertDialog;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;
import android.os.Handler;
import android.os.Bundle;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends FragmentActivity {
    static final int REQUEST_ENABLE_BT = 10;
    int myPariedDeviceCount = 0;
    Set<BluetoothDevice> myDevices;
    BluetoothAdapter myBluetoothAdapter;
    BluetoothDevice myRemoteDevie;
    // 스마트폰과 페어링 된 디바이스간 통신 채널에 대응 하는 BluetoothSocket
    BluetoothSocket mySocket = null;
    static OutputStream myOutputStream = null;
    InputStream myInputStream = null;
    byte[] readBuffer;
    int readBufferPosition;
    Thread myWorkerThread = null;
    char myCharDelimiter =  '\n';
    ImageView iv_myicon;
    Vibrator mvibe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//레이아웃 바 없애기

        setContentView(R.layout.activity_main);//메인 액티비티의 레이아웃 가져오기

        iv_myicon =(ImageView) findViewById(R.id.tempimg);//그림 이미지 가져와서 쓰기위해 사용
        mvibe= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);//진동을 위한 객체 선언

    }

    @Override
    public boolean onTouchEvent(MotionEvent event){//만약 화면의 터치가 일어나면

        switch(event.getAction()){//터치가 있었다면
            case MotionEvent.ACTION_DOWN://터치가 시작될 때
                mvibe.vibrate(50);// 진동을 0.05초 함
                checkBlueTooth();
                break;


        }
        return false;
    }

    void checkBlueTooth() {
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(myBluetoothAdapter == null) {
            // 장치가 블루투스 지원하지 않는 경우
            Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_SHORT).show();
            finish();   // 어플리케이션 종료
        }

        else {
            // 장치가 블루투스 지원하는 경우
            if(!myBluetoothAdapter.isEnabled()) {
                // 블루투스를 지원하지만 비활성 상태인 경우
                // 블루투스를 활성 상태로 바꾸기 위해 사용자 동의 요첨
                Toast.makeText(getApplicationContext(), "현재 블루투스가 비활성 상태입니다.", Toast.LENGTH_SHORT).show();

                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            else {
                // 블루투스를 지원하며 활성 상태인 경우
                selectDevice();
            }
        }
    }

    void selectDevice() {
        // 블루투스 디바이스는 연결해서 사용하기 전에 먼저 페어링 되어야만 한다
        // getBondedDevices() : 페어링된 장치 목록 얻어오는 함수.
        try{
            myDevices = myBluetoothAdapter.getBondedDevices();
            myPariedDeviceCount = myDevices.size();
        }catch(NullPointerException e){
            myPariedDeviceCount =0;
        }

        if(myPariedDeviceCount == 0 ) { // 페어링된 장치가 없는 경우.
            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
            finish(); // App 종료.
        }
        // 페어링된 장치가 있는 경우.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");

        // 각 디바이스는 이름과(서로 다른) 주소를 가진다. 페어링 된 디바이스들을 표시한다.
        List<String> listItems = new ArrayList<String>();
        try {
            for (BluetoothDevice device : myDevices) {
                // device.getName() : 단말기의 Bluetooth Adapter 이름을 반환.
                listItems.add(device.getName());
            }
            listItems.add("취소");  // 취소 항목 추가.
        } catch (NullPointerException e){
            listItems.add("취소");
        }


        // CharSequence : 변경 가능한 문자열.
        // toArray : List형태로 넘어온것 배열로 바꿔서 처리하기 위한 toArray() 함수.
        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        // toArray 함수를 이용해서 size만큼 배열이 생성 되었다.
        listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                // TODO Auto-generated method stub
                if(item == myPariedDeviceCount) { // 연결할 장치를 선택하지 않고 '취소' 를 누른 경우.
                    Toast.makeText(getApplicationContext(), "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                    finish();
                }
                else { // 연결할 장치를 선택한 경우, 선택한 장치와 연결을 시도함.
                    connectToSelectedDevice(items[item].toString());

                }
            }

        });

        builder.setCancelable(false);  // 뒤로 가기 버튼 사용 금지.
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // startActivityForResult 를 여러번 사용할 땐 이런 식으로 switch 문을 사용하여 어떤 요청인지 구분하여 사용함.
        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK) { // 블루투스 활성화 상태
                    selectDevice();
                }
                else if(resultCode == RESULT_CANCELED) { // 블루투스 비활성화 상태 (종료)
                    Toast.makeText(getApplicationContext(), "블루투수를 사용할 수 없어 프로그램을 종료합니다", Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //  connectToSelectedDevice() : 원격 장치와 연결하는 과정을 나타냄.
    //        실제 데이터 송수신을 위해서는 소켓으로부터 입출력 스트림을 얻고 입출력 스트림을 이용하여 이루어 진다.
    void connectToSelectedDevice(String selectedDeviceName) {
        // BluetoothDevice 원격 블루투스 기기를 나타냄.
        myRemoteDevie = getDeviceFromBondedList(selectedDeviceName);
        // java.util.UUID.fromString : 자바에서 중복되지 않는 Unique 키 생성.
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            // 소켓 생성, RFCOMM 채널을 통한 연결.
            // createRfcommSocketToServiceRecord(uuid) : 이 함수를 사용하여 원격 블루투스 장치와 통신할 수 있는 소켓을 생성함.
            // 이 메소드가 성공하면 스마트폰과 페어링 된 디바이스간 통신 채널에 대응하는 BluetoothSocket 오브젝트를 리턴함.
            mySocket = myRemoteDevie.createRfcommSocketToServiceRecord(uuid);
            mySocket.connect(); // 소켓이 생성 되면 connect() 함수를 호출함으로써 두기기의 연결은 완료된다.
            // 데이터 송수신을 위한 스트림 얻기.
            // BluetoothSocket 오브젝트는 두개의 Stream을 제공한다.
            // 1. 데이터를 보내기 위한 OutputStrem
            // 2. 데이터를 받기 위한 InputStream

            myOutputStream = mySocket.getOutputStream();
            myInputStream = mySocket.getInputStream();


            Intent intent = new Intent(this, BlueActivity.class);
            startActivity(intent);//만약 연결 성공한다면 다른페이지로 넘어가서 작업 수행

            Toast.makeText(getApplicationContext(), "선택한 장치와 연결성공 ", Toast.LENGTH_LONG).show();

            // 데이터 수신 준비.
            beginListenForData();

        }catch(Exception e) { // 블루투스 연결 중 오류 발생
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
            finish();  // App 종료
        }
    }
//
    BluetoothDevice getDeviceFromBondedList(String name) {
        // selectedDevice는 블루투스 기기들 중에서 선택된 기기를 저장하기 위해 선언했습니다.
        BluetoothDevice selectedDevice = null;
        // 블루투스 기기를 Set로 한 이유가 블루투스 기기가 동일한 것들에 대해 저장을 하지 않도록 하기 위해서 Set로 지정.
        // 각 device를 통해서 기기 이름을 통해 연결을 시도합니다. device에는 이름, 주소, uuid 등등이 있습니다.
        for(BluetoothDevice deivce : myDevices) {
            if(name.equals(deivce.getName())) { // getName()을 통해서 디바이스의 이름이 동일한 경우에
                selectedDevice = deivce; // 저장을 하고 난 후에 반환을 합니다.
                break;
            }
        }
        return selectedDevice;
    }

    // 데이터 수신(쓰레드 사용 수신된 메시지를 계속 검사함)
    void beginListenForData() {
        final Handler handler = new Handler();

        readBufferPosition = 0;                 // 버퍼 내 수신 문자 저장 위치.
        readBuffer = new byte[1024];            // 수신 버퍼.

        // 문자열 수신 쓰레드.
        myWorkerThread = new Thread(new Runnable()
        {
            @Override
            public void run() {
                // interrupt() 메소드를 이용 스레드를 종료시키는 예제이다.
                // interrupt() 메소드는 하던 일을 멈추는 메소드이다.
                // isInterrupted() 메소드를 사용하여 멈추었을 경우 반복문을 나가서 스레드가 종료하게 된다.
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        // InputStream.available() : 다른 스레드에서 blocking 하기 전까지 읽은 수 있는 문자열 개수를 반환함.
                        int byteAvailable = myInputStream.available();   // 수신 데이터 확인
                        if(byteAvailable > 0) {                        // 데이터가 수신된 경우.
                            byte[] packetBytes = new byte[byteAvailable];
                            // read(buf[]) : 입력스트림에서 buf[] 크기만큼 읽어서 저장 없을 경우에 -1 리턴.
                            myInputStream.read(packetBytes);
                            for(int i=0; i<byteAvailable; i++) {
                                byte b = packetBytes[i];
                                if(b == myCharDelimiter) {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    //  System.arraycopy(복사할 배열, 복사시작점, 복사된 배열, 붙이기 시작점, 복사할 개수)
                                    //  readBuffer 배열을 처음 부터 끝까지 encodedBytes 배열로 복사.
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);

                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable(){
                                        // 수신된 문자열 데이터에 대한 처리.
                                        @Override
                                        public void run() {
                                            String s=(data.indexOf(0)=='1') ? "작동" : "종료";
                                            Toast.makeText(MainActivity.this, String.format("기기를 %s합니다.", s), Toast.LENGTH_SHORT);
                                        }
                                    });
                                }
                                else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }

                    } catch (Exception e) {    // 데이터 수신 중 오류 발생.
                        Toast.makeText(getApplicationContext(), "데이터 수신 중 오류가 발생 했습니다.", Toast.LENGTH_LONG).show();
                        finish();            // App 종료.
                    }
                }
            }

        });

    }

    @Override
    protected void onDestroy() {
        try{
            myWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
            myInputStream.close();
            mySocket.close();
        }catch(Exception e){}
        super.onDestroy();
    }


}
