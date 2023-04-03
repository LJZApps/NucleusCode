package com.ljz.nucleus.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * User created in this database are just temporary and will be deleted after a successful registration or login
 */
class UserDBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        if (!tableExists("UserData", db)) {
            createUserDataTable(db)
        }
    }

    fun deleteUser(uid: String) {
        val db = this.writableDatabase

        db.execSQL("DELETE FROM UserData WHERE uid='$uid'")
        db.close()
    }

    fun removeUserPassword(uid: String) {
        val db = this.writableDatabase

        db.execSQL("UPDATE 'UserData' SET 'password' = NULL WHERE 'uid' = '$uid'")

        db.close()
    }

    fun addUser(uid: String, password: String, email: String) {
        val db = this.writableDatabase

        db.execSQL("INSERT INTO UserData (uid, email, password) VALUES ('$uid', '$email', '$password')")

        db.close()
    }

    fun getPassword(uid: String): String {
        val db = this.readableDatabase
        val returnValue: String
        if (tableExists("UserData", db)) {
            val query = "SELECT password FROM UserData WHERE uid='$uid'"
            val cursor: Cursor = db.rawQuery(query, null)
            returnValue = if (cursor.moveToFirst()) {
                cursor.getString(0)
            } else {
                throw Exception("No user with this uid")
            }
            cursor.close()

            /*
            if (cursor.moveToFirst()) {
                returnValue = cursor.getString(cursor.getColumnIndexOrThrow("color_value"))
            }else{
                returnValue = "cursor is not moved to first"
            }
             */
        } else {
            returnValue = throw Exception("No users in Database")
        }

        db.close()

        return returnValue
    }

    private fun tableExists(table: String?, db: SQLiteDatabase?): Boolean {
        if (db != null) {
            if (!db.isOpen || table == null) {
                return false
            }
        }
        var count = 0
        val cursor = db?.rawQuery(
            "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='$table'",
            null
        )
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }
        }
        cursor?.close()

        return count > 0
    }

    fun isRegisterFinished(uid: String): Boolean {
        val db = this.readableDatabase
        var returnValue = false
        if (tableExists("UserData", db)) {
            var query = "SELECT * FROM UserData WHERE register_finished='1' AND uid='$uid'"
            val cursor: Cursor = db.rawQuery(query, null)
            returnValue = cursor.count > 0
        } else {
            false
        }

        db.close()

        return returnValue
    }

    private fun createUserDataTable(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE 'UserData' ('uid' VARCHAR PRIMARY KEY, 'email' VARCHAR, 'password' VARCHAR, 'register_finished' NUMERIC(1))")

        //db.close()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //TODO("Not yet implemented")
    }

    companion object {
        // here we have defined variables for our database

        // below is variable for database name
        private const val DATABASE_NAME = "UserDatabase"

        // below is the variable for database version
        private const val DATABASE_VERSION = 1
    }
}