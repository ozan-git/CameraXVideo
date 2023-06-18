package com.example.android.camerax.video

import com.clickntap.vimeo.Vimeo
import com.clickntap.vimeo.VimeoResponse
import java.io.File

class VimeoManager {
    private val vimeo: Vimeo

    init {
        val accessToken = "d43b63cdb265ba0655c0bdbe5a8790a3"
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
