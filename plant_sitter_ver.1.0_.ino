#include <SPI.h>
#include "WizFi250.h"


int relay1 = 8; //릴레이1 핀설정
int relay2 = 9; //릴레이2 핀설정
int relay3 = 10; //릴레이3 핀설정
int relay4 = 11; //릴레이4 핀설정

unsigned long time1; //물어본 시간을 저장할 변수
unsigned long time2; //물어본 시간을 저장할 변수
unsigned long time3; //물어본 시간을 저장할 변수

unsigned long time4;//현재시간 저장할 변수


int soil = A0; // 토양 수분센서를 A0번 핀으로 설정합니다.
int Liquid_level = 6; //수위 센서를 5번 핀으로 설정

char ssid[] = "blue4-76";    // your network SSID (name)
char pass[] = "12345753";          // your network password
int status = WL_IDLE_STATUS;       // the Wifi radio's status

char server[] = "teambutton.dothome.co.kr";

int i = 0;
int j = 0;
int cond = 0;
int temp = 0;
  int w1 = 0;
  int w2 = 0;

String s = "";
// Initialize the Ethernet client object
WiFiClient client;
void sendcmd(String cmd);
void Wifiinit();
void printWifiStatus();
void printanswer();
void work(String n, int j, int t,unsigned long *tm,int valve);
void setup()
{
  Serial.begin(115200);

  Wifiinit();
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
  time3 = millis(); //프로그램 시작 전에 시간을 저장한다.
}



