package com.yoni.nanitapp.data.datasource

import com.yoni.nanitapp.data.dto.BirthdayResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

sealed class WebSocketEvent {
    object Connecting : WebSocketEvent()
    object Connected : WebSocketEvent()
    object Disconnected : WebSocketEvent()
    data class MessageReceived(val response: BirthdayResponse) : WebSocketEvent()
    data class Error(val message: String, val exception: Throwable? = null) : WebSocketEvent()
}

@Singleton
class WebSocketDataSource @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val json: Json
) {
    private var webSocket: WebSocket? = null

    fun connectAndListenForEvents(
        ipAddress: String,
        port: Int
    ): Flow<WebSocketEvent> = callbackFlow {
        val request = Request.Builder()
            .url("ws://$ipAddress:$port/nanit")
            .build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                trySend(WebSocketEvent.Connected)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val birthdayResponse = json.decodeFromString<BirthdayResponse>(text)
                    trySend(WebSocketEvent.MessageReceived(birthdayResponse))
                } catch (e: Exception) {
                    trySend(WebSocketEvent.Error("Failed to parse JSON: ${e.message}", e))
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                trySend(WebSocketEvent.Disconnected)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                trySend(WebSocketEvent.Disconnected)
                close()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                trySend(WebSocketEvent.Error(t.message ?: "Connection failed", t))
                close()
            }
        }

        trySend(WebSocketEvent.Connecting)
        webSocket = okHttpClient.newWebSocket(request, listener)

        awaitClose {
            webSocket?.close(1000, "Client closing")
            webSocket = null
        }
    }

    fun disconnect() {
        webSocket?.close(1000, "Manual disconnect")
        webSocket = null
    }
}