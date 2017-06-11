package com.example.zzamtiger.textview;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class Setting extends AppCompatActivity implements OnClickListener{
    Button btsave=null;
    Button btcancel=null;
    ImageButton ibsttime;
    ImageButton ibsthumd;
    ImageButton ibstsun;
    EditText edname=null;

    TextView txsttimetitle=null;    //주기
    TextView txsttimecontent=null;
    TextView txsthumdtitle=null;    //습도
    TextView txsthumdcontent=null;
    TextView txstsuntitle=null;     //일조량
    TextView txstsuncontent=null;

    String pid="";      //화분의 고유번호
    String user_id="";  //유저 id
    String time = "";
    String sunshine = "";
    String humd = "";
    String name="";
    String cond="";

    int inttime = 0;
    int intsunshine = 10;
    int inthumd = 40;

    CharSequence[] items = {"주기","습도", "일조량"};
    CharSequence[] timeitems = {"1일","1주","1달","선택안함"};
    CharSequence[] humditems = {"70","60","50","40","30","20","10","5","선택안함"};
    CharSequence[] lightitems = {"30","20","15","10","선택안함"};

    AlertDialog alerttime;
    AlertDialog alerthumd;
    AlertDialog alertlight;

    String SUpdate = "http://teambutton.dothome.co.kr/SUpdate.php";    //설정 저장 php
    String Setting = "http://teambutton.dothome.co.kr/Setting.php";    //설정 불러오기 php


    public void gointent(String msg){
        if (msg.equals("successsuccess")) {
            //Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
            Intent itlogin = new Intent(this, Mainmenu.class);
            startActivity(itlogin);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    ArrayList<Setting.ListItem> listItem= new ArrayList<Setting.ListItem>();
    Setting.ListItem Item;

    public class ListItem {
        private String[] mData;
        public ListItem(String[] data ){
            mData = data;
        }
        public ListItem(String Name, String Humd, String Sunshine,String Waterlevel){
            mData = new String[4];
            mData[0] = Name;
            mData[1] = Humd;
            mData[2] = Sunshine;
            mData[3] = Waterlevel;
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
        setContentView(R.layout.activity_setting);

        SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);
        String id = data.getString("id", "");   //저장되어있는 id가져오기
        user_id=id; // php로 보낼 변수에 저장
        Intent intent=getIntent();
        pid= intent.getExtras().getString("pid");  //몇번째 화분인지 구별하여 해당 화분설정을 데이터베이스에서 꺼내옴

        ibsttime=(ImageButton) findViewById(R.id.ibsttime);
        ibsthumd=(ImageButton) findViewById(R.id.ibsthumd);
        ibstsun=(ImageButton) findViewById(R.id.ibstsun);
        btcancel=(Button)findViewById(R.id.btcancel);
        btsave=(Button)findViewById(R.id.btsave);
        edname = (EditText)findViewById(R.id.name);

        ibsttime.setOnClickListener(this);
        ibsthumd.setOnClickListener(this);
        ibstsun.setOnClickListener(this);
        btcancel.setOnClickListener(this);
        btsave.setOnClickListener(this);

        txsttimetitle=(TextView) findViewById(R.id.txsttimetitle);      //주기
        txsttimecontent=(TextView) findViewById(R.id.txsttimecontent);
        txsthumdtitle=(TextView) findViewById(R.id.txsthumdtitle);      //습도
        txsthumdcontent=(TextView) findViewById(R.id.txsthumdcontent);
        txstsuntitle=(TextView) findViewById(R.id.txstsuntitle);        //일조량
        txstsuncontent=(TextView) findViewById(R.id.txstsuncontent);


        AlertDialog.Builder dialogtime = new AlertDialog.Builder(this);
        dialogtime.setTitle("설정할 주기를 선택해주세요.");
        dialogtime.setItems(timeitems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                time=timeitems[item].toString();
                if(time.equals("1일"))
                {
                    inttime = 0;
                    txsttimecontent.setText(time+"에 한번씩 예약 물펌프 작동");
                    txsttimecontent.setTextColor(Color.BLACK);
                    txsttimetitle.setTextColor(Color.BLACK);
                }

                else if(time.equals("1주"))
                {
                    inttime = 1;
                    txsttimecontent.setText(time+"에 한번씩 예약 물펌프 작동");
                    txsttimecontent.setTextColor(Color.BLACK);
                    txsttimetitle.setTextColor(Color.BLACK);
                }
                else if (time.equals("1달"))
                {
                    inttime = 2;
                    txsttimecontent.setText(time+"에 한번씩 예약 물펌프 작동");
                    txsttimecontent.setTextColor(Color.BLACK);
                    txsttimetitle.setTextColor(Color.BLACK);
                }
                else if (time.equals("선택안함"))
                {
                    inttime = 3;
                    txsttimecontent.setText("설정안함");
                    txsttimecontent.setTextColor(Color.GRAY);
                    txsttimetitle.setTextColor(Color.GRAY);
                }

            }
        });
        alerttime = dialogtime.create();

        AlertDialog.Builder dialoghumd = new AlertDialog.Builder(this);
        dialoghumd.setTitle("설정할 습도를 선택해주세요.");
        dialoghumd.setItems(humditems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                humd=humditems[item].toString();
                if(humd.equals("선택안함"))
                {
                    inthumd=110;
                    txsthumdtitle.setTextColor(Color.GRAY);
                    txsthumdcontent.setText("설정안함");
                    txsthumdcontent.setTextColor(Color.GRAY);
                }
                else
                {
                    txsthumdcontent.setText("습도가"+humd+"%이하일때 물 펌프 작동");
                    txsthumdtitle.setTextColor(Color.BLACK);
                    txsthumdcontent.setTextColor(Color.BLACK);
                    inthumd=Integer.parseInt(humd);
                }

            }
        });
        alerthumd = dialoghumd.create();

        AlertDialog.Builder dialoglight = new AlertDialog.Builder(this);
        dialoglight.setTitle("설정할 일조량를 선택해주세요.");
        dialoglight.setItems(lightitems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                sunshine=lightitems[item].toString();
                if(sunshine.equals("선택안함"))
                {
                    intsunshine=110;
                    txstsuntitle.setTextColor(Color.GRAY);
                    txstsuncontent.setText("설정안함");
                    txstsuncontent.setTextColor(Color.GRAY);
                }
                else
                {
                    txstsuncontent.setText("일조량이"+sunshine+"%이하일때 Led 작동");
                    txstsuntitle.setTextColor(Color.BLACK);
                    txstsuncontent.setTextColor(Color.BLACK);
                    intsunshine=Integer.parseInt(sunshine);
                }

            }
        });
        alertlight = dialoglight.create();

        new Settingstate().execute();       //저장된 설정 불러오기




    }
    public void onClick(View arg0){
        switch(arg0.getId()){
            case R.id.ibsttime:
                alerttime.show();
                break;
            case R.id.ibsthumd:
                alerthumd.show();
                break;
            case R.id.ibstsun:
                alertlight.show();
                break;
            case R.id.btcancel:
                Intent itpluscancel=new Intent(this,Mainmenu.class);
                startActivity(itpluscancel);
                break;
            case R.id.btsave:
                name=edname.getText().toString();
                if (name != null && name.length() == 0)
                {
                    Toast toast = Toast.makeText(this, "화분이름은 최소 입력사항입니다.", Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                }
                new Save().execute();
                break;
        }
    }
    public class Settingstate extends AsyncTask<Void,Void,String> {     //설정되어있는 정보들 불러와서 화면에 나타내기
        StringBuilder jsonHtml = new StringBuilder();
        @Override
        protected String doInBackground(Void... params) {

            try {
                URL obj = new URL(Setting);

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

            String a;
            try{

                JSONObject root = new JSONObject(str);
                JSONArray ja = root.getJSONArray("results");
                for(int i=0; i<ja.length(); i++){
                    JSONObject jo = ja.getJSONObject(i);
                    Name = jo.getString("Name");
                    Humd = jo.getString("Humd");
                    Sunshine = jo.getString("Sunshine");
                    Timer = jo.getString("Timer");
                    listItem.add(new Setting.ListItem(Name,Humd,Sunshine,Timer));
                }
                Name=listItem.get(0).getData(0);
                edname.setText(Name);

                Humd=listItem.get(0).getData(1);    //습도
                inthumd=Integer.parseInt(Humd);
                if(inthumd==110)    //설정 안함이었을경우
                {
                    txsthumdcontent.setText("설정안함");
                    txsthumdcontent.setTextColor(Color.GRAY);
                    txsthumdtitle.setTextColor(Color.GRAY);
                }
                else
                {
                    txsthumdcontent.setText("습도가"+Humd+"%이하일때 물 펌프 작동");
                    txsthumdcontent.setTextColor(Color.BLACK);
                    txsthumdtitle.setTextColor(Color.BLACK);
                }


                Sunshine=listItem.get(0).getData(2);    //일조량
                intsunshine=Integer.parseInt(Sunshine);
                if(intsunshine==110)    //설정 안함이었을경우
                {
                    txstsuncontent.setText("설정안함");
                    txstsuntitle.setTextColor(Color.GRAY);
                    txstsuncontent.setTextColor(Color.GRAY);
                }
                else
                {
                    txstsuncontent.setText("일조량이"+Sunshine+"%이하일때 Led 작동");
                    txstsuntitle.setTextColor(Color.BLACK);
                    txstsuncontent.setTextColor(Color.BLACK);
                }


                Timer=listItem.get(0).getData(3);    //주기
                if(Timer.equals("0"))
                {
                    Timer="일";
                    inttime = 0;
                    txsttimecontent.setText("1"+Timer+"에 한번씩 예약 물펌프 작동");
                    txsttimetitle.setTextColor(Color.BLACK);
                    txsttimecontent.setTextColor(Color.BLACK);
                }
                else if(Timer.equals("1"))
                {
                    Timer="주";
                    inttime = 1;
                    txsttimecontent.setText("1"+Timer+"에 한번씩 예약 물펌프 작동");
                    txsttimetitle.setTextColor(Color.BLACK);
                    txsttimecontent.setTextColor(Color.BLACK);
                }
                else if(Timer.equals("2"))
                {
                    Timer="달";
                    inttime = 2;
                    txsttimecontent.setText("1"+Timer+"에 한번씩 예약 물펌프 작동");
                    txsttimetitle.setTextColor(Color.BLACK);
                    txsttimecontent.setTextColor(Color.BLACK);
                }
                else
                {
                    Timer="선택안함";
                    inttime = 3;
                    txsttimecontent.setText("설정안함");
                    txsttimetitle.setTextColor(Color.GRAY);
                    txsttimecontent.setTextColor(Color.GRAY);
                }

            }catch(JSONException e){
                e.printStackTrace();
            }
        }
    }
    public class Save extends AsyncTask<Void,Void,String> {    //화분 설정 갱신

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL obj = new URL(SUpdate);

                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setRequestMethod("POST"); //요청 방식을 설정 (default : GET)

                conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)

                conn.setConnectTimeout(10000);
                conn.setUseCaches(false);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(
                        "&pid="+pid+ "&name="+name +"&timer="+inttime +"&humd="+inthumd +"&sunshine="+intsunshine
                ); //요청 파라미터를 입력
                writer.flush();
                writer.close();
                os.close();


                conn.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8")); //캐릭터셋 설정

                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    if(sb.length() > 0) {
                        sb.append("\n");
                    }
                    sb.append(line);
                }
                br.close();
                conn.disconnect();

                return sb.toString().trim();

            }catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }


        @Override
        protected void onPostExecute(String result){
            gointent(result);

        }
    }

}
