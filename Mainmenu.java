package com.example.zzamtiger.textview;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import com.google.firebase.messaging.FirebaseMessaging;
import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;




public class Mainmenu extends AppCompatActivity implements OnClickListener {
    static final int REQUEST_ENABLE_BT = 10;
    int mPariedDeviceCount = 0;
    Set<BluetoothDevice> mDevices;
    // 폰의 블루투스 모듈을 사용하기 위한 오브젝트.
    BluetoothAdapter mBluetoothAdapter;
    /**
     BluetoothDevice 로 기기의 장치정보를 알아낼 수 있는 자세한 메소드 및 상태값을 알아낼 수 있다.
     연결하고자 하는 다른 블루투스 기기의 이름, 주소, 연결 상태 등의 정보를 조회할 수 있는 클래스.
     현재 기기가 아닌 다른 블루투스 기기와의 연결 및 정보를 알아낼 때 사용.
     */
    BluetoothDevice mRemoteDevie;
    // 스마트폰과 페어링 된 디바이스간 통신 채널에 대응 하는 BluetoothSocket
    BluetoothSocket mSocket = null;
    OutputStream mOutputStream = null;
    InputStream mInputStream = null;
    String mStrDelimiter = "\n";


    java.lang.Thread mWorkerThread = null;

    EditText SsidText, PassText;
    Button sendButton;
    Button cancelButton;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long backPressedTime = 0;

    public static final int SEND_INFORMATION = 0;
    public static final int SEND_STOP = 1;
    Thread thread;

    TextView tx = null;  //미세먼지 텍스트뷰

    ImageView ivmaintemp;    //온도에 따른 이미지 뷰
    ImageView ivmaindust;    //미세먼지에 따른 이미지 뷰
    ImageView ivmainwater;    //수위에 따른 이미지 뷰

    TextView txtemp = null;  //전체 온도
    TextView txwaterlevel = null;  //전체 수위
    TextView txname1 = null;  //화분1 이름
    TextView txname2 = null;  //화분2 이름
    TextView txname3 = null;  //화분3 이름
    TextView txhumd1 = null;  //화분1 토양습도
    TextView txhumd2 = null;  //화분2 토양습도
    TextView txhumd3 = null;  //화분3 토양습도
    TextView txsunshine1 = null;  //화분1 일조량
    TextView txsunshine2 = null;  //화분2 일조량
    TextView txsunshine3 = null;  //화분3 일조량
    ImageButton btplus = null;     //화분추가
    ImageButton btlogout = null;   //로그아웃
    ImageButton btblutooth = null;   //블루투스 연결

    Button btop1 = null;      //화분1 상세설정
    Button btop2 = null;      //화분2 상세설정
    Button btop3 = null;      //화분3 상세설정

    ArrayList<ListItem> listItem = new ArrayList<ListItem>();
    ListItem Item;
    String user_id = null;
    int plantnum;
    String first = "http://teambutton.dothome.co.kr/first.php";  //화분들 현재상태 불러오기
    String Tokensave = "http://teambutton.dothome.co.kr/Token.php";  //화분들 현재상태 불러오기
    String urlpath1 = "http://teambutton.dothome.co.kr/Mainstate.php";  //화분들 현재상태 불러오기
    String dust = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnMesureLIst?itemCode=PM10&dataGubun=HOUR&pageNo=1&numOfRows=10&ServiceKey=%2FhO6MEaWMef4ZjlgRNqODaKbH%2Fl5LwCPFEQriP%2FQrLwv3Jr3dHj0on1xIEM%2FNa1%2BAS%2F8TALZiP4rFH8sFMkzCA%3D%3D&_returnType=json";  //샘플

    String id = null;
    String pid = null;
    String pid1 = null;   //화분 상세페이지에 보낼 변수들
    String pid2 = null;
    String pid3 = null;
    String Area;    //화분 주인이 등록한 지역
    String token;   //FCM을 위한 토큰
    String wifiname;    //와이파이 이름
    String wifipass;    //와이파이 비번

