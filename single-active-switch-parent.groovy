/*
    Single Active Switch Parent
    Copyright 2023 Mike Bishop,  All Rights Reserved
*/


definition(
    name: "Single Active Switch",
    namespace: "evequefou",
    author: "Mike Bishop",
    description: "Collection of Single Active Switch groups",
    category: "Lighting",
    importUrl: "https://raw.githubusercontent.com/MikeBishop/hubitat-single-active/main/single-active-switch-parent.groovy",
	iconUrl: "",
    iconX2Url: "",
    iconX3Url: ""
)

preferences {
     page name: "mainPage", title: "", install: true, uninstall: true
}


def installed() {
    log.info "Installed with settings: ${settings}"
    initialize()
}

def uninstalled() {
    log.info "Uninstalled"
}

def updated() {
    log.info "Updated with settings: ${settings}"
    unsubscribe()
    initialize()
}

def initialize() {
    log.info "There are ${childApps.size()} child apps"
    childApps.each { child ->
    	log.info "Child app: ${child.label}"
        child.initialize();
    }
}

def getFormat(type, myText=""){
	if(type == "header-green") return "<div style='color:#ffffff;font-weight: bold;background-color:#81BC00;border: 1px solid;box-shadow: 2px 3px #A9A9A9'>${myText}</div>"
    if(type == "line") return "\n<hr style='background-color:#1A77C9; height: 1px; border: 0;'></hr>"
	if(type == "title") return "<h2 style='color:#1A77C9;font-weight: bold'>${myText}</h2>"
}

def mainPage() {
    dynamicPage(name: "mainPage", install: true, uninstall: true) {
        def appInstalled = app.getInstallationState();

        if (appInstalled != 'COMPLETE') {
    		section{paragraph "Please hit 'Done' to install '${app.label}' parent app "}
  	    }
        else {
			section() {
				paragraph "Each child instance can be configured to make a specific set of devices mutually exclusive. " +
                    "You can create as many instances as you need to cover all of your devices."
            }
  			section("<b>Single-Active Groups:</b>") {
				app(name: "anyOpenApp", appName: "Single Active Switch Child", namespace: "evequefou", title: "<b>Create a new single-active group</b>", multiple: true)
			}
		}
	}
}
