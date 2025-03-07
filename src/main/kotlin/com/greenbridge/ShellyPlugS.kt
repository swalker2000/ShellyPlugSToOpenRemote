package com.greenbridge

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

class ShellyPlugS(private val url : String, private val setSateAction : Boolean = false) {

    private val SET_STATE_ACTION_TIMEOUT = 60L

    private val client = OkHttpClient().newBuilder()
        .readTimeout(30000L, TimeUnit.MILLISECONDS).build()

    private var currentState = false

    private val logger = org.slf4j.LoggerFactory.getLogger("${ShellyPlugS::class.java}")

    val setStateActionThread = Thread{setStateAction()}


    init {
        logger.info("Init ${ShellyPlugS::class.java}{url='$url, setSateAction=$setSateAction'}")
        if (setSateAction) {
            logger.info("Run set state action")
            setStateActionThread.start()
        }
        else
            logger.info("Run set state action not needed")
    }


    fun setState(state : Boolean){
        if (state)
        {
            on()
        }
        else
        {
            off()
        }
    }

    @Synchronized
    fun on(){
        currentState=true
        getRequest("http://$url/relay/0?turn=on")
    }

    @Synchronized
    fun off(){
        currentState=false
        getRequest("http://$url/relay/0?turn=off")
    }

    private fun getRequest(uri : String) : String
    {
        while (true)
            try {
                logger.debug("[TD_URI:GET] : $uri")
                val request: Request = Request.Builder()
                    .url(uri)
                    .get()
                    .build()
                val call: Call = client.newCall(request)
                val response: Response = call.execute()
                logger.debug("[RD_CODE:GET] : ${response.code}")
                val rd = response.body!!.string()
                logger.debug("[RD:GET] : $rd")
                checkServerReturnError(response)
                return rd
            }
            catch (ex: Exception)
            {
                logger.error("Connect to server error.")
                logger.error(ex.stackTraceToString())
            }
    }

    private fun checkServerReturnError(response: Response)
    {
        if(response.code!=200 && response.body!=null) {
            val errorMessage = "Server returned code ${response.code}. Message from server ${response.body!!.string()}."
            logger.error(errorMessage)
            //throw Exception(errorMessage)
        }
    }

    private fun setStateAction()
    {
        while (true) {
            setState(currentState)
            Thread.sleep(SET_STATE_ACTION_TIMEOUT)
        }
    }




}