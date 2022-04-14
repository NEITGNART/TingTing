package api

import com.example.tingting.utils.Entity.User
import retrofit2.http.GET

interface UserApi {
    //    @GET("posts")
//    suspend fun getPosts(@Query("_page") page: Int = 1, @Query("_limit") limit: Int = 10): List<User>
    @GET(".")
    suspend fun getUser(): User
}