package com.example.zzamtiger.textview;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Detail extends AppCompatActivity implements View.OnClickListener{
    AlertDialog.Builder builder;
    SharedPreferences data;
    Button btdetail=null;
    Button btback=null;
    ImageButton btwater=null;
    ImageButton btlight=null;
    Button btdelete=null;

    TextView txdtname=null;  //식물 이름
    TextView txdtcreat=null;  //생성 날짜
    TextView txdtwater=null;  //마지막 물펌프 작동 시간
    TextView txdtled=null;  //마지막 LED 작동 시간
    TextView txdtground=null;  //현재 토양 습도
    TextView txdtsunshine=null;  //현재 일조량

    String waterdate;        //물펌프가 작동된 시간
    Date theDatewater;       //물펌프가 작동된 시간
    String leddate;        //led램프가 작동된 시간
    Date theDateled;       //led램프가 작동된 시간

    String w=null;  //물주기 버튼 활성화 여부 조사변수
    String l=null;  //led켜기 버튼 활성화 여부 조사변수
    long now;
    Date date;

    String pid="";      //화분의 고유번호
    String user_id=null;
    String id=null;
    String Detail = "http://teambutton.dothome.co.kr/Detail.php";    //설정 불러오기 php
    String Remotewater = "http://teambutton.dothome.co.kr/Remotewater.php";  //화분들 물주기
    String Remotelight = "http://teambutton.dothome.co.kr/Remotelight.php";  //화분들 조명주기
    String Remove = "http://teambutton.dothome.co.kr/Remove.php";  //화분제거

    ArrayList<Detail.ListItem> listItem= new ArrayList<Detail.ListItem>();
    Detail.ListItem Item;

    public class ListItem {
        private String[] mData;
        public ListItem(String[] data ){
            mData = data;
        }
        public ListItem(String Name, String Humd, String Sunshine,String Waterlevel,String Rwater,String Rlight,String Cdate,String Waterdate,String Lampdate){
            mData = new String[9];
            mData[0] = Name;
            mData[1] = Humd;
            mData[2] = Sunshine;
            mData[3] = Waterlevel;
            mData[4] = Rwater;
            mData[5] = Rlight;
            mData[6] = Cdate;
            mData[7] = Waterdate;
            mData[8] = Lampdate;
        }
        public String[] getData(){
            return mData;
        }
        public String getData(int index){
            return mData[index];
        }
        public void setData(String[] data){
            mData = data;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this
        Intent intent=getIntent();
        pid= intent.getExtras().getString("pid");  //몇번째 화분인지 구별하여 해당 화분설정을 데이터베이스에서 꺼내옴

        txdtname=(TextView) findViewById(R.id.txdtname);
        txdtcreat=(TextView) findViewById(R.id.txdtcreat);
        txdtwater=(TextView) findViewById(R.id.txdtwater);
        txdtled=(TextView) findViewById(R.id.txdtled);
        txdtground=(TextView) findViewById(R.id.txdtground);
        txdtsunshine=(TextView) findViewById(R.id.txdtsunshine);


        btdetail=(Button)findViewById(R.id.btdetail);
        btback=(Button)findViewById(R.id.btback);
        btwater=(ImageButton) findViewById(R.id.btwater);
        btlight=(ImageButton)findViewById(R.id.btlight);
        btdelete=(Button)findViewById(R.id.btdelete);

        btdetail.setOnClickListener(this);
        btback.setOnClickListener(this);
        btwater.setOnClickListener(this);
        btlight.setOnClickListener(this);
        btdelete.setOnClickListener(this);


        data = getSharedPreferences("data", MODE_PRIVATE);
        id = data.getString("id", "");   //저장되어있는 id가져오기
        user_id=id; // php로 보낼 변수에 저장



        new Detailstate().execute();       //저장된 설정 불러오기


    }
    public void onClick(View arg0) {
        switch (arg0.getId()) {
            case R.id.btdetail:
                Intent itop=new Intent(this,Setting.class);
                itop.putExtra("pid",pid);  //화분1의 설정 정보들을 읽기위한 구별값 전달
                startActivity(itop);
                break;

            case R.id.btback:
                Intent itback = new Intent(this, Mainmenu.class);
                startActivity(itback);
                break;

            case R.id.btwater:
                if(w.equals("2")) {     //물주기 버튼이 꺼져있다면
                    btwater.setImageResource(R.drawable.water); //활성화
                    w="1";
                }
                else {      //활성화 되어 있다면
                    btwater.setImageResource(R.drawable.nwater);//끄기
                    w="2";
                }
                new Remotewater().execute();      //원격 물주기
                break;

            case R.id.btlight:
                if(l.equals("2")) {     //led버튼이 꺼져있다면
                    btlight.setImageResource(R.drawable.lamp);  //활성화
                    l="1";
                }
                else {                  //아니라면
                    btlight.setImageResource(R.drawable.nlamp); //끄기
                    l="2";
                }
                new Remotelight().execute();      //원격 LED 켜기
                break;

            case R.id.btdelete:
                builder.setTitle("화분 제거 확인")        // 제목 설정
                        .setMessage("정말 이 화분을 제거하시겠습니까?")        // 메세지 설정
                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                        .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                            // 확인 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton){
                                new Remove().execute();      //원격 제거하기
                            }
                        })
                        .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                            // 취소 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton){
                                dialog.cancel();
                            }
                        });
                AlertDialog dialog = builder.create();    // 알림창 객체 생성
                dialog.show();    // 알림창 띄우기
                break;
        }
    }
    public class Detailstate extends AsyncTask<Void,Void,String> {     //설정되어있는 정보들 불러와서 화면에 나타내기
        StringBuilder jsonHtml = new StringBuilder();
        @Override
        protected String doInBackground(Void... params) {

            try {
                URL obj = new URL(Detail);

                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                if(conn != null) {      // 연결되었음 코드가 리턴되면.
                    conn.setRequestMethod("POST"); //요청 방식을 설정 (default : GET)
                    conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                    conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)

                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(
                            "&pid=" +pid
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
            }catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
            return jsonHtml.toString();
        }

        @Override
        protected void onPostExecute(String str){       //json 가지고 작업하기
            String Timer;        //화분 수위
            String Name;        //화분 이름
            String Humd;        //화분 습도
            String Sunshine;    //화분 일조량
            String Rwater;      //화분에 물을 주고 있는지
            String Rlight;      //화분에 led를 키고 있는지
            String Cdate;      //화분의 생성 날짜
            String Waterdate;      //마지막으로 물펌프가 작동한 시간
            String Lampdate;      //마지막으로 LED램프가 작동한 시간
            try{

                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                for(int i=0; i<ja.length(); i++){
                    JSONObject jo = ja.getJSONObject(i);
                    Name = jo.getString("Name");
                    Humd = jo.getString("Humd");
                    Sunshine = jo.getString("Sunshine");
                    Timer = jo.getString("Timer");
                    Rwater = jo.getString("Rwater");
                    Rlight = jo.getString("Rlight");
                    Cdate = jo.getString("Cdate");
                    Waterdate= jo.getString("Waterdate");
                    Lampdate= jo.getString("Lampdate");
                    listItem.add(new Detail.ListItem(Name,Humd,Sunshine,Timer,Rwater,Rlight,Cdate,Waterdate,Lampdate));
                }
                Name=listItem.get(0).getData(0);
                txdtname.setText(Name);

                Humd=listItem.get(0).getData(1);
                txdtground.setText("토양습도:"+Humd+"%");

                Sunshine=listItem.get(0).getData(2);
                txdtsunshine.setText("일조량:"+Sunshine+"%");

                Rwater=listItem.get(0).getData(4);    //물
                if(Rwater.equals("2")) {            //물주기 버튼이 비활성화 되어있다면
                    btwater.setImageResource(R.drawable.nwater);    //꺼져있는 그림으로 설정
                    w=Rwater;
                }
                else {
                    btwater.setImageResource(R.drawable.water);
                    w=Rwater;
                }

                Rlight=listItem.get(0).getData(5);    //led
                if(Rlight.equals("2")) {        //led 버튼이 비활성화 되어있다면
                    btlight.setImageResource(R.drawable.nlamp); //꺼져있는 그림으로 설정
                    l=Rlight;
                }
                else {
                    btlight.setImageResource(R.drawable.lamp);
                    l=Rlight;
                }

                Cdate=listItem.get(0).getData(6);    //생성날짜
                txdtcreat.setText(Cdate);

                now = System.currentTimeMillis();  //현재 시간 구하기
                date = new Date(now);                  //현재 시간 date형으로 변환
                Waterdate=listItem.get(0).getData(7);               //마지막으로 물펌프가 작동한 시간 구하기
                try {
                    SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    theDatewater = transFormat.parse(Waterdate);
                    long waterduration = (date.getTime() - theDatewater.getTime())/1000/60/60; //마지막으로 물펌프를 작동시킨 시간을 시간단위로 계산
                    if(waterduration>400000)//애초에 저장된 적이 없는경우
                    {
                        txdtwater.setText("아직 작동한 적이 없습니다.");
                    }
                    else
                    {
                        if(waterduration<1)
                        {
                            waterduration = (date.getTime() - theDatewater.getTime())/1000/60;  //마지막 작동시간이 1시간이 안된다면 분으로 계산
                            String waterdu=String.valueOf(waterduration);
                            txdtwater.setText(waterdu+"분전");
                        }
                        else
                        {
                            String waterdu=String.valueOf(waterduration);
                            txdtwater.setText(waterdu+"시간전");
                        }

                    }
                } catch (ParseException e) {
                    //handle exception
                }

                Lampdate=listItem.get(0).getData(8);               //마지막으로 LED lamp가 작동한 시간 구하기
                try {
                    SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    theDateled = transFormat.parse(Lampdate);
                    long ledduration = (date.getTime() - theDateled.getTime())/1000/60/60; //마지막으로 LED를 작동시킨 시간을 시간단위로 계산
                    if(ledduration>400000)      //애초에 저장된 적이 없는경우
                    {
                        txdtled.setText("아직 작동한 적이 없습니다.");
                    }
                    else
                    {
                        if(ledduration<1)
                        {
                            ledduration = (date.getTime() - theDateled.getTime())/1000/60;  //마지막 작동시간이 1시간이 안된다면 분으로 계산
                            String leddu=String.valueOf(ledduration);
                            txdtled.setText(leddu+"분전");
                        }
                        else
                        {
                            String leddu=String.valueOf(ledduration);
                            txdtled.setText(leddu+"시간전");
                        }
                    }
                } catch (ParseException e) {
                    //handle exception
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

    public class Remotewater extends AsyncTask<Void,Void,String> {     ////원격으로 물주기 위한 클래스
        StringBuilder jsonHtml = new StringBuilder();
        @Override
        protected String doInBackground(Void... params) {

            try {
                URL obj = new URL(Remotewater);        //원격 물주기 php

                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                if(conn != null) {      // 연결되었음 코드가 리턴되면.
                    conn.setRequestMethod("POST"); //요청 방식을 설정 (default : GET)
                    conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                    conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)

                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(
                            "&pid=" +pid
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
            }catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
            return jsonHtml.toString();
        }

        @Override
        protected void onPostExecute(String str){

        }
    }
    public class Remotelight extends AsyncTask<Void,Void,String> {     ////원격으로 물주기 위한 클래스
        StringBuilder jsonHtml = new StringBuilder();
        @Override
        protected String doInBackground(Void... params) {

            try {
                URL obj = new URL(Remotelight);    //원격 조명php

                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                if(conn != null) {      // 연결되었음 코드가 리턴되면.
                    conn.setRequestMethod("POST"); //요청 방식을 설정 (default : GET)
                    conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                    conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)

                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(
                            "&pid=" +pid
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
            }catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
            return jsonHtml.toString();
        }

        @Override
        protected void onPostExecute(String str){

        }
    }

    public class Remove extends AsyncTask<Void,Void,String> {     ////화분 제거하기
        StringBuilder jsonHtml = new StringBuilder();
        @Override
        protected String doInBackground(Void... params) {

            try {
                URL obj = new URL(Remove);        //원격 물주기 php

                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                if(conn != null) {      // 연결되었음 코드가 리턴되면.
                    conn.setRequestMethod("POST"); //요청 방식을 설정 (default : GET)
                    conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                    conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)

                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);

                    OutputStream os = conn.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(
                            "&pid=" +pid
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
            }catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
            return jsonHtml.toString();
        }

        @Override
        protected void onPostExecute(String str){
            gointent2(str);
        }
    }
    public void gointent2(String msg){
        Intent itlogin = new Intent(this, Mainmenu.class);
        startActivity(itlogin);
        finish();
    }
}

