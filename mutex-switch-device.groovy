/*
    Mutex Switch Device
    Copyright 2023 Mike Bishop,  All Rights Reserved
*/

metadata {
    definition (name: "Mutex Switch", namespace: "evequefou", author: "Mike Bishop") {
        command "allOff"
        attribute "activeSwitch", "string"
        attribute "previousActiveSwitch", "string"
    }
    preferences {
        input name: "debugSpew", type: "bool", title: "Enable debug logging", defaultValue: false
    }
}

def fetchChild(String id) {
    String thisId = device.id
    def cd = getChildDevice("${thisId}-${id}")
    if (!cd) {
        log.debug "creating child device ${id}"
        cd = addChildDevice("hubitat", "Generic Component Switch", "${thisId}-${id}", [name: "${device.displayName} Switch ${id}", isComponent: false])
        //set initial attribute values
        cd.parse([[name:"switch", value:"off", descriptionText:"set initial switch value"]])
    }
    return cd
}

def fetchChildren() {
    return getChildDevices()
}

//child device methods
void componentOn(cd){
    debug "received on request from ${cd}"
    def targetId = cd.getDeviceNetworkId();
    def childDevices = getChildDevices();
    def switchOn = childDevices.find { it.getDeviceNetworkId() == targetId };
    childDevices.each {
        if( targetId != it.getDeviceNetworkId() && it.currentValue("switch") == "on" ) {
            it.parse([[name:"switch", value: "off", descriptionText:"turned off when ${cd} was turned on"]]);
        }
    }
    switchOn.parse([[name:"switch", value:"on", descriptionText:"turned on"]]);
    updateCurrent(switchOn);
}

void componentOff(cd){
    if (debugSpew) log.debug "received off request from ${cd}"
    def child = getChildDevice(cd.getDeviceNetworkId());
    child.parse([[name:"switch", value:"off", descriptionText:"${cd} was turned off"]])
    if (getChildDevices().every { it.currentValue("switch") == "off" }) {
        updateCurrent();
    }
}

void updateCurrent(child = null) {
    debug "updating current switch to ${child}"
    sendEvent([name:"previousActiveSwitch", value: device.currentValue("activeSwitch") ?: "none"]);
    sendEvent([name:"activeSwitch", value: "${child ?: "none"}"]);
}

void allOff() {
    def childDevices = getChildDevices();
    childDevices.each {
        if( it.currentValue("switch") == "on" ) {
            it.parse([[name:"switch", value: "off", descriptionText:"turned off by allOff()"]]);
        }
    }
}

void componentRefresh(cd) { }

void debug(msg) {
    if (debugSpew) log.debug msg
}