    public class ListItem {
        private String[] mData;

        public ListItem(String[] data) {
            mData = data;
        }

        public ListItem(String Name, String Humd, String Sunshine, String Temp, String Waterlevel, String PID) {
            mData = new String[6];
            mData[0] = Name;
            mData[1] = Humd;
            mData[2] = Sunshine;
            mData[3] = Temp;
            mData[4] = Waterlevel;
            mData[5] = PID;
        }

        public String[] getData() {
            return mData;
        }

        public String getData(int index) {
            return mData[index];
        }

        public void setData(String[] data) {
            mData = data;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btlogout = (ImageButton) findViewById(R.id.btlogout);
        btplus = (ImageButton) findViewById(R.id.btplus);
        btblutooth = (ImageButton) findViewById(R.id.btblutooth);
        btop1 = (Button) findViewById(R.id.btop1);
        btop2 = (Button) findViewById(R.id.btop2);
        btop3 = (Button) findViewById(R.id.btop3);

        tx = (TextView) findViewById(R.id.textView17);

        txtemp = (TextView) findViewById(R.id.txtemp);
        txwaterlevel = (TextView) findViewById(R.id.txwaterlevel);

        txname1 = (TextView) findViewById(R.id.txname1);
        txname2 = (TextView) findViewById(R.id.txname2);
        txname3 = (TextView) findViewById(R.id.txname3);
        txhumd1 = (TextView) findViewById(R.id.txhumd1);
        txhumd2 = (TextView) findViewById(R.id.txhumd2);
        txhumd3 = (TextView) findViewById(R.id.txhumd3);
        txsunshine1 = (TextView) findViewById(R.id.txsunshine1);
        txsunshine2 = (TextView) findViewById(R.id.txsunshine2);
        txsunshine3 = (TextView) findViewById(R.id.txsunshine3);

        ivmaintemp=(ImageView)findViewById(R.id.ivmaintemp);
        ivmaindust=(ImageView)findViewById(R.id.ivmaindust);
        ivmainwater=(ImageView)findViewById(R.id.ivmainwater);

        btlogout.setOnClickListener(this);
        btplus.setOnClickListener(this);
        btblutooth.setOnClickListener(this);
        btop1.setOnClickListener(this);
        btop2.setOnClickListener(this);
        btop3.setOnClickListener(this);

        SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);
        String id = data.getString("id", "");   //저장되어있는 id가져오기
        user_id = id; // php로 보낼 변수에 저장

        FirebaseInstanceId.getInstance().getToken();        //토큰 얻기
        token = FirebaseInstanceId.getInstance().getToken();
        Log.d("FCM_Token", token);
        FirebaseMessaging.getInstance().subscribeToTopic("notice");

        SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        new first().execute();       //거주지역 저장
        new Token().execute();       //토큰 등록
        new Mainstate().execute();       //메인메뉴 화분 상태 갱신
        new Duststate().execute();      //거주지역에 따른 미세먼지 측정
        thread=new Thread();
        thread.start();

    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SEND_INFORMATION:
                    break;

                case SEND_STOP:
                    thread.stopThread();
                    break;

