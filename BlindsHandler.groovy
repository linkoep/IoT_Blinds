/**
 *  Smart Blinds Custom Handler
 *
 *  Adapted From: https://github.com/JZ-SmartThings/SmartThings/blob/master/Devices/Generic%20HTTP%20Device/GenericHTTPDevice.groovy
 *  at Generic HTTP Device v1.0.20160402
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */

import groovy.json.JsonSlurper

metadata {
	definition (name: "Generic HTTP Device - ESP8266 - Blinds", author: "linkoep", namespace:"linkoep") {
		capability "Switch"
		attribute "triggerswitch", "string"
		command "DeviceTrigger"
	}


	preferences {
		input("DeviceURL", "string", title:"Device URL", description: "Please enter your device's root URL", required: true, displayDuringSetup: true)
	}

	simulator {
	}

	tiles {
		standardTile("DeviceTrigger", "device.triggerswitch", width: 3, height: 3, canChangeIcon: true, canChangeBackground: true) {
			state "triggeroff", label:'CLOSED' , action: "on", icon: "st.Weather.weather4", backgroundColor:"#808080", nextState: "trying"
			state "triggeron", label: 'OPEN', action: "off", icon: "st.Weather.weather14", backgroundColor: "#ffffff", nextState: "trying"
			state "trying", label: 'TRYING', action: "", icon: "st.Home.home9", backgroundColor: "#FFAA33"
		}
		main "DeviceTrigger"
		details(["DeviceTrigger", "oscTrigger", "modeTrigger", "speedTrigger", "timerAddTrigger", "timerMinusTrigger"])
	}
}

def on() {
	log.debug "Triggered OPEN!!!"
	sendEvent(name: "triggerswitch", value: "triggeron", isStateChange: true)
    state.blinds = "on";
	runCmd("/open")
}
def off() {
	log.debug "Triggered CLOSE!!!"
	sendEvent(name: "triggerswitch", value: "triggeroff", isStateChange: true)
    state.blinds = "off";
	runCmd("/close")
}


def runCmd(String varCommand) {
	def host = DeviceURL
	def path = DeviceUrl + varCommand
	log.debug "path is: $path"
	def body = ""//varCommand
	def headers = [:]
	log.debug "The Header is $headers"
	def method = "GET"
	log.debug "The method is $method"
	try {
		def hubAction = new physicalgraph.device.HubAction(
			method: method,
			path: path,
			body: body,
			headers: headers
			)
		hubAction.options = [outputMsgToS3:false]
		//log.debug hubAction
		hubAction
	}
	catch (Exception e) {
		log.debug "Hit Exception $e on $hubAction"
	}
}

def parse(String description) {
	//log.debug "Parsing '${description}'"
	def whichTile = ''	
	log.debug "state.blinds " + state.blinds
	
    if (state.blinds == "on") {
    	//sendEvent(name: "triggerswitch", value: "triggergon", isStateChange: true)
        whichTile = 'mainon'
    }
    if (state.blinds == "off") {
    	//sendEvent(name: "triggerswitch", value: "triggergoff", isStateChange: true)
        whichTile = 'mainoff'
    }
	
    //RETURN BUTTONS TO CORRECT STATE
	log.debug 'whichTile: ' + whichTile
    switch (whichTile) {
        case 'mainon':
			def result = createEvent(name: "switch", value: "on", isStateChange: true)
			return result
        case 'mainoff':
			def result = createEvent(name: "switch", value: "off", isStateChange: true)
			return result
        default:
			def result = createEvent(name: "testswitch", value: "default", isStateChange: true)
			//log.debug "testswitch returned ${result?.descriptionText}"
			return result
    }
}
