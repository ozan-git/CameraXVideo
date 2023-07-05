package com.example.android.camerax.video

import com.example.android.camerax.video.core.Constants.Companion.ACCESS_TOKEN
import com.example.android.camerax.video.core.vimeo.Vimeo
import com.example.android.camerax.video.core.vimeo.VimeoResponse
import java.io.File

class VimeoManager {
    private val vimeo: Vimeo = Vimeo(ACCESS_TOKEN)

    fun addVideo(file: File): String {
        return vimeo.addVideo(file)
    }

    fun addVideo(file: File, name: String, privacy: Map<String, String>): String {
        return vimeo.addVideo(file, name, privacy)
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
        reviewLink: Boolean,
    ) {
        vimeo.updateVideoMetadata(
            videoEndPoint, name, desc, license, privacyView, privacyEmbed, reviewLink
        )
    }

    fun addVideoPrivacyDomain(videoEndPoint: String, domain: String) {
        vimeo.addVideoPrivacyDomain(videoEndPoint, domain)
    }

    fun removeVideo(videoEndPoint: String) {
        vimeo.removeVideo(videoEndPoint)
    }
}
