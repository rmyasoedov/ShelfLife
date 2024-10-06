package com.shelflife.instrument.api

import android.util.Log
import com.shelflife.instrument.BuildConfig
import io.appmetrica.analytics.impl.T
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import retrofit2.Call
import retrofit2.Response

class MyResponse <T>{
    var code: Int? = null
    var error: Boolean = false
    var e: Exception? = null
    var body: T?= null
    var errorBody: Any? = null
    var raw: Any? = null
    var headers: Any? = null
    var message: Any?= null
    var eTimeout: Boolean = false
    var errorMessage: Any ?= null
    var errorUtf8: String ?= null

    companion object{
        private suspend fun <T> callbackResponse(deferredResponse: Deferred<Response<T>>): MyResponse<T>{
            val myResponse = MyResponse<T>()
            myResponse.error = false
            try {
                val response = deferredResponse.await()

                //-----------------------------------------------------------------------------------
                if(BuildConfig.DEBUG){
                    val strLog =
                        "%-->ЗАПРОС ОТПРАВЛЕН: %0A${response.raw()}\n-->ПАРАМЕТРЫ ЗАПРОСА: ${response.raw().request} \n<--СЕРВЕР ОТВЕТИЛ: %0A${
                            response.body().toString()
                        }"

                    Log.d("NET_LOG", strLog)
                }
                //-----------------------------------------------------------------------------------

                if(response.code() !in listOf(200,204)){
                    throw Exception("")
                }
                myResponse.apply {
                    code = response.code()
                    body = response.body()
                    errorBody = response.errorBody()
                    raw = response.raw()
                    headers = response.headers()
                    message = response.message()
                    val errorMessage1 = response.errorBody()?.byteString()
                    errorMessage = errorMessage1
                    errorUtf8 = errorMessage1?.utf8()
                }

            }catch (e: Exception){
                myResponse.error = true
                myResponse.e = e
            }
            return myResponse
        }

        suspend fun <T> getResponse(response: Call<T>): MyResponse<T>{
            return callbackResponse(
                GlobalScope.async {
                    response.execute()
                }
            )
        }
    }
}