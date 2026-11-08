package app.rive.data.model

import com.google.gson.annotations.SerializedName

data class ProviderListResponse(
    @SerializedName("data") val data: List<String>
)

data class StreamResponse(
    @SerializedName("data") val data: StreamData?
)

data class StreamData(
    @SerializedName("sources") val sources: List<StreamSource>?,
    @SerializedName("captions") val captions: List<StreamCaption>?
)

data class StreamSource(
    @SerializedName("quality") val quality: String?,
    @SerializedName("url") val url: String,
    @SerializedName("source") val source: String?,
    @SerializedName("format") val format: String? // "hls", "mp4", etc.
) {
    val isHls: Boolean
        get() = format.equals("hls", ignoreCase = true) || url.contains(".m3u8")
}

data class StreamCaption(
    @SerializedName("file") val file: String,
    @SerializedName("label") val label: String?,
    @SerializedName("language") val language: String?,
    @SerializedName("type") val type: String? // "vtt", "srt"
)

