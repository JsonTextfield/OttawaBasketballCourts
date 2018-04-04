package com.textfield.json.ottawabasketballcourts.util

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase

import java.io.IOException

class DB(context: Context) {
    private var mDb: SQLiteDatabase? = null
    private val mDbHelper: DBHelper = DBHelper(context)

    @Throws(SQLException::class)
    fun createDatabase(): DB {
        try {
            mDbHelper.createDataBase()
        } catch (mIOException: IOException) {
            throw Error("UnableToCreateDatabase")
        }
        return this
    }

    fun open(): DB {
        mDb = mDbHelper.readableDatabase
        return this
    }

    fun close() {
        mDbHelper.close()
    }

    fun runQuery(sql: String): Cursor? {
        println(sql)

        val mCur = mDb!!.rawQuery(sql, null)
        mCur?.moveToNext()

        return mCur
    }

    companion object {
        private var mInstance: DB? = null

        fun getInstance(context: Context): DB {
            if (mInstance == null) {
                mInstance = DB(context)
            }
            return mInstance as DB
        }
    }

}