#include <SPI.h>
#include "WizFi250.h"
#include "DHT.h"
#define DHTPIN A3 // data pin to DHT22
#define DHTTYPE DHT22 // using DHT 22
#include <SoftwareSerial.h>  
#include <avr/pgmspace.h>

DHT dht(DHTPIN, DHTTYPE);
SoftwareSerial bluetooth(5,6);
int relay1 = 7; //릴레이1 핀설정
int relay2 = 8; //릴레이2 핀설정
int relay3 = 9; //릴레이3 핀설정
int relay4 = 10; //릴레이4 핀설정

unsigned long time1; //물어본 시간을 저장할 변수
unsigned long time2; //물어본 시간을 저장할 변수

unsigned long time4;//현재시간 저장할 변수


int soil = A0; // 토양 수분센서를 A0번 핀으로 설정합니다.
/*
            char ssid[] = "SK_WiFiAE05";    // your network SSID (name)
            char pass[] = "1601003607";          // your network password
*/
      /*      
char ssid[] = "abcd";    // your network SSID (name)
char pass[] = "qwer1234";          // your network password*/
int status = WL_IDLE_STATUS;       // the Wifi radio's status

char server[] = "teambutton.dothome.co.kr";

int h = 0;
int t = 0;
int cond = 0;
int temp = 0;
int w1 = 0;
int w2 = 0;
String tem = "";
int w, l;
int humd = 0;//analogRead(j); //토양습도 측정
int shine = 0;//analogRead(t);//조도측정
int index;
int w_cond = 1000;//물조건

int l_cond = 0;//빛조건
int n = 0;
unsigned long w_time = 100000000;
char *user;

String water;
String cmd;
String fcm1;
String fcm2;
// Initialize the Ethernet client object
WiFiClient client;

void printWifiStatus();
int bufferPosition = 0;
byte buffer[50];
void setup()
{
  Serial.begin(115200);
  bluetooth.begin(9600);


  delay(1000);
  
  while(!bluetooth.available());
  
  delay(2000);
  
 byte data;
  
  while(bluetooth.available())
  {
   data = bluetooth.read();
   buffer[bufferPosition++] = data;
  }
  bufferPosition--;
  if (data == '\n') 
  {
    buffer[bufferPosition++] = '&';
     buffer[bufferPosition] = '\0';
  } 
  bluetooth.end();
  //String myString = String((char *)buffer);

  char * ssid;
  char * pass;

  ssid = strtok((char*)buffer,"&");
  pass = strtok(NULL,"&");
  user = strtok(NULL,"&");
  
  //Serial.println((char*)ssid);
  //Serial.println((char*)pass);
  //Serial.println((char*)user);
  bluetooth.end(); // end of bluetooth softwareserial.
  
   dht.begin();
  WiFi.init();
 
  // check for the presence of the shield
  if (WiFi.status() == WL_NO_SHIELD) {
    //Serial.println("WiFi shield not present");
    // don't continue
    while (true);
  }

  // attempt to connect to WiFi network
  while (status != WL_CONNECTED) {
    //Serial.print("Attempting to connect to WPA SSID: ");
    //Serial.println(ssid);
    // Connect to WPA/WPA2 network
    status = WiFi.begin(ssid, pass);
  }

  // you're connected now, so print out the data
  //Serial.println("You're connected to the network");

  printWifiStatus();

  Serial.println();
  //Serial.println("Starting connection to server...");
  // if you get a connection, report back via serial

  pinMode(relay1, OUTPUT); // relay1를 output으로 설정한다.
  pinMode(relay2, OUTPUT); // relay2를 output으로 설정한다.
  pinMode(relay3, OUTPUT); // relay3를 output으로 설정한다.
  pinMode(relay4, OUTPUT); // relay4를 output으로 설정한다.
  digitalWrite(relay1, HIGH);
  digitalWrite(relay2, HIGH);
  digitalWrite(relay3, HIGH);
  digitalWrite(relay4, HIGH);
  time1 = millis(); //프로그램 시작 전에 시간을 저장한다.
  time2 = millis(); //프로그램 시작 전에 시간을 저장한다.

}



