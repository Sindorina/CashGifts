package com.sin.cashgift.data

import android.os.Parcelable



data class TrainDataBeanGuiYang(
    var number: String = "",
    var trainName: String = "",
    var stationName:String = "",
    var operationLine: String = "",
    var waitColor:String = "",
) {
    var train_info_uid:Long = 0L
}

