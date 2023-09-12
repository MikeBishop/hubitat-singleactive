/*
    Mutex Switch Child
    Copyright 2023 Mike Bishop,  All Rights Reserved
*/

definition (
    parent: "evequefou:Single Active Switch",
    name: "Mutex Switch Child", namespace: "evequefou", author: "Mike Bishop", description: "Collection of virtual switches, at most one can be on",
    importUrl: "https://raw.githubusercontent.com/MikeBishop/hubitat-singleactive/main/mutex-switch-child.groovy",
    category: "Lighting",
    iconUrl: "",
    iconX2Url: ""
)

preferences {
    page(name: "mainPage")
}

Map mainPage() {
    dynamicPage(name: "mainPage", title: "Mutex Switch", install: true, uninstall: true) {
        initialize();
        section() {
            input "thisName", "text", title: "Name this collection", submitOnChange: true
            def mutexRoot = getMutexRoot();
            if(thisName) {
                app.updateLabel("$thisName");
                mutexRoot.setLabel("$thisName");
            }

            paragraph "At most one child switch can be on at once. If one switch turns on, the others turn off. Use these like Modes, but with as many sets as you need."

            if( state.nextIndex == null ) {
                state.nextIndex = 0;
            }
            else if( settings["childSwitch${state.nextIndex}"] ) {
                debug "Adding child ${state.nextIndex}"
                def newChild = mutexRoot.fetchChild("${state.nextIndex}");
                state.nextIndex = state.nextIndex + 1;
            }

            getMutexRoot().fetchChildren().each { child ->
                def childIndex = child.getDeviceNetworkId().split("-")[1].toInteger();
                def childName = thisName + ": Switch ${childIndex}";
                if( settings["childSwitch${childIndex}"] ) {
                    childName = thisName + ": " + settings["childSwitch${childIndex}"];
                    if( child.getLabel() != childName ) {
                        debug "Updating label for ${childIndex} to ${childName}"
                        child.setLabel(childName);
                    }
                }
                def childLink = "<a href='/device/edit/${child.getId()}' target='_blank'>${settings["childSwitch${childIndex}"]}</a>"
                input "childSwitch${childIndex}", "string", title: "Rename ${childLink}", submitOnChange: true
            }

            input "childSwitch${state.nextIndex}", "string", title: "Add new switch to set:", submitOnChange: true
        }
        section() {
            input "debugSpew", "bool", title: "Log debug messages?",
                submitOnChange: true, defaultValue: false;
        }
    }
}

def getMutexRoot() {
    def rootId = "${app.getId()}";
    def mutexRoot = getChildDevice(rootId)
    if( !mutexRoot ) {
        mutexRoot = addChildDevice("evequefou", "Mutex Switch", rootId, [name: "Mutex Switch", label: thisName, isComponent: true])
    }
    return mutexRoot;
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
