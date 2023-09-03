/*
    Single Active Switch
    Copyright 2023 Mike Bishop,  All Rights Reserved
*/

definition (
    parent: "evequefou:Single Active Switch",
    name: "Single Active Switch Child", namespace: "evequefou", author: "Mike Bishop", description: "Allows at most one switch to be on",
    importUrl: "https://raw.githubusercontent.com/MikeBishop/hubitat-singleactive/main/single-active-switch.groovy",
    category: "Lighting",
    iconUrl: "",
    iconX2Url: ""
)

preferences {
    page(name: "mainPage")
}

Map mainPage() {
    dynamicPage(name: "mainPage", title: "Single Active Switch", install: true, uninstall: true) {
        initialize();
        section() {
            paragraph "At most one selected switch can be on at once. If one switch turns on, the others turn off."
        }
        section() {
            input "monitored", "capability.switch", title: "Monitored Switches",
                required: true, multiple: true

            input "debugSpew", "bool", title: "Log debug messages?",
                submitOnChange: true, defaultValue: false;
        }
    }
}

void installed() {
    initialize();
}

void updated() {
    initialize();
}

void initialize() {
    unsubscribe();
    subscribe(monitored, "switch.on", "switchOn");
}

void switchOn(event) {
    debug("switchOn: ${event.device} ${event.value}");
    def eventDNI = event.device.getDeviceNetworkId();
    monitored.each { switchDevice ->
        if( switchDevice.getDeviceNetworkId() != eventDNI ) {
            if( debugSpew && switchDevice.currentValue("switch") == "on" ) {
                log.debug("switchOn: Turning ${switchDevice} off");
            }
            switchDevice.off();
        }
    }
}

void debug(String msg) {
    if( debugSpew ) {
        log.debug(msg)
    }
}

void warn(String msg) {
    log.warn(msg)
}

void error(Exception ex) {
    log.error "${ex} at ${ex.getStackTrace()}"
}

void error(String msg) {
    log.error msg
}
