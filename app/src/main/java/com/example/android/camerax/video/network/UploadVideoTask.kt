package com.example.android.camerax.video.network

import android.net.Uri
import androidx.core.net.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.IOException

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
}

class UploadVideoTask(private val videoUri: Uri) {

    suspend fun uploadVideo(): Result<String> = withContext(Dispatchers.IO) {
        val videoFile = videoUri.toFile()

        val requestBody = videoFile.asRequestBody("video/*".toMediaTypeOrNull())
        val videoPart = MultipartBody.Part.createFormData("video", videoFile.name, requestBody)

        try {
            val response = VimeoApiClient.vimeoService.uploadVideo(videoPart).execute()
            if (response.isSuccessful) {
                val videoId = response.body()?.videoId
                if (videoId != null) {
                    return@withContext Result.Success(videoId)
                }
            }
            return@withContext Result.Error("Failed to upload video")
        } catch (e: IOException) {
            return@withContext Result.Error("Network error: ${e.localizedMessage}")
        } catch (e: HttpException) {
            return@withContext Result.Error("HTTP error: ${e.code()}")
        }
    }
}