void loop()
{
  n = n % 2 + 1;
  String s = "";
  String p_name=user;
  p_name+=n;
  Serial.println(p_name);
  // if there are incoming bytes available
  // from the server, read them and print them
  temp = dht.readTemperature();;//온도측정
  w1 = digitalRead(A5);//위에꺼//수위측정1
  w2 = digitalRead(A4);//수위측정2
unsigned long *tm;
  if (n == 1)
  {
    h = A0;
    t = A2;
    relay2 = 8;
    fcm1 = "/Pushled1.php";
    fcm2 = "/Pushwater1.php";
    tm=&time1;
  }
  else
  {
    h = A1;
    t = A2;
    relay2 = 9;
    fcm1 = "/Pushled2.php";
    fcm2 = "/Pushwater2.php";
    tm=&time2;
  }
  // 측정된 값은 실제 수분량을 나타내는 것이 아니라, 0~1023 범위로 환산된 저항값을 의미합니다.
  humd = analogRead(h); //토양습도 측정
  shine = analogRead(t);//조도측정

  if (w1 == 1) {
    water = "sang";
  }
  else {
    if (w2 == 1) {
      water = "jung";
    }
    else {
      water = "ha";
    }
  }
  
  // 토양 수분센서로부터 측정된 값를 시리얼 모니터에 출력합니다.

  //Serial.println(temp);
  //Serial.print("Moisture sensor value : ");
   //Serial.println(humd);
  //Serial.print("suwi sensor value : ");
  //Serial.println(w1, DEC);
  //Serial.println(w2, DEC);
  //Serial.print("jodo sensor value : ");
   //Serial.println(shine);
  //Serial.print("suwi level : ");
  //Serial.println(water);
  // Make a HTTP request
  cmd = "GET /aduino.php?name=";
  cmd += p_name;
  cmd += "&temp=";
  cmd += temp;
  cmd += "&humd=";
  cmd += humd/10;
  cmd += "&shine=";
  cmd += shine/10;
  cmd += "&water=";
  cmd += water;
  cmd += " HTTP/1.1";

  //Serial.println(cmd);
  while (!client.connect(server, 80)) {

  }

  //Serial.println("Connected to server");
  // Make a HTTP request
  client.println(cmd);
  client.println(F("Host: teambutton.dothome.co.kr"));
  client.println(F("Connection: close"));
  client.println();
  for (; !client.available();) {}
  s = "";
  while (client.available()) {
    char c = client.read();
    s += c;
  }
  //Serial.print(s);

  if (s.indexOf("result") != -1) {
    tem = "";
    index = s.indexOf("result");
    while (s[index + 6] != '&') {
      tem += s[index + 6];
      index++;
    }

    //Serial.println();
    w = tem.toInt();
    //Serial.print("w=");
    //Serial.println(w);
    index++;
    tem = "";

    while (s[index + 6] != '&') {
      tem += s[index + 6];
      index++;
    }

    l = tem.toInt();
    //Serial.print("l=");
    //Serial.println(l);
    index++;
    tem = "";

    while (s[index + 6] != '&') {
      tem += s[index + 6];
      index++;
    }
    w_time = tem.toInt();

    //Serial.print("w_time=");
    //Serial.println(w_time);
    if (w_time == 0) {
      w_time = 30000;
    }
    else if (w_time == 1)
    {
      w_time = 40000;
    }
    else if (w_time == 2)
    {
      w_time = 50000;
    }
    index++;
    tem = "";

    while (s[index + 6] != '&') {
      tem += s[index + 6];
      index++;
    }
    w_cond = tem.toInt();
    //Serial.print("w_cond=");
    //Serial.println(w_cond);
    w_cond=w_cond*10;
    
    index++;
    tem = "";

    while (s[index + 6] != '&') {
      tem += s[index + 6];
      index++;
    }

    l_cond = tem.toInt();
    //Serial.print("l_cond=");
    //Serial.println(l_cond);
    l_cond=l_cond*10;
    // if the server's disconnected, stop the client
    if (!client.connected()) {
      //Serial.println();
      //Serial.println("Disconnecting from server...");
      client.stop();
    }

    if (l == 1) {
      //불키기
      //Serial.println("button light on");
      digitalWrite(relay4, LOW);
    }
    else {
      if(l_cond==1100){}
      else if (shine<l_cond) {
        //불킨다.
        digitalWrite(relay4, LOW);
        //Serial.println("condition light on");
        while (!client.connect(server, 80)) {

        }

        //Serial.println("Connected to server");
        // Make a HTTP request
        client.println("GET "+fcm1+"?user_id="+p_name+" HTTP/1.0");
        client.println(F("Host: teambutton.dothome.co.kr"));
        client.println(F("Connection: close"));
        client.println();
        for (; !client.available();) {}
        s = "";
        while (client.available()) {
          char c = client.read();
          s += c;
        }
        //Serial.print(s);

        
    // if the server's disconnected, stop the client
    if (!client.connected()) {
      //Serial.println();
      //Serial.println("Disconnecting from server...");
      client.stop();
    }
      }
      else if (shine>l_cond) {
        //불끈다
        digitalWrite(relay4, HIGH);
        //Serial.println("condition light off");
      }

    }

    if (w == 1) {
      //물주기
      digitalWrite(relay1, LOW);
      digitalWrite(relay2, LOW);
      //Serial.println("button water on");
      delay(5000);
      *tm = millis(); //현재 시간을 저장한다.
      digitalWrite(relay2, HIGH);
      digitalWrite(relay1, HIGH);
    }
    else {
      if(w_cond==1100){}
      else if (humd<w_cond) {
        while (!client.connect(server, 80)) {

        }

        //Serial.println("Connected to server");
        // Make a HTTP request
        client.println("GET "+fcm2+"?user_id="+p_name+" HTTP/1.0");
        client.println(F("Host: teambutton.dothome.co.kr"));
        client.println(F("Connection: close"));
        client.println();
        for (; !client.available();) {}
        s = "";
        while (client.available()) {
          char c = client.read();
          s += c;
        }
        //Serial.print(s);

        
    // if the server's disconnected, stop the client
    if (!client.connected()) {
      //Serial.println();
      //Serial.println("Disconnecting from server...");
      client.stop();
    }

        
       digitalWrite(relay1, LOW);
       digitalWrite(relay2, LOW);
        //Serial.println("condition water on");
        *tm = millis();
        delay(5000);
      digitalWrite(relay2, HIGH);
       digitalWrite(relay1, HIGH);
      }

      time4 = millis(); //현재 시간을 저장한다.
      if(w_time==3){}
      else if(time4 - *tm > w_time)
      {
         while (!client.connect(server, 80)) {

        }

        //Serial.println("Connected to server");
        // Make a HTTP request
        client.println("GET "+fcm2+"?user_id="+p_name+" HTTP/1.0");
        client.println(F("Host: teambutton.dothome.co.kr"));
        client.println(F("Connection: close"));
        client.println();
        for (; !client.available();) {}
        s = "";
        while (client.available()) {
          char c = client.read();
          s += c;
        }
        //Serial.print(s);

        
    // if the server's disconnected, stop the client
    if (!client.connected()) {
      //Serial.println();
      //Serial.println("Disconnecting from server...");
      client.stop();
    }
        //Serial.println("time water on");
        digitalWrite(relay1, LOW);
        digitalWrite(relay2, LOW);
        delay(5000);
        *tm = time4; //다음 비교를 위해 현재 시간을 저장한다.
        digitalWrite(relay1, HIGH);
        digitalWrite(relay2, HIGH);
      }
    }
  }
  delay(5000);

}
void printWifiStatus()
{
  // print the SSID of the network you're attached to
  //Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  // print your WiFi shield's IP address
  IPAddress ip = WiFi.localIP();
  //Serial.print("IP Address: ");
  Serial.println(ip);

  // print the received signal strength
  long rssi = WiFi.RSSI();
  //Serial.print("Signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
}


