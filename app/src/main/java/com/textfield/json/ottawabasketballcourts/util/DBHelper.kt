package com.textfield.json.ottawabasketballcourts.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DBHelper(private val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, 1) {
    @Throws(IOException::class)
    fun createDataBase() {
        //If the database does not exist, copy it from the assets.

        val dbFile = File(context.cacheDir, databaseName)
        if (!dbFile.exists()) {
            try {
                copyDataBase(dbFile)
                Log.e(TAG, "createDatabase database created")
            } catch (mIOException: IOException) {
                throw Error("ErrorCopyingDataBase")
            }
        }
    }

    //Copy the database from assets
    @Throws(IOException::class)
    private fun copyDataBase(dbFile: File) {
        val mInput = context.assets.open(databaseName)
        val mOutput = FileOutputStream(dbFile)
        mInput.copyTo(mOutput, 1024)
        mOutput.flush()
        mOutput.close()
        mInput.close()
    }

    override fun onCreate(db: SQLiteDatabase) {}

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    override fun getReadableDatabase(): SQLiteDatabase {
        return SQLiteDatabase.openDatabase(File(context.cacheDir, databaseName).path, null, SQLiteDatabase.OPEN_READONLY)
    }

    companion object {
        private val TAG = "DataBaseHelper"
        private const val DB_NAME = "basketball.db"// Database name
    }
}
