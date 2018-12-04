package basile.luc.epicture

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers

interface ApiImage {
    @Headers("Authorization: Client-ID 9e775f25b104d4d")
    @GET("3/gallery/hot/viral/week?showViral=true&mature=true&album_previews=true")
    fun getImages() : Observable<ImageResponse>
}


interface ApiSearch {
    @Headers("Authorization: Client-ID 9e775f25b104d4d")
    @GET("3/gallery/search/")
    fun getSearch(@Query ("q") query: String): Observable<ImageResponse>
}
