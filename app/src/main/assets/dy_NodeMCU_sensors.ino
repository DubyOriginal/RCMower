#include <ESP8266WiFi.h>

/*
D0   = 16;
D1   = 5;
D2   = 4;
D3   = 0;
D4   = 2;
D5   = 14;
D6   = 12;
D7   = 13;
D8   = 15;
D9   = 3;
D10  = 1;
*/

//CONST
//***********************************************
const char* ssid = "SkyNet";
const char* password = "adidasneo";

// Defines Tirg and Echo pins of the Ultrasonic Sensor
int echoPin = 4;  //gpio 4  -> D2
int trigPin = 5;  //gpio 5  -> D1

//VAR
//***********************************************
long duration;
int distance;

// Create an instance of the server, specify the port to listen on as an argument
WiFiServer server(80);

//*********************************************************************************************************
//*********************************************************************************************************
void setup(){
  Serial.begin(115200);
  while (!Serial);
  Serial.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
  Serial.println("MowerApp v1.1.5");

  pinMode(trigPin, OUTPUT);  // Sets the trigPin as an Output
  pinMode(echoPin, INPUT);   // Sets the echoPin as an Input

  delay(1000);

  // Connect to WiFi network
  Serial.print("Connecting to ");
  Serial.print(ssid);

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("connected");

  // print the received signal strength:
  long rssi = WiFi.RSSI();
  Serial.print("Signal strength (RSSI):");
  Serial.print(rssi);
  Serial.println(" dBm");

  // Start the server
  server.begin();
  Serial.println("Server started");

  // Print the IP address
  Serial.println(WiFi.localIP());
  Serial.println("---------------------------------------------------------------------");
}

//*********************************************************************************************************
//*********************************************************************************************************
String readHCSR04(){
    Serial.println("readHCSR04");

    digitalWrite(trigPin, LOW);
    delayMicroseconds(2);
    digitalWrite(trigPin, HIGH);
    delayMicroseconds(10);
    digitalWrite(trigPin, LOW);

    duration = pulseIn(echoPin, HIGH); // Reads the echoPin, returns the sound wave travel time in microseconds
    distance= duration*0.34/2;

    Serial.print("s = ");
    Serial.print(distance); // Sends the distance value into the Serial Port
    Serial.println(" mm");

    Serial.print("t = ");
    Serial.print(duration); // Sends the distance value into the Serial Port
    Serial.println(" us");

    String disStr = String(distance);
    return "\"sensor\":\"HC-SR04\",\"distance\":\"" + disStr;

}

//*********************************************************************************************************
String ledON(){
      digitalWrite(trigPin, HIGH);
      Serial.println("---------------------------------------");
      return "\"sensor\":\"LED\", \"status\":\"ON\"";
}

//*********************************************************************************************************
String ledOFF(){
      digitalWrite(trigPin, LOW);
      Serial.println("---------------------------------------");
      return "\"sensor\":\"LED\", \"status\":\"OFF\"";
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

  String resHeader = "{\"status\":\"ok\",";
  String resFotter = "\"}";
  String jsonResponse;

  // Match the request
  if (req.indexOf("/sensor/hcsr04") != -1){
    String distance = readHCSR04();
    jsonResponse = resHeader + distance + resFotter;
    //Serial.println(jsonResponse);

  }else if (req.indexOf("/led/on") != -1){
    String ledStatus = ledON();
    jsonResponse = resHeader + ledStatus + resFotter;
    Serial.println("/led/on");

  }else if (req.indexOf("/led/off") != -1){
    String ledStatus = ledOFF();
    jsonResponse = resHeader + ledStatus + resFotter;
    Serial.println("/led/off");

  }else if (req.indexOf("/sensor/all") != -1){
    Serial.println("/sensor/all");

  }else {
    Serial.println("invalid request");
    client.stop();
    return;
  }

  client.flush();   // clead out the input buffer:

  //jsonResponse: {"status":"ok","sensor":"HC-SR04","distance":"481"}

  // Prepare the response
  int jsonSize = jsonResponse.length();
  String httpResponse = "HTTP/1.1 200 OK\r\n";
  httpResponse += "Content-Type: application/json;\r\n";
  httpResponse += "Content-Length:" + String(jsonSize) + ";\r\n\r\n";  //NOTE - after header comes empty line
  httpResponse += jsonResponse + ";\r\n";
  client.print(httpResponse);   // Send the response to the client
  client.println();

  Serial.print(httpResponse);

  delay(1);
  Serial.println("Client disonnected");
  Serial.println("---------------------------------------");

  // The client will actually be disconnected when the function returns and 'client' object is detroyed
}