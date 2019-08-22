/**
 *  Smart Blinds Custom Handler
 *
 */

metadata {
	// Define the device and supported attributes
	definition (name: "Generic HTTP Device - ESP8266 - Blinds", author: "linkoep", namespace:"linkoep") {
		capability "Switch"
		attribute "triggerswitch", "string"
		command "DeviceTrigger"
	}

	// Allow the user to specify URL during setup
	preferences {
		input("DeviceIP", "string", title:"Device IP", description: "Please enter your device's IP Address", required: true, displayDuringSetup: true)
	}

	// Any settings for testing go here
    simulator {
	}

	// Configure UI
    tiles {
		standardTile("DeviceTrigger", "device.triggerswitch", width: 3, height: 3, canChangeIcon: true, canChangeBackground: true) {
			state "triggeroff", label:'CLOSED' , action: "on", icon: "st.Weather.weather4", backgroundColor:"#808080", nextState: "trying"
			state "triggeron", label: 'OPEN', action: "off", icon: "st.Weather.weather14", backgroundColor: "#ffffff", nextState: "trying"
			state "trying", label: 'TRYING', action: "", icon: "st.Home.home9", backgroundColor: "#FFAA33"
		}
		main "DeviceTrigger"
	}
}

def on() {
	log.debug "Triggered OPEN!"
	sendEvent(name: "triggerswitch", value: "triggeron", isStateChange: true)
    state.blinds = "on";
	runCmd("/open")
}
def off() {
	log.debug "Triggered CLOSE!"
	sendEvent(name: "triggerswitch", value: "triggeroff", isStateChange: true)
    state.blinds = "off";
	runCmd("/close")
}


def runCmd(String varCommand) {
	def result = new physicalgraph.device.HubAction(
		method: "GET",
		path: varCommand,
		headers: [
			HOST: "$DeviceIP:80"
		]
	)
	log.debug result
	return result
}

// Currently the device does not send any messages of value
def parse(String description) {
	log.debug "Parsing"
	log.debug "$description"
}

