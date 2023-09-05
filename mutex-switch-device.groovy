/*
    Mutex Switch Device
    Copyright 2023 Mike Bishop,  All Rights Reserved
*/

metadata {
    definition (name: "Mutex Switch", namespace: "evequefou", author: "Mike Bishop") {
        command "allOff"
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
    if (debugSpew) log.info "received on request from ${cd}"
    def targetId = cd.getDeviceNetworkId();
    def childDevices = getChildDevices();
    childDevices.find { it.getDeviceNetworkId() == targetId }.parse([[name:"switch", value:"on", descriptionText:"turned on"]]);
    childDevices.each {
        if( targetId != it.getDeviceNetworkId() && device.currentValue("switch") == "on" ) {
            it.parse([[name:"switch", value: "off", descriptionText:"turned off when ${cd} was turned on"]]);
        }
    }
}

void componentOff(cd){
    if (debugSpew) log.debug "received off request from ${cd}"
    def child = getChildDevice(cd.getDeviceNetworkId());
    child.parse([[name:"switch", value:"off", descriptionText:"${cd} was turned off"]])
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
