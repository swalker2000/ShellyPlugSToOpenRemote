package com.greenbridge.com.greenbridge

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.concurrent.TimeUnit

class ShellyPlugS(private val url : String) {

    private val client = OkHttpClient().newBuilder()
        .readTimeout(30000L, TimeUnit.MILLISECONDS).build()

    private val logger = org.slf4j.LoggerFactory.getLogger("${ShellyPlugS::class.java}")


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

    fun on(){
        getRequest("http://192.168.0.15/relay/0?turn=on")
    }

    fun off(){
        getRequest("http://192.168.0.15/relay/0?turn=off")
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
                logger.warn("Connect to server error.")
                logger.warn(ex.stackTraceToString())
                throw ex
            }
    }

    private fun checkServerReturnError(response: Response)
    {
        if(response.code!=200 && response.body!=null) {
            val errorMessage = "Server returned code ${response.code}. Message from server ${response.body!!.string()}."
            logger.error(errorMessage)
            throw Exception(errorMessage)
        }
    }




}