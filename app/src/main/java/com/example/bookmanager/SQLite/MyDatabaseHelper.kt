package com.example.bookmanager.SQLite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import androidx.fragment.app.FragmentActivity

class MyDatabaseHelper(private val context: FragmentActivity?, name: String, version: Int) : SQLiteOpenHelper(context, name, null, version) {

    private val createBook = "create table Book (" +
            " id integer primary key autoincrement," +
            "book_name text," +
            "author_name text," +
            "book_type text," +
            "book_address text," +
            "book_statement integer," +
            "book_picture BLOB)"//0代表借出，1代表在架

    override fun onCreate(p0: SQLiteDatabase?) {
        p0?.execSQL(createBook)
        Toast.makeText(context, "Success", Toast.LENGTH_LONG).show()
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {

    }

}