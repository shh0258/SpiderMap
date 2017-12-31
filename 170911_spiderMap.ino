#include <Servo.h> 
#include <SoftwareSerial.h> //시리얼 통신 라이브러리 호출


Servo servo;
 
int servoPin = 9;
int num =0;
int angle= 0;
int temp =0;

int blueTx=2;   //Tx (보내는핀 설정)
int blueRx=3;   //Rx (받는핀 설정)
SoftwareSerial mySerial(blueTx, blueRx);  //시리얼 통신을 위한 객체선언
String myString=""; //받는 문자열


void callback1(){
  temp =0;
    while(temp == 0)
    {
     servo.write(60); 
     delay(1000); 
     servo.write(0); 
     delay(1000);
     if (mySerial.available()){
      temp = mySerial.read();
     }
    }
}

void callback2(){
  temp =0;
  while(temp == 0)
  {
   servo.write(120); 
   delay(1000); 
   servo.write(180); 
   delay(1000); 
   if (mySerial.available()){
      temp = mySerial.read();
     }
  }
}

void setup() {
  // put your setup code here, to run once:
    Serial.begin(9600);   //시리얼모니터
    mySerial.begin(9600); //블루투스 시리얼 개방
    servo.attach(servoPin);
    
    for(angle = 0; angle < 180; angle++) 
  { 
    servo.write(angle); 
    delay(15); 
  }
    
}

void loop() {
  while(mySerial.available())  //mySerial 값이 있으면
  {
    char myChar = (char)mySerial.read();  //mySerial int형식의 값을 char형식으로 변환
    myString+=myChar;   //수신되는 문자열을 myString에 모두 붙임 (1바이트씩 전송되는 것을 모두 붙임)
    delay(5);           //수신 문자열 끊김 방지
  }
  
  if(!myString.equals(""))  //myString 값이 있다면
  {
    Serial.println("input value: "+myString); //시리얼모니터에 myString값 출력
 
      if(myString=="1")  //myString 값이 'on' 이라면
      {
        callback1(); 
      } else if (myString == "2"){
        callback2();
      }
      
    myString="";  //myString 변수값 초기화
  }


}
