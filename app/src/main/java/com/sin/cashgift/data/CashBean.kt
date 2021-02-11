package com.sin.cashgift.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cash")
data class CashBean (
    @PrimaryKey
    var id:Long,
    var name:String = "",
    var money:String = "0")


