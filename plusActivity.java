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


import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;


public class plusActivity extends AppCompatActivity implements OnClickListener{
    Button btplussave=null;
    Button btpluscancel=null;
    EditText edname=null;

    TextView txtimetitle=null;  //주기
    TextView txhumdtitle=null;  //습도
    TextView txsunshinetitle=null;  //일조량
    TextView txtimecontent=null;    //주기 내용
    TextView txhumdcontent=null;    //습도 내용
    TextView txplsuncontent=null;    //일조량 내용

    String user_id="";  //유저 id
    String time = "";
    String sunshine = "";
    String humd = "";
    String name="";
    String cond="";
    Date date;
    String strDate="";

    ImageButton ibpltime;
    ImageButton ibplhumd;
    ImageButton ibplsun;


    int inttime = 3;
    int intsunshine = 110;
    int inthumd = 110;

    CharSequence[] items = {"주기","습도", "일조량"};
    CharSequence[] timeitems = {"1일","1주","1달","선택안함"};
    CharSequence[] humditems = {"70","60","50","40","30","20","10","5","선택안함"};
    CharSequence[] lightitems = {"30","20","15","10","선택안함"};

    AlertDialog alerttime;
    AlertDialog alerthumd;
    AlertDialog alertlight;

    String urlpath = "http://teambutton.dothome.co.kr/SJoin.php";


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plus);

        SharedPreferences data = getSharedPreferences("data", MODE_PRIVATE);
        String id = data.getString("id", "");   //저장되어있는 id가져오기
        user_id=id; // php로 보낼 변수에 저장

        btpluscancel=(Button)findViewById(R.id.btpluscancel);
        btplussave=(Button)findViewById(R.id.btplussave);

        ibpltime=(ImageButton) findViewById(R.id.ibpltime);
        ibplhumd=(ImageButton) findViewById(R.id.ibplhumd);
        ibplsun=(ImageButton) findViewById(R.id.ibplsun);
        ibpltime.setOnClickListener(this);
        ibplhumd.setOnClickListener(this);
        ibplsun.setOnClickListener(this);

        edname = (EditText)findViewById(R.id.name);

        btpluscancel.setOnClickListener(this);
        btplussave.setOnClickListener(this);

        txtimetitle=(TextView) findViewById(R.id.txpltimetitle);
        txtimecontent=(TextView) findViewById(R.id.txpltimecontent);
        txhumdtitle=(TextView) findViewById(R.id.txplhumdtitle);
        txhumdcontent=(TextView) findViewById(R.id.txhumdcontent);
        txsunshinetitle=(TextView) findViewById(R.id.txplsuntitle);
        txplsuncontent=(TextView) findViewById(R.id.txplsuncontent);


        AlertDialog.Builder dialogtime = new AlertDialog.Builder(this);
        dialogtime.setTitle("설정할 주기를 선택해주세요.");
        dialogtime.setItems(timeitems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                time=timeitems[item].toString();
                if(time.equals("1일"))
                {
                    inttime = 0;
                    txtimetitle.setTextColor(Color.BLACK);
                    txtimecontent.setText(time+"에 한번씩 예약 물펌프 작동");
                    txtimecontent.setTextColor(Color.BLACK);
                }
                else if(time.equals("1주"))
                {
                    inttime=1;
                    txtimetitle.setTextColor(Color.BLACK);
                    txtimecontent.setText(time+"에 한번씩 예약 물펌프 작동");
                    txtimecontent.setTextColor(Color.BLACK);
                }
                else if (time.equals("1달"))
                {
                    inttime=2;
                    txtimetitle.setTextColor(Color.BLACK);
                    txtimecontent.setText(time+"에 한번씩 예약 물펌프 작동");
                    txtimecontent.setTextColor(Color.BLACK);
                }

                else if (time.equals("선택안함"))
                {
                    inttime=3;
                    txtimetitle.setTextColor(Color.GRAY);
                    txtimecontent.setText("설정안함");
                    txtimecontent.setTextColor(Color.GRAY);
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
                    txhumdtitle.setTextColor(Color.GRAY);
                    txhumdcontent.setText("설정안함");
                    txhumdcontent.setTextColor(Color.GRAY);
                }
                else
                {
                    txhumdcontent.setText("습도가"+humd+"%이하일때 물 펌프 작동");
                    txhumdtitle.setTextColor(Color.BLACK);
                    txhumdcontent.setTextColor(Color.BLACK);
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
                    txplsuncontent.setText("설정안함");
                    txsunshinetitle.setTextColor(Color.GRAY);
                    txplsuncontent.setTextColor(Color.GRAY);
                    intsunshine=110;
                }
                else
                {
                    txplsuncontent.setText("일조량이"+sunshine+"%이하일때 Led 작동");
                    txsunshinetitle.setTextColor(Color.BLACK);
                    txplsuncontent.setTextColor(Color.BLACK);
                    intsunshine=Integer.parseInt(sunshine);
                }
            }
        });
        alertlight = dialoglight.create();

    }
    public void onClick(View arg0){
        switch(arg0.getId()){
            case R.id.ibpltime:
                alerttime.show();
                break;
            case R.id.ibplhumd:
                alerthumd.show();
                break;
            case R.id.ibplsun:
                alertlight.show();
                break;
            case R.id.btpluscancel:
                Intent itpluscancel=new Intent(this,Mainmenu.class);
                startActivity(itpluscancel);
                break;
            case R.id.btplussave:
                name=edname.getText().toString();
                if (name != null && name.length() == 0)
                {
                    Toast toast = Toast.makeText(this, "화분이름은 최소 입력사항입니다.", Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                }
                long now = System.currentTimeMillis();  //현재 시간 구하기
                date = new Date(now);                  //현재 시간 date형으로 변환
                SimpleDateFormat dateFormat = new  SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
                Date date = new Date();
                strDate = dateFormat.format(date);
                new Plus().execute();
                break;
        }
    }

    public class Plus extends AsyncTask<Void,Void,String> {     //사용자가 선택한 조건에 따른 식물 추가

        @Override
        protected String doInBackground(Void... params) {

            try {
                URL obj = new URL(urlpath);

                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setRequestMethod("POST"); //요청 방식을 설정 (default : GET)

                conn.setDoInput(true); //input을 사용하도록 설정 (default : true)
                conn.setDoOutput(true); //output을 사용하도록 설정 (default : false)

                conn.setConnectTimeout(10000);
                conn.setUseCaches(false);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(
                        "&user_id=" +user_id +"&name="+name +"&timer="+inttime +"&humd="+inthumd +"&sunshine="+intsunshine
                                +"&date="+strDate
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
