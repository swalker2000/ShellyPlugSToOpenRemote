package com.greenbridge

import connector.OpenRemoteConnectorFactory

private val openRemoteConnectorFactory = OpenRemoteConnectorFactory(
    System.getenv("host"),
    System.getenv("port").toInt(),
    System.getenv("clientId"),
    System.getenv("realm"),//master
)
    .authorizationByLoginAndPassword(System.getenv("username"),System.getenv("password"))
    .trustAllCerts()
    .build()


private val shelly = ShellyPlugS(System.getenv("shellyUrl"))

private val mqttLogger = org.slf4j.LoggerFactory.getLogger("mqtt")

fun main() {
//subscribe on event
    val subscribeConnector = openRemoteConnectorFactory.getOpenRemoteSubscribeConnector<Boolean>( "state", System.getenv("assetId"))
    subscribeConnector.subscribe{
        mqttLogger.info("new message : ${it.value}")
        try{
            it.value?.let { it1 -> shelly.setState(it1) }
        }
        catch (ex : Exception) {
            mqttLogger.error(ex.stackTraceToString())
        }

    }
}