void loop()
{
  temp = 0;//온도측정
  w1 = 0;//digitalRead(Liquid_level);//위에꺼//수위측정1
  w2 = 0;//수위측정2
  work("a1", A0, A1,&time1,8);
  work("a2",A2,A3,&time2,9);
  //work("a3",A4,A5,&time3);
  delay(6000000);
}
void work(String n, int j, int t,unsigned long *tm,int valve) {
  Serial.print(n);
  Serial.println("work start");
  // 측정된 값은 실제 수분량을 나타내는 것이 아니라, 0~1023 범위로 환산된 저항값을 의미합니다.


  // 토양 수분센서로부터 측정된 값를 시리얼 모니터에 출력합니다.
  String tem = "";
  int w, l;
  int humd = 0;//analogRead(j); //토양습도 측정
  int shine = 0;//analogRead(t);//조도측정
  int index;
  int w_cond = 1000;//물조건
  int l_cond = 0;//빛조건
  unsigned long w_time = 100000000;
  
  String water;
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
  Serial.print("Moisture sensor value : ");
  Serial.println(humd);
  Serial.print("suwi sensor value : ");
  Serial.println(w1, DEC);
  Serial.print("jodo sensor value : ");
  Serial.println(shine);
  Serial.print("suwi level : ");
  Serial.println(water);
  // Make a HTTP request

  String cmd = "GET http://teambutton.dothome.co.kr/aduino.php?name=a1&temp=";
  cmd += temp;
  cmd += "&humd=";
  cmd += humd;
  cmd += "&shine=";
  cmd += shine;
  cmd += "&water=";
  cmd += water;
  cmd += "\n\r";

  sendcmd(cmd);
  Serial.println(cmd);


  if (s.indexOf("result") != -1) {
    index = s.indexOf("result");
    while (s[index + 6] != '&') {
      tem += s[index + 6];
      index++;
    }

    w = tem.toInt();
    Serial.print("w=");
    Serial.println(w);
    index++;
    tem = "";

    while (s[index + 6] != '&') {
      tem += s[index + 6];
      index++;
    }

    l = tem.toInt();
    Serial.print("l=");
    Serial.println(l);
    index++;
    tem = "";

    while (s[index + 6] != '&') {
      tem += s[index + 6];
      index++;
    }
    Serial.println(tem);

    w_time = tem.toInt();
    Serial.println(w_time);
    Serial.print("w_time=");
    Serial.println(w_time);
    if (w_time == 1) {
      w_time = 86400000;
    }
    else if (w_time == 2)
    {
      w_time = 86400000 * 7;
    }
    else if (w_time == 3)
    {
      w_time = 86400000 * 30;
    }
    index++;
    tem = "";

    while (s[index + 6] != '&') {
      tem += s[index + 6];
      index++;
    }
    Serial.println(tem);
    w_cond = tem.toInt();
    Serial.print("w_cond=");
    Serial.println(w_cond);


    index++;
    tem = "";

    while (s[index + 6] != '&') {
      tem += s[index + 6];
      index++;
    }
    Serial.println(tem);
    l_cond = tem.toInt();
    Serial.print("l_cond=");
    Serial.println(l_cond);

  }

  if (l == 1) {
    //불키기
    Serial.println("버튼 불키기");
    digitalWrite(relay4, LOW);
  }
  else{
     if (shine<l_cond) {
    //불킨다.
    digitalWrite(relay4, LOW);
    Serial.println("조건 불키기");

   }
   if (shine>l_cond) {
     //불끈다
     digitalWrite(relay4, HIGH);
     Serial.println("조건 불끄기");
   }
  }
  if (w == 1) {
    //물주기
    digitalWrite(relay1, LOW);
    digitalWrite(valve, LOW);
    Serial.println("버튼 물주기");
    delay(5000);
    *tm = millis(); //현재 시간을 저장한다.
    digitalWrite(relay1, HIGH);
    digitalWrite(valve, HIGH);
  }
  else{
   if (humd<w_cond) {
    digitalWrite(relay1, LOW);
    digitalWrite(valve, LOW);
    Serial.println("조건 물주기");
    *tm = millis();
    delay(5000);
    digitalWrite(valve, HIGH);
    digitalWrite(relay1, HIGH);
  }


  time4 = millis(); //현재 시간을 저장한다.
  
  if (time4 - *tm > w_time)
  {
    Serial.println("시간 물주기");
    digitalWrite(relay1, LOW);
    digitalWrite(valve, LOW);
    delay(5000);
    *tm = time4; //다음 비교를 위해 현재 시간을 저장한다.
    digitalWrite(relay1, HIGH);
    digitalWrite(valve, HIGH);
  }
  }

 


}
void sendcmd(String cmd) {

  // if you get a connection, report back via serial
  //
  while (!client.connect(server, 80)) {
      
    }

    Serial.println("Connected to server");
    // Make a HTTP request
    client.println(cmd);
    client.println("Host: teambutton.dothome.co.kr");
    client.println("Connection: close");
    client.println();
    // if there are incoming bytes available
    // from the server, read them and print them
    printanswer();

  if (!client.connected()) {
    Serial.println();
    Serial.println("Disconnecting from server...");
    client.stop();

  }

}
void Wifiinit() {
  WiFi.init();

  // check for the presence of the shield
  if (WiFi.status() == WL_NO_SHIELD) {
    Serial.println("WiFi shield not present");
    // don't continue
    while (true);
  }

  // attempt to connect to WiFi network
  while (status != WL_CONNECTED) {
    Serial.print("Attempting to connect to WPA SSID: ");
    Serial.println(ssid);
    // Connect to WPA/WPA2 network
    status = WiFi.begin(ssid, pass);
  }

  // you're connected now, so print out the data
  Serial.println("You're connected to the network");

  printWifiStatus();

  Serial.println();
  Serial.println("Starting connection to server...");

}
void printanswer() {
  char c;
  s = "";
  for (; !client.available();) {}
  while (client.available()) {
    c = client.read();
    Serial.write(c);
    s += c;
  }
  Serial.println();
}

void printWifiStatus()
{
  // print the SSID of the network you're attached to
  Serial.print("SSID: ");
  Serial.println(WiFi.SSID());

  // print your WiFi shield's IP address
  IPAddress ip = WiFi.localIP();
  Serial.print("IP Address: ");
  Serial.println(ip);

  // print the received signal strength
  long rssi = WiFi.RSSI();
  Serial.print("Signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
}

