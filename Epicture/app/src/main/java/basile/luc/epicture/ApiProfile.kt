package basile.luc.epicture

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface ApiProfile {
    @Headers("Authorization: Client-ID 9e775f25b104d4d")
    @GET("3/account/{accountname}")
    fun getProfile(@Path("accountname") String: Any) : retrofit2.Call<ProfileReponse>
}

interface ApiProfileImages {
    @Headers("Authorization: Bearer 9e775f25b104d4d")
    @GET("3/account/{accountname}/images")
    fun getProfileImages(@Path("accountname") String: Any) : Observable<ImageResponse>
}