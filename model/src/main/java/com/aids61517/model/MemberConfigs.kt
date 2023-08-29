package com.aids61517.model

import com.aids61517.processor.annotation.GenConfigMap
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@GenConfigMap
@Serializable
data class MemberConfigs(
    @SerialName("android_ad_cache_slots") val adSlots: Int? = null,
    @SerialName("android_ad_ttl") val adTtl: Int? = null,
    @SerialName("video_upload_duration_max_second") val videoUploadDurationMaxSecond: Int = 900,
    @SerialName("video_upload_file_size_max_mb") val videoUploadFileSizeMaxMb: Int = 1024,
    @SerialName("video_upload_short_side_max_px") val videoUploadShortSideMaxPx: Int? = null,
)