package com.example.android.camerax.video

import com.example.android.camerax.video.vimeo.Vimeo
import com.example.android.camerax.video.vimeo.VimeoResponse
import java.io.File

class VimeoManager {
    private val vimeo: Vimeo

    init {
        val accessToken = "549c86b0dbe2d671ef63e503c4af2c60"
        vimeo = Vimeo(accessToken)
    }

    fun addVideo(file: File): String {
        return vimeo.addVideo(file)
    }

    fun getVideoInfo(videoEndPoint: String): VimeoResponse {
        return vimeo.getVideoInfo(videoEndPoint)
    }

    fun updateVideoMetadata(
        videoEndPoint: String,
        name: String,
        desc: String,
        license: String,
        privacyView: String,
        privacyEmbed: String,
        reviewLink: Boolean
    ) {
        vimeo.updateVideoMetadata(
            videoEndPoint,
            name,
            desc,
            license,
            privacyView,
            privacyEmbed,
            reviewLink
        )
    }

    fun addVideoPrivacyDomain(videoEndPoint: String, domain: String) {
        vimeo.addVideoPrivacyDomain(videoEndPoint, domain)
    }

    fun removeVideo(videoEndPoint: String) {
        vimeo.removeVideo(videoEndPoint)
    }
}
