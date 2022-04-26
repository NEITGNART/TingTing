package api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


private const val BASE_URL = "https://tingting-94a57-default-rtdb.asia-southeast1.firebasedatabase.app/"
object RetrofitInstance {
    /**
     * Use the Retrofit builder to build a retrofit object using a Moshi converter.
     */
    private val retrofit by lazy {

        val moshi = Moshi.Builder() // adapter
            .add(KotlinJsonAdapterFactory())
            .build()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi).asLenient())
            .build()
    }

    val api: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }
}