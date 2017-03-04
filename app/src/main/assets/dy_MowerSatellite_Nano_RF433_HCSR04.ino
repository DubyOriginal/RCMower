
#include <RCSwitch.h>

#define SATELLITE_ID 1111

// Ultrasonic Sensor
//---------------------------------------------------------------
int echoPin = 4;
int trigPin = 5;
int ledPin  = 6;
//---------------------------------------------------------------

//VAR
//---------------------------------------------------------------
long duration;
int distance;
unsigned long lastT = 0;
unsigned long timeDiff = 0;

RCSwitch mySwitch = RCSwitch();

void setup() {
  Serial.begin(115200);
  pinMode(ledPin, OUTPUT);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);

  digitalWrite(ledPin, 1);
  delay(1000);
  digitalWrite(ledPin, 0);

  Serial.println("----------------------------------------------------------");
  Serial.println("Mover Satellite - ID 1111");
  mySwitch.enableReceive(0);  // Receiver on interrupt 0 => that is pin #2
  Serial.println("READY");

}


void pingUltraSound(){
  Serial.print("ping -> ");

  digitalWrite(trigPin, LOW);
  delayMicroseconds(2);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);

  duration = pulseIn(echoPin, HIGH); // Reads the echoPin, returns the sound wave travel time in microseconds
  distance = duration * 0.34 / 2;

  Serial.print("D: " + String(distance) + "mm");
  Serial.println(" (t: " + String(duration) + "us)");
}

void printReceivedData(){
    Serial.print("\nReceived: ");
    Serial.print( mySwitch.getReceivedValue() );
    Serial.print(" / ");
    Serial.print( mySwitch.getReceivedBitlength() );
    Serial.print("bit / ");
    Serial.print("protocol: ");
    Serial.print(mySwitch.getReceivedProtocol());
    Serial.print(" / PW: ");
    Serial.print(mySwitch.getReceivedDelay());
    Serial.print("us /// ");
}

void loop() {
  digitalWrite(ledPin, 0);
  if (mySwitch.available()) {
    digitalWrite(ledPin, 1);

    int value = mySwitch.getReceivedValue();
    if (value == SATELLITE_ID) {
       timeDiff = micros() - lastT;
       if (timeDiff > 300000){
          printReceivedData();
          pingUltraSound();
          lastT = micros();
       }else {
          Serial.print("*");
       }

       mySwitch.resetAvailable();
       //Serial.println("endT: " + String(micros()));

    }else {
      Serial.println("unknown code: " + String(value));
    }
  }
}