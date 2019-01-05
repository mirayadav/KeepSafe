// Constantly reads signals from bluetooth device, and if no signal received buzzer is played
// The HC-05 defaults to commincation mode when first powered on.
 
#include <SoftwareSerial.h>
SoftwareSerial BTserial(10, 11); // RX | TX
const int buzzerPin = 7;//the buzzer pin attach to pin 7
// Connect the HC-05 TX to Arduino pin 10 
// Connect the HC-05 RX to Arduino pin 11

void setup() 
{
    Serial.begin(9600);
    pinMode(buzzerPin,OUTPUT);//set buzzerPin as OUTPUT
    // HC-06 default serial speed for communcation mode is 9600
    BTserial.begin(9600);  
}
 
void loop() 
{
    Serial.println(BTserial.read());
    //If no signal received then play buzzer
    if(BTserial.read() == -1){
      tone(7,100);
      delay(1000);
      BTserial.write(2);
    }

    //If signal received then silence buzzer
    else{
      noTone(7);
      delay(100);
      BTserial.write(2);
    }
}