                default:
                    break;
            }
        }
    };

    class Thread extends java.lang.Thread {
        boolean stopped = false;
        int i = 0;

        public Thread() {
            stopped = false;
        }

        public void stopThread() {
            stopped = true;
        }

        @Override
        public void run() {
            super.run();
            while (stopped == false) {
                i++;
                try {
                    new Mainstate().execute();      //메인화면 갱신
                    new Duststate().execute();      //미세먼지 측정 갱신
                    sleep(1800000);       //30분마다 메인화면과 미세먼지 갱신
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 블루투스 장치의 이름이 주어졌을때 해당 블루투스 장치 객체를 페어링 된 장치 목록에서 찾아내는 코드.
    BluetoothDevice getDeviceFromBondedList(String name) {
        // BluetoothDevice : 페어링 된 기기 목록을 얻어옴.
        BluetoothDevice selectedDevice = null;
        // getBondedDevices 함수가 반환하는 페어링 된 기기 목록은 Set 형식이며,
        // Set 형식에서는 n 번째 원소를 얻어오는 방법이 없으므로 주어진 이름과 비교해서 찾는다.
        for(BluetoothDevice deivce : mDevices) {
            // getName() : 단말기의 Bluetooth Adapter 이름을 반환
            if(name.equals(deivce.getName())) {
                selectedDevice = deivce;
                break;
            }
        }
        return selectedDevice;
    }

    // 문자열 전송하는 함수(쓰레드 사용 x)
    void sendData(String msg) {
        msg += mStrDelimiter;  // 문자열 종료표시 (\n)
        try{
            mOutputStream.write(msg.getBytes());  // 문자열 전송.
        }catch(Exception e) {  // 문자열 전송 도중 오류가 발생한 경우
            Toast.makeText(getApplicationContext(), "데이터 전송중 오류가 발생\n 앱을 종료하고 블루투스를 다시 연결 하세요.", Toast.LENGTH_LONG).show();
        }
    }



    void connectToSelectedDevice(String selectedDeviceName) {
        // BluetoothDevice 원격 블루투스 기기를 나타냄.
        mRemoteDevie = getDeviceFromBondedList(selectedDeviceName);
        // java.util.UUID.fromString : 자바에서 중복되지 않는 Unique 키 생성.
        UUID uuid = java.util.UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

        try {
            // 소켓 생성, RFCOMM 채널을 통한 연결.
            // createRfcommSocketToServiceRecord(uuid) : 이 함수를 사용하여 원격 블루투스 장치와 통신할 수 있는 소켓을 생성함.
            // 이 메소드가 성공하면 스마트폰과 페어링 된 디바이스간 통신 채널에 대응하는 BluetoothSocket 오브젝트를 리턴함.
            mSocket = mRemoteDevie.createRfcommSocketToServiceRecord(uuid);
            mSocket.connect(); // 소켓이 생성 되면 connect() 함수를 호출함으로써 두기기의 연결은 완료된다.

            // 데이터 송수신을 위한 스트림 얻기.
            // BluetoothSocket 오브젝트는 두개의 Stream을 제공한다.
            // 1. 데이터를 보내기 위한 OutputStrem
            // 2. 데이터를 받기 위한 InputStream
            mOutputStream = mSocket.getOutputStream();

            Context mContext = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.custom_dialog,(ViewGroup) findViewById(R.id.layout_root));
            AlertDialog.Builder aDialog = new AlertDialog.Builder(Mainmenu.this);
            aDialog.setTitle("WiFi 연결 시작");
            aDialog.setView(layout);

            final EditText wifina=(EditText)layout.findViewById((R.id.image));
            final EditText wifipa=(EditText)layout.findViewById((R.id.text));
            aDialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    wifiname=wifina.getText().toString();       //wifi 이름 저장
                    wifipass=wifipa.getText().toString();       //wifi 비번 저장
                    sendData(wifina.getText()+"&"+wifipa.getText()+"&"+user_id);
                    dialog.dismiss();
                }

            });

            aDialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }

            });
            AlertDialog ad = aDialog.create();
            ad.show();

        }catch(Exception e) { // 블루투스 연결 중 오류 발생
            Toast.makeText(getApplicationContext(), "블루투스 연결 중 오류가 발생했습니다.", Toast.LENGTH_LONG).show();
        }
    }


    // 블루투스 지원하며 활성 상태인 경우.
    void selectDevice() {
        // 블루투스 디바이스는 연결해서 사용하기 전에 먼저 페어링 되어야만 한다
        // getBondedDevices() : 페어링된 장치 목록 얻어오는 함수.
        mDevices = mBluetoothAdapter.getBondedDevices();
        mPariedDeviceCount = mDevices.size();

        if(mPariedDeviceCount == 0 ) { // 페어링된 장치가 없는 경우.
            Toast.makeText(getApplicationContext(), "페어링된 장치가 없습니다.", Toast.LENGTH_LONG).show();
        }
        // 페어링된 장치가 있는 경우.
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("블루투스 장치 선택");

        // 각 디바이스는 이름과(서로 다른) 주소를 가진다. 페어링 된 디바이스들을 표시한다.
        List<String> listItems = new ArrayList<String>();
        for(BluetoothDevice device : mDevices) {
            // device.getName() : 단말기의 Bluetooth Adapter 이름을 반환.
            listItems.add(device.getName());
        }
        listItems.add("취소");  // 취소 항목 추가.


        // CharSequence : 변경 가능한 문자열.
        // toArray : List형태로 넘어온것 배열로 바꿔서 처리하기 위한 toArray() 함수.
        final CharSequence[] items = listItems.toArray(new CharSequence[listItems.size()]);
        // toArray 함수를 이용해서 size만큼 배열이 생성 되었다.
        listItems.toArray(new CharSequence[listItems.size()]);

        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int item) {
                // TODO Auto-generated method stub
                if(item == mPariedDeviceCount) { // 연결할 장치를 선택하지 않고 '취소' 를 누른 경우.
                    Toast.makeText(getApplicationContext(), "연결할 장치를 선택하지 않았습니다.", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
                else { // 연결할 장치를 선택한 경우, 선택한 장치와 연결을 시도함.
                    connectToSelectedDevice(items[item].toString());
                }
            }

        });

        builder.setCancelable(false);  // 뒤로 가기 버튼 사용 금지.
        android.app.AlertDialog alert = builder.create();
        alert.show();
    }


    void checkBluetooth() {
        /**
         * getDefaultAdapter() : 만일 폰에 블루투스 모듈이 없으면 null 을 리턴한다.
         이경우 Toast를 사용해 에러메시지를 표시하고 앱을 종료한다.
         */
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null ) {  // 블루투스 미지원
            Toast.makeText(getApplicationContext(), "기기가 블루투스를 지원하지 않습니다.", Toast.LENGTH_LONG).show();
        }
        else { // 블루투스 지원
            /** isEnable() : 블루투스 모듈이 활성화 되었는지 확인.
             *               true : 지원 ,  false : 미지원
             */
            if(!mBluetoothAdapter.isEnabled()) { // 블루투스 지원하며 비활성 상태인 경우.
                Toast.makeText(getApplicationContext(), "현재 블루투스가 비활성 상태입니다.", Toast.LENGTH_LONG).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                // REQUEST_ENABLE_BT : 블루투스 활성 상태의 변경 결과를 App 으로 알려줄 때 식별자로 사용(0이상)
                /**
                 startActivityForResult 함수 호출후 다이얼로그가 나타남
                 "예" 를 선택하면 시스템의 블루투스 장치를 활성화 시키고
                 "아니오" 를 선택하면 비활성화 상태를 유지 한다.
                 선택 결과는 onActivityResult 콜백 함수에서 확인할 수 있다.
                 */
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            else // 블루투스 지원하며 활성 상태인 경우.
                selectDevice();
        }
    }



    // onDestroy() : 어플이 종료될때 호출 되는 함수.
    //               블루투스 연결이 필요하지 않는 경우 입출력 스트림 소켓을 닫아줌.
    @Override
    protected void onDestroy() {
        try{
            mWorkerThread.interrupt(); // 데이터 수신 쓰레드 종료
            mInputStream.close();
            mSocket.close();
        }catch(Exception e){}
        super.onDestroy();
    }


    // onActivityResult : 사용자의 선택결과 확인 (아니오, 예)
    // RESULT_OK: 블루투스가 활성화 상태로 변경된 경우. "예"
    // RESULT_CANCELED : 오류나 사용자의 "아니오" 선택으로 비활성 상태로 남아 있는 경우  RESULT_CANCELED

    /**
     사용자가 request를 허가(또는 거부)하면 안드로이드 앱의 onActivityResult 메소도를 호출해서 request의 허가/거부를 확인할수 있다.
     첫번째 requestCode : startActivityForResult 에서 사용했던 요청 코드. REQUEST_ENABLE_BT 값
     두번째 resultCode  : 종료된 액티비티가 setReuslt로 지정한 결과 코드. RESULT_OK, RESULT_CANCELED 값중 하나가 들어감.
     세번째 data        : 종료된 액티비티가 인테트를 첨부했을 경우, 그 인텐트가 들어있고 첨부하지 않으면 null
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // startActivityForResult 를 여러번 사용할 땐 이런 식으로 switch 문을 사용하여 어떤 요청인지 구분하여 사용함.
        switch(requestCode) {
            case REQUEST_ENABLE_BT:
                if(resultCode == RESULT_OK) { // 블루투스 활성화 상태
                    selectDevice();
                }
                else if(resultCode == RESULT_CANCELED) { // 블루투스 비활성화 상태 (종료)
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



    public class first extends AsyncTask<Void, Void, String> {     //맨처음 사용자의 거주지역정보를 받아오기 위함
        StringBuilder jsonHtml = new StringBuilder();

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL obj = new URL(first);

                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                if (conn != null) {      // 연결되었음 코드가 리턴되면.
                    conn.setRequestMethod("POST"); //요청 방식을 설정 (default : GET)
                    conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                    conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)

                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(
                            "&user_id=" + user_id
                    ); //요청 파라미터를 입력
                    writer.flush();
                    writer.close();
                    os.close();
                    conn.connect();
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
            return jsonHtml.toString();
        }

        @Override
        protected void onPostExecute(String str) {       //json 가지고 작업하기

            try {

                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    Area = jo.getString("AREA");                    //사용자의 거주지역을 저장
                }
                //tx.setText(Area);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    public class Token extends AsyncTask<Void, Void, String> {     //토큰 등록
        StringBuilder jsonHtml = new StringBuilder();

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL obj = new URL(Tokensave);

                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                if (conn != null) {      // 연결되었음 코드가 리턴되면.
                    conn.setRequestMethod("POST"); //요청 방식을 설정 (default : GET)
                    conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                    conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)

                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(
                            "&user_id=" + user_id + "&token=" + token
                    ); //요청 파라미터를 입력
                    writer.flush();
                    writer.close();
                    os.close();
                    conn.connect();
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
            return jsonHtml.toString();
        }

        @Override
        protected void onPostExecute(String str) {       //json 가지고 작업하기
        }
    }

    public class Mainstate extends AsyncTask<Void, Void, String> {     //메인메뉴 화분 상태 갱신을 위해서 php 이용
        StringBuilder jsonHtml = new StringBuilder();
        double dtemp;
        @Override
        protected String doInBackground(Void... params) {

            try {
                URL obj = new URL(urlpath1);

                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                if (conn != null) {      // 연결되었음 코드가 리턴되면.
                    conn.setRequestMethod("POST"); //요청 방식을 설정 (default : GET)
                    conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                    conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)

                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(
                            "&user_id=" + user_id
                    ); //요청 파라미터를 입력
                    writer.flush();
                    writer.close();
                    os.close();
                    conn.connect();
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
            return jsonHtml.toString();
        }

        @Override
        protected void onPostExecute(String str) {       //json 가지고 작업하기
            String Temp;        //화분 온도
            String Waterlevel;        //화분 수위
            String Name;        //화분 이름
            String Humd;        //화분 습도
            String Sunshine;    //화분 일조량
            String PID;    //화분 고유 번호


            String a;
            try {

                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    Name = jo.getString("Name");
                    Humd = jo.getString("Humd");
                    Sunshine = jo.getString("Sunshine");
                    Temp = jo.getString("Temp");
                    Waterlevel = jo.getString("Waterlevel");
                    PID = jo.getString("PID");
                    listItem.add(new ListItem(Name, Humd, Sunshine, Temp, Waterlevel, PID));
                }
                plantnum = ja.length();       //현재 화분 갯수 알아내기
                SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);    //id값 저장
                SharedPreferences.Editor editor = data.edit();
                editor.putInt("plantnum", plantnum);        //화분 갯수 저장
                editor.commit();        //저장

                a = String.valueOf(plantnum);   //화분갯수 확인용
                //tx.setText(a);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (plantnum == 0)     //화분이 하나도 없을 경우
            {
                txname1.setText("화분을");
                txhumd1.setText("추가해");
                txsunshine1.setText("주세요");
                txname2.setText("화분을");
                txhumd2.setText("추가해");
                txsunshine2.setText("주세요");
                txname3.setText("화분을");
                txhumd3.setText("추가해");
                txsunshine3.setText("주세요");

                btop1.setVisibility(View.INVISIBLE);
                btop2.setVisibility(View.INVISIBLE);
                btop3.setVisibility(View.INVISIBLE);
            } else if (plantnum == 1)         //화분 1개일떄의 설정
            {

                Name = listItem.get(0).getData(0);
                txname1.setText(Name);
                Humd = listItem.get(0).getData(1);
                txhumd1.setText(Humd);
                Sunshine = listItem.get(0).getData(2);
                txsunshine1.setText(Sunshine);

                Temp = listItem.get(0).getData(3);
                dtemp=Double.valueOf(Temp).doubleValue();
                if(dtemp>25)
                {
                    ivmaintemp.setImageResource(R.drawable.hightemp);
                }
                else if(dtemp<=25 && dtemp>15)
                {
                    ivmaintemp.setImageResource(R.drawable.midtemp);
                }
                else if(dtemp<=15)
                {
                    ivmaintemp.setImageResource(R.drawable.lowtemp);
                }
                txtemp.setText("온도:" + Temp + "℃");

                Waterlevel = listItem.get(0).getData(4);
                if(Waterlevel.equals("sang"))
                {
                    ivmainwater.setImageResource(R.drawable.highwater);
                    txwaterlevel.setText("수위:상");
                }
                else if(Waterlevel.equals("jung"))
                {
                    ivmainwater.setImageResource(R.drawable.midwater);
                    txwaterlevel.setText("수위:중");
                }
                else if(Waterlevel.equals("ha"))
                {
                    ivmainwater.setImageResource(R.drawable.lowwater);
                    txwaterlevel.setText("수위:하");
                }
                pid1 = listItem.get(0).getData(5);

                txname2.setText("화분을");
                txhumd2.setText("추가해");
                txsunshine2.setText("주세요");
                txname3.setText("화분을");
                txhumd3.setText("추가해");
                txsunshine3.setText("주세요");


                btop2.setVisibility(View.INVISIBLE);
                btop3.setVisibility(View.INVISIBLE);

            } else if (plantnum == 2)   //화분 2개일떄의 설정
            {

                Name = listItem.get(0).getData(0);
                txname1.setText(Name);
                Humd = listItem.get(0).getData(1);
                txhumd1.setText(Humd);
                Sunshine = listItem.get(0).getData(2);
                txsunshine1.setText(Sunshine);


                Temp = listItem.get(0).getData(3);
                dtemp=Double.valueOf(Temp).doubleValue();
                if(dtemp>25)
                {
                    ivmaintemp.setImageResource(R.drawable.hightemp);
                }
                else if(dtemp<=25 && dtemp>15)
                {
                    ivmaintemp.setImageResource(R.drawable.midtemp);
                }
                else if(dtemp<=15)
                {
                    ivmaintemp.setImageResource(R.drawable.lowtemp);
                }
                txtemp.setText("온도:" + Temp + "℃");


                Waterlevel = listItem.get(0).getData(4);
                Log.v("Waterlevel",Waterlevel);
                if(Waterlevel.equals("sang"))
                {
                    ivmainwater.setImageResource(R.drawable.highwater);
                    txwaterlevel.setText("수위:상");
                }
                else if(Waterlevel.equals("jung"))
                {
                    ivmainwater.setImageResource(R.drawable.midwater);
                    txwaterlevel.setText("수위:중");
                }
                else if(Waterlevel.equals("ha"))
                {
                    ivmainwater.setImageResource(R.drawable.lowwater);
                    txwaterlevel.setText("수위:하");
                }

                Name = listItem.get(1).getData(0);
                txname2.setText(Name);
                Humd = listItem.get(1).getData(1);
                txhumd2.setText(Humd);
                Sunshine = listItem.get(1).getData(2);
                txsunshine2.setText(Sunshine);
                pid1 = listItem.get(0).getData(5);
                pid2 = listItem.get(1).getData(5);

                txname3.setText("화분을");
                txhumd3.setText("추가해");
                txsunshine3.setText("주세요");

                btop3.setVisibility(View.INVISIBLE);
            } else if (plantnum == 3)       //화분 3개일떄의 설정
            {

                Name = listItem.get(0).getData(0);
                txname1.setText(Name);
                Humd = listItem.get(0).getData(1);
                txhumd1.setText(Humd);
                Sunshine = listItem.get(0).getData(2);
                txsunshine1.setText(Sunshine);

                Temp = listItem.get(0).getData(3);
                dtemp=Double.valueOf(Temp).doubleValue();
                if(dtemp>25)
                {
                    ivmaintemp.setImageResource(R.drawable.hightemp);
                }
                else if(dtemp<=25 && dtemp>15)
                {
                    ivmaintemp.setImageResource(R.drawable.midtemp);
                }
                else if(dtemp<=15)
                {
                    ivmaintemp.setImageResource(R.drawable.lowtemp);
                }
                txtemp.setText("온도:" + Temp + "℃");

                Waterlevel = listItem.get(0).getData(4);
                if(Waterlevel.equals("sang"))
                {
                    ivmainwater.setImageResource(R.drawable.highwater);
                    txwaterlevel.setText("수위:상");
                }
                else if(Waterlevel.equals("jung"))
                {
                    ivmainwater.setImageResource(R.drawable.midwater);
                    txwaterlevel.setText("수위:중");
                }
                else if(Waterlevel.equals("ha"))
                {
                    ivmainwater.setImageResource(R.drawable.lowwater);
                    txwaterlevel.setText("수위:하");
                }


                Name = listItem.get(1).getData(0);
                txname2.setText(Name);
                Humd = listItem.get(1).getData(1);
                txhumd2.setText(Humd);
                Sunshine = listItem.get(1).getData(2);
                txsunshine2.setText(Sunshine);

                Name = listItem.get(2).getData(0);
                txname3.setText(Name);
                Humd = listItem.get(2).getData(1);
                txhumd3.setText(Humd);
                Sunshine = listItem.get(2).getData(2);
                txsunshine3.setText(Sunshine);

                pid1 = listItem.get(0).getData(5);
                pid2 = listItem.get(1).getData(5);
                pid3 = listItem.get(2).getData(5);
            }

        }
    }
    public class Duststate extends AsyncTask<Void, Void, String> {     //거주지역에 따른 미세먼지 측정
        StringBuilder jsonHtml = new StringBuilder();

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL obj = new URL(dust);

                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                if (conn != null) {      // 연결되었음 코드가 리턴되면.
                    conn.setRequestMethod("POST"); //요청 방식을 설정 (default : GET)
                    conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                    conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)

                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    OutputStream os = conn.getOutputStream();
                    os.close();
                    conn.connect();
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            jsonHtml.append(line + "\n");
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }
            return jsonHtml.toString();
        }

        @Override
        protected void onPostExecute(String str) {       //json 가지고 작업하기
            String dust=null;
            int intdust;
            try {

                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("list");

                root = ja.getJSONObject(0); // 가장 최신 결과만을 얻음
                dust = root.getString(Area);    //사용자의 거주지역에 따른 정보 흭득
                intdust= Integer.parseInt(dust);      //string to int
                if(intdust>=0 && intdust<31)                //값에 따른 미세먼지 상태 출력
                {
                    tx.setText(Area+"\n"+"미세먼지:좋음");
                    ivmaindust.setImageResource(R.drawable.sunny);
                }
                else if(intdust>=31 && intdust<81)
                {
                    tx.setText(Area+"\n"+"미세먼지:보통");
                    ivmaindust.setImageResource(R.drawable.sunny);
                }
                else if(intdust>=81 && intdust<150)
                {
                    tx.setText(Area+"\n"+"미세먼지:나쁨");
                    ivmaindust.setImageResource(R.drawable.dust);
                }
                else
                {
                    tx.setText(Area+"\n"+"미세먼지:매우나쁨");
                    ivmaindust.setImageResource(R.drawable.dust);
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void onClick(View arg0){
        Intent intent=getIntent();
        switch(arg0.getId()){
            case R.id.btlogout:         //로그아웃
                handler.sendEmptyMessage(SEND_STOP);
                Intent itlogout=new Intent(this,Loginmenu.class);
                startActivity(itlogout);
                break;
            case R.id.btplus:           //화분추가
                handler.sendEmptyMessage(SEND_STOP);
                SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);
                int ppid = data.getInt("plantnum",0);   //저장되어있는 화분 갯수가져오기
                if(ppid==3)
                {
                    Toast toast = Toast.makeText(this, "더이상 화분을 추가하실수 없습니다.", Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                }
                Intent itplus=new Intent(this,plusActivity.class);
                startActivity(itplus);
                break;
            case R.id.btblutooth:           //블루투스 연결
                handler.sendEmptyMessage(SEND_STOP);
                checkBluetooth();
                break;
            case R.id.btop1:            //화분1 상세 페이지
                handler.sendEmptyMessage(SEND_STOP);
                Intent itop1=new Intent(this,Detail.class);
                data = getSharedPreferences("data", MODE_PRIVATE);
                id = data.getString("id", "");   //저장되어있는 id가져오기
                user_id=id; // php로 보낼 변수에 저장
                itop1.putExtra("pid",pid1);  //화분1의 설정 정보들을 읽기위한 구별값 전달
                startActivity(itop1);
                break;
            case R.id.btop2:            //화분2 상세 페이지
                handler.sendEmptyMessage(SEND_STOP);
                Intent itop2=new Intent(this,Detail.class);
                data = getSharedPreferences("data", MODE_PRIVATE);
                id = data.getString("id", "");   //저장되어있는 id가져오기
                user_id=id; // php로 보낼 변수에 저장
                itop2.putExtra("pid",pid2);  //화분1의 설정 정보들을 읽기위한 구별값 전달
                startActivity(itop2);
                break;
            case R.id.btop3:            //화분3 상세 페이지
                handler.sendEmptyMessage(SEND_STOP);
                Intent itop3=new Intent(this,Detail.class);
                data = getSharedPreferences("data", MODE_PRIVATE);
                id = data.getString("id", "");   //저장되어있는 id가져오기
                user_id=id; // php로 보낼 변수에 저장
                itop3.putExtra("pid",pid3);  //화분1의 설정 정보들을 읽기위한 구별값 전달
                startActivity(itop3);
                break;
        }
    }

    @Override
    public void onBackPressed() {           //백버튼 2번으로 종료시키기
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime)
        {
            super.onBackPressed();
        }
        else
        {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "한번 더 뒤로가기 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

}
