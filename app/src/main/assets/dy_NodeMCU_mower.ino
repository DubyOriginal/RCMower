/*
 *  This sketch demonstrates how to set up a simple HTTP-like server.
 *  The server will set a GPIO pin depending on the request
 *    http://server_ip/gpio/0 will set the GPIO2 low,
 *    http://server_ip/gpio/1 will set the GPIO2 high
 *  server_ip is the IP address of the ESP8266 module, will be 
 *  printed to Serial when the module is connected.
 */

#include <ESP8266WiFi.h>

//CONST
//***********************************************
const char* ssid = "SkyNet";
const char* password = "adidasneo";

const int FORWARD = 0;
const int BACK    = 1;
const int RLEFT   = 2;
const int RRIGHT  = 3;
const int STOP    = 4;

// motorA
int enA = D0;
int in1 = D1;
int in2 = D2;

// motorB
int in3 = D3;  
int in4 = D4;
int enB = D5;

//VAR
//***********************************************
int dSpeed = 120;        //initial value
int driveState = STOP;  //initial value

// Create an instance of the server, specify the port to listen on as an argument
WiFiServer server(80);

//*********************************************************************************************************
//*********************************************************************************************************
void setup() {
  Serial.begin(115200);
  delay(10);

  preparePINS();
  
  // Connect to WiFi network
  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  
  WiFi.begin(ssid, password);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");

  // print the received signal strength:
  long rssi = WiFi.RSSI();
  Serial.print("signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");
  
  // Start the server
  server.begin();
  Serial.println("Server started");

  // Print the IP address
  Serial.println(WiFi.localIP());
  Serial.println("**********************************************************************");

  //test();
 
}

//*********************************************************************************************************
//*********************************************************************************************************
void preparePINS(){
  //pinMode(2, OUTPUT);
  //digitalWrite(2, 0);

  // motorA
  pinMode(enA, OUTPUT);
  pinMode(in1, OUTPUT);
  pinMode(in2, OUTPUT);

  // motorB
  pinMode(enB, OUTPUT);
  pinMode(in3, OUTPUT);
  pinMode(in4, OUTPUT);

  stopMotors();
}

void test(){
  String req = "GET /cmd/speed/248 HTTP/1.1";
  Serial.print("req.length: ");
  Serial.println(req.length());

  Serial.print("req.indexOf(/cmd/speed/): ");
  Serial.println(req.indexOf("/cmd/speed/"));
  
  String result = req.substring(15, 18);
  Serial.print("result: ");
  Serial.println(result);
}

//DRIVE CONTROL
//*********************************************************************************************************
//*********************************************************************************************************
void driveFroward(){
  Serial.print("drive: FORWARD (");
  Serial.print(dSpeed);
  Serial.println(")");
  driveState = FORWARD;
 
  // turn on motor A
  digitalWrite(in1, HIGH);
  digitalWrite(in2, LOW);
  analogWrite(enA, dSpeed);

  // turn on motor B
  digitalWrite(in3, HIGH);
  digitalWrite(in4, LOW);
  analogWrite(enB, dSpeed);
}

//******************************************************************************
void driveBack(){
  Serial.print("drive: BACK (");
  Serial.print(dSpeed);
  Serial.println(")");
  driveState = BACK;
  
  // turn on motor A
  digitalWrite(in1, LOW);
  digitalWrite(in2, HIGH);
  analogWrite(enA, dSpeed);

  // turn on motor B
  digitalWrite(in3, LOW);
  digitalWrite(in4, HIGH);
  analogWrite(enB, dSpeed);
}

//******************************************************************************
void rotateLeft(){
  Serial.print("drive: RLEFT (");
  Serial.print(dSpeed);
  Serial.println(")");
  driveState = RLEFT;
  
  // turn on motor A
  digitalWrite(in1, LOW);
  digitalWrite(in2, HIGH);
  analogWrite(enA, dSpeed);

  // turn on motor B
  digitalWrite(in3, HIGH);
  digitalWrite(in4, LOW);
  analogWrite(enB, dSpeed);
}

//******************************************************************************
void rotateRight(){
  Serial.print("drive: RRIGHT (");
  Serial.print(dSpeed);
  Serial.println(")");
  driveState = RRIGHT;
  
  // turn on motor A
  digitalWrite(in1, HIGH);
  digitalWrite(in2, LOW);
  analogWrite(enA, dSpeed);

  // turn on motor B
  digitalWrite(in3, LOW);
  digitalWrite(in4, HIGH);
  analogWrite(enB, dSpeed);
}

//******************************************************************************
void stopMotors(){
  Serial.println("drive: STOP");
  driveState = STOP;
  
  digitalWrite(in1, LOW);
  digitalWrite(in2, LOW);
  digitalWrite(in3, LOW);
  digitalWrite(in4, LOW);

  analogWrite(enA, 0);
  analogWrite(enB, 0);
}

//******************************************************************************
void updateSpeed(){
    if (driveState = FORWARD){
        driveFroward();
    } else if (driveState = BACK){
        driveBack();
    } else if (driveState = RLEFT){
        rotateLeft();
    } else if (driveState = RRIGHT){
        rotateRight();
    }
}

//*********************************************************************************************************
//*********************************************************************************************************
void loop() {
  // Check if a client has connected
  WiFiClient client = server.available();
  if (!client) {
    //no client state is most of the time
    return;
  }
  
  // Wait until the client sends some data
  Serial.println("new client");
  while(!client.available()){
    delay(1);
  }

  //handleINRequest();
  //************************************************
  String req = client.readStringUntil('\r');    // Read the first line of the request
  Serial.println(req);
  client.flush();
  
  // Match the request
  if (req.indexOf("/cmd/forward") != -1){
    driveFroward();
  }else if (req.indexOf("/cmd/back") != -1){
    driveBack();
  }else if (req.indexOf("/cmd/rotateleft") != -1){
    rotateLeft();
  }else if (req.indexOf("/cmd/rotateright") != -1){
    rotateRight();
  }else if (req.indexOf("/cmd/stop") != -1){
    stopMotors();
  }else if (req.indexOf("/cmd/speed/") != -1){
    //req -> "GET /cmd/speed/248 HTTP/1.1"
    String val = req.substring(15,18);
    Serial.print("CMD/speed/");
    Serial.println(val);
    dSpeed = val.toInt();
    updateSpeed();
  }else {
    Serial.println("invalid request");
    client.stop();
    return;
  }
  
  client.flush();

  // Prepare the response
  //String s = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\n\r\n<!DOCTYPE HTML>\r\n<html>\r\nGPIO is now ";
  //s += (val)?"high":"low";
  //s += "</html>\n";

  //client.print("{\"location\":\"outdoor\",\"celsius\":\"");
  //client.print(temperatureOutdoor);
  //client.print("\"}]}");

  String response = "{\"status\":\"ok\", \"data\":\"123\"}";
  client.print(response);   // Send the response to the client
  delay(1);
  Serial.println("Client disonnected");

  // The client will actually be disconnected when the function returns and 'client' object is detroyed
}
