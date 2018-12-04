package basile.luc.epicture

import android.content.Context
import android.content.Intent
import android.net.Uri
import okhttp3.HttpUrl
import okhttp3.OkHttpClient

class Imgur_Auth {
    val HOST = "api.imgur.com"

    inner class CallbackResponse {
        var accessToken: String? = null
        var expiresIn: Long = 0
        var tokenType: String? = null
        var refreshToken: String? = null
        var accountUsername: String? = null
        var accountId: Long = 0 // check
    }

    private val clientId: String = "9e775f25b104d4d"
    private val clientSecret: String = "7b8834b4c004cd6ccdaa44276666df82575d5441"
    public var token: String? = null
    public var accountUsername: String? = null

    val client: OkHttpClient = OkHttpClient.Builder().build()

    /*fun Imgur(clientId: String, clientSecret: String) {
        this.clientId = clientId
        this.clientSecret = clientSecret
        this.client = OkHttpClient.Builder().build()
    }*/

    private fun parseResponse(uri: String): CallbackResponse {
        val res = CallbackResponse()
        val parameters = uri.split("#".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].split("&".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()

        res.accessToken = parameters[0].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        res.expiresIn =
                java.lang.Long.valueOf(parameters[1].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])
        res.tokenType = parameters[2].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        res.refreshToken = parameters[3].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        res.accountUsername = parameters[4].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        res.accountId =
                java.lang.Long.valueOf(parameters[5].split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1])

        return res
    }

    fun authenticate(context: Context) {
        val url = HttpUrl.Builder()
            .scheme("https")
            .host(HOST)
            .addPathSegment("oauth2")
            .addPathSegment("authorize")
            .addQueryParameter("client_id", clientId)
            .addQueryParameter("response_type", "token")
            .build()

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.toString()))
        context.startActivity(intent)
    }


    fun authenticateCallback(uri: String) {
        val res = parseResponse(uri)
        this.token = res.accessToken
        this.accountUsername = res.accountUsername
        println("ICI")
        println(this.token)
    }

}