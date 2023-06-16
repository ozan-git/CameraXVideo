package com.example.android.camerax.video.network

import com.example.android.camerax.video.model.UploadVideoResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface VimeoService {
    @Multipart
    @POST("upload/video")
    fun uploadVideo(@Part video: MultipartBody.Part): Call<UploadVideoResponse>
}
