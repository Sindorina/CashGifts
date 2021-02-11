package com.sin.cashgift.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sin.cashgift.CASH_DB

@Database(entities = [CashBean::class],version = 1,exportSchema = false)
abstract class CashDatabase:RoomDatabase(){

    abstract fun cashDao():CashDao

    companion object{
        @Volatile
        private var instance:CashDatabase? = null

        fun getInstance(context:Context):CashDatabase{
            return instance?: synchronized(this){
                Room.databaseBuilder(context,CashDatabase::class.java, CASH_DB).build()
                    .also {
                        instance = it
                    }
            }
        }
    }

}