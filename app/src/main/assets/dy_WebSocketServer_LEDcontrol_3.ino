/*
 * WebSocketServer_LEDcontrol.ino
 *
 *  Created on: 26.11.2015
 *
 */

#include <Arduino.h>

#include <ESP8266WiFi.h>
#include <ESP8266WiFiMulti.h>
#include <WebSocketsServer.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>
#include <Hash.h>
#include "BasicUtils.h"

//DEFINE PINS
//****************************************
#define LED_RED     15
#define LED_GREEN   12
#define LED_BLUE    13 

int analogPin = A0;  //gpio 16  -> A0

// Ultrasonic Sensor
int echoPin = 4;  //gpio 4  -> D2
int trigPin = 5;  //gpio 5  -> D1

//VAR
//***********************************************
long duration;
int distance;
unsigned long startT = 0;

//****************************************
#define serial Serial

//WiFi
const char *ssid = "MowerNet";
const char *password = "mower123";

//ESP8266WebServer server = ESP8266WebServer(80);
WebSocketsServer webSocket = WebSocketsServer(81);

void webSocketEvent(uint8_t num, WStype_t type, uint8_t * payload, size_t sizeT) {
    serial.println("webSocketEvent");
    serial.println("-------------------S-------------------");
    //serial.printf("num: %d\n", num);         //0
    //serial.printf("type: %d\n", type);       //2, 3,...
    //serial.printf("payload: %s\n", payload);   //text
    //serial.printf("sizeT: %u\n", sizeT);     //text length
    
    switch(type) {
        case WStype_DISCONNECTED:
            serial.printf("[%u] Disconnected...!\n", num);
            break;
        case WStype_CONNECTED: {
            IPAddress ip = webSocket.remoteIP(num);
            serial.printf("[%u] Connected from %d.%d.%d.%d url: %s\n", num, ip[0], ip[1], ip[2], ip[3], payload);

            // send message to client
            webSocket.sendTXT(num, "Connected");
        }
            break;
        case WStype_TEXT: {
            //serial.printf("[%u] get Text: %s\n", num, payload);

            //webSocket.sendTXT(num, payload, lenght);     // echo data back to browser
            //webSocket.broadcastTXT("1234567890123456789012345678901234567890", 30);   // send data to all connected clients

            String msgCode = String((char *) &payload[0]);
            String resultStr = handleWSEventForCode(msgCode);

            webSocket.sendTXT(num, resultStr);     // echo data back
            //webSocket.broadcastTXT(resultStr); // send data to all connected clients
        }
            break;
        case WStype_BIN:
            serial.printf("[%u] get binary length: %u\n", num, sizeT);
            hexdump(payload, sizeT);
            // echo data back to browser
            webSocket.sendBIN(num, payload, sizeT);
            break;
        case WStype_ERROR:
            serial.println("webSocketEvent - ERROR");
            break;
        default:
          serial.println("webSocketEvent - UNKNOWN");
    }
    serial.println("-------------------E-------------------");
}

//*********************************************************************************************************
String handleWSEventForCode(String msgCode){
  serial.println("handleWSEventForCode - " + msgCode);
  String resultStr = "";
  if (msgCode=="CMD_LED"){
    serial.println("execute LED event");
    resultStr = "RES_LED";
    
  }else if (msgCode == "CMD_ANALOG"){
    serial.println("execute ANALOG event");
    String analogValue = readAnalog();
    resultStr = analogValue;

  }else if (msgCode == "CMD_HCSR04"){
    serial.println("execute CMD_HCSR04 event");
    String sensorValue = readHCSR04();
    resultStr = sensorValue;

  }else {
    serial.println("unknown message code");
  }
  return resultStr;
}

//potenciometer
//*******************************************************
String readAnalog() {
  Serial.print("readAnalog -> ");
  int analog = analogRead(analogPin);    // read the input pin
  Serial.println("AN = " + analog);
  String analogStr = String(analog);
  return "RES_ANALOG," + analogStr;
}

//UltraSound sensor
//*******************************************************
String readHCSR04() {
  Serial.print("readHCSR04 -> ");

  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  duration = pulseIn(echoPin, HIGH); // Reads the echoPin, returns the sound wave travel time in microseconds
  distance = duration * 0.34 / 2;

  Serial.println("s = " + String(distance) + " mm");

  Serial.print(" (t = ");
  Serial.print(duration); // Sends the distance value into the Serial Port
  Serial.println(" us)");

  String disStr = String(distance);
  return "RES_HCSR04," + disStr;

}

//*********************************************************************************************************
void preparePINS(){
    pinMode(analogPin, INPUT);   // Sets the analogPin as an Input

    pinMode(trigPin, OUTPUT);  // Sets the trigPin as an Output
    pinMode(echoPin, INPUT);   // Sets the echoPin as an Input
  
    pinMode(LED_RED, OUTPUT);
    pinMode(LED_GREEN, OUTPUT);
    pinMode(LED_BLUE, OUTPUT);

    digitalWrite(LED_RED, 1);
    digitalWrite(LED_GREEN, 1);
    digitalWrite(LED_BLUE, 1); 
}

void setupWiFi_AP(){
    IPAddress address(192, 168, 4, 1);
    IPAddress subnet(255, 255, 255, 0);
  
    byte channel = 11;
    float wifiOutputPower = 20.5; //Max power
    WiFi.setOutputPower(wifiOutputPower);
    WiFi.setPhyMode(WIFI_PHY_MODE_11B);
    WiFi.setSleepMode(WIFI_NONE_SLEEP);
    WiFi.disconnect(true);
    WiFi.mode(WIFI_AP);
    //C:\Users\spe\AppData\Roaming\Arduino15\packages\esp8266\hardware\esp8266\2.1.0\cores\esp8266\core_esp8266_phy.c
    //TRYING TO SET [114] = 3 in core_esp8266_phy.c 3 = init all rf
  
    WiFi.persistent(false);
    WiFi.softAPConfig(address, address, subnet);
    WiFi.softAP(ssid, password, channel);
}

//*********************************************************************************************************
//*********************************************************************************************************
void setup() {
    serial.begin(115200);
    serial.setDebugOutput(false);
    delay(1000);

    preparePINS();

    serial.println("");
    serial.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    for(uint8_t t = 4; t > 0; t--) {
        serial.printf("[SETUP] BOOT WAIT %d...\n", t);
        serial.flush();
        delay(1000);
    }

    serial.println("Configuring access point...");
    setupWiFi_AP();
    delay(100);
    
    IPAddress myIP = WiFi.softAPIP();
    serial.println("AP IP address: " + String(myIP[0]) + "." + String(myIP[1]) + "." + String(myIP[2]) + "." + String(myIP[3]));
    serial.println("AP IP address: " + myIP.toString());
    serial.println(decodeWifiSTATUS(WiFi.status()));

    // start webSocket server
    webSocket.begin();
    webSocket.onEvent(webSocketEvent);
    serial.println("WebSocket Server Started");

    digitalWrite(LED_RED, 0);
    digitalWrite(LED_GREEN, 0);
    digitalWrite(LED_BLUE, 0);

}

void loop() {
    webSocket.loop();
    //server.handleClient();
}



