/*
 * DubyOriginal
 * 04.03.2017
 * RF433 data to NANO D2
 */
#include <RCSwitch.h>

#define SATELLITE_ID 1111

// Ultrasonic Sensor
//---------------------------------------------------------------
int echoPin = 4; 
int trigPin = 5;  
//---------------------------------------------------------------

//VAR
//---------------------------------------------------------------
long duration;
int distance;
RCSwitch mySwitch = RCSwitch();

void setup() {
  Serial.begin(115200);
  delay(1000);
  Serial.println("----------------------------------------------------------");
  mySwitch.enableReceive(0);  // Receiver on interrupt 0 => that is pin #2
  Serial.println("setup loaded");
}

void pingUltraSound1(){
  Serial.println("pingUltraSound1");
  
  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  duration = pulseIn(echoPin, HIGH); // Reads the echoPin, returns the sound wave travel time in microseconds
  distance = duration * 0.34 / 2;

  Serial.print("s = " + String(distance) + " mm");
  Serial.println(" (t = " + String(duration) + " us)");
  String disStr = String(distance);
}


void loop() {
  if (mySwitch.available()) {
    
    int value = mySwitch.getReceivedValue();
    
    if (value == 0) {
      Serial.println("Unknown encoding");
    } else {
      Serial.print("Received ");
      Serial.print( mySwitch.getReceivedValue() );
      Serial.print(" / ");
      Serial.print( mySwitch.getReceivedBitlength() );
      Serial.print("bit ");
      Serial.print("Protocol: ");
      Serial.println( mySwitch.getReceivedProtocol() );
    }

    if (value == SATELLITE_ID) {
      pingUltraSound1();  
    }

    mySwitch.resetAvailable();
  }
}
