package com.example.liveearthmapuet.adapters

import androidx.room.Entity
import androidx.room.PrimaryKey


data class ResponseCams(
    val data: MutableList<AllCamModel>
)

@Entity(tableName = "cams")
data class AllCamModel(
    var cam_name: String? = "",
    var url_mjpeg: String? = "",
    var cam_country: String? = "",
    var cam_city: String? = "",
    var lat: String? = "",
    var lang: String? = "",
    var titleImage: Int? = null,
    var isFav: Boolean? = false,
    var categoryName: String? = "",
    @PrimaryKey
    var id: Int? = 0,

    )