#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>
#include <ESP8266mDNS.h>

// Replace with your network credentials
const char* ssid = "REDACTED";
const char* password = "REDACTED";

// Create server and listen on port 80
ESP8266WebServer server(80);
MDNSResponder mdns;

String webPage = "";

#define OPEN_PIN 16 //D0
#define CLOSE_PIN 5 //D1
#define ON_TIME 1000
 
void setup(void){
  pinMode(OPEN_PIN, OUTPUT);
  pinMode(CLOSE_PIN, OUTPUT);
  digitalWrite(OPEN_PIN, HIGH);
  digitalWrite(CLOSE_PIN, HIGH);
  webPage += "<h1>Smart Blinds</h1>\
              <p>Blinds #1\
                <a href=\"open\">\
                  <button>Open</button>\
                </a>\
                <a href=\"close\">\
                  <button>Close</button>\
                </a>\
              </p>";
  
  Serial.println();
  Serial.begin(9600);
  WiFi.begin(ssid, password);

  // Wait for connection
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println();
  Serial.print("Connected to ");
  Serial.println(ssid);
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());
  
  if (mdns.begin("SmartBlinds", WiFi.localIP())) {
    Serial.println("MDNS responder started");
  }
  
  server.on("/", [](){
    server.send(200, "text/html", webPage);
  });
  server.on("/open", [](){
    server.send(200, "text/html", webPage);
		open();
  });
  server.on("/close", [](){
    server.send(200, "text/html", webPage);
    Serial.println("Closing");
		close();
  });
  server.begin();
  Serial.println("HTTP server started");
}
 
void loop(void){
  server.handleClient();
} 

void open(void){
    Serial.println("Opening");
    digitalWrite(OPEN_PIN, LOW);
    delay(ON_TIME);
    digitalWrite(OPEN_PIN, HIGH);
}

void close(void){
    Serial.println("Closing");
    digitalWrite(CLOSE_PIN, LOW);
    delay(ON_TIME);
    digitalWrite(CLOSE_PIN, HIGH);
}
