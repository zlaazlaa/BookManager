package com.example.bookmanager.normal_class

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.fragment.app.FragmentActivity

class DataSelected {
    private lateinit var dbHelper: MyDatabaseHelper
    companion object {
        var bookList = ArrayList<Book>()
    }

    private fun getDbHelper(context: FragmentActivity) {
        dbHelper = SqliteClass().getDbHelper(context)!!
    }

    fun getBookList(): ArrayList<Book> {
        return bookList
    }

    @SuppressLint("Range")
    fun updateAllBooks(context: FragmentActivity) {
        getDbHelper(context)
        bookList.clear()
        val db = dbHelper.writableDatabase
        val allBooks = db?.query("Book", null, null, null, null, null, null)
        if (allBooks != null) {
            if (allBooks.moveToFirst()) {
                do {
                    val id = allBooks.getInt(allBooks.getColumnIndex("id"))
                    val bookName = allBooks.getString(allBooks.getColumnIndex("book_name"))
                    val authorName = allBooks.getString(allBooks.getColumnIndex("author_name"))
                    val bookType = allBooks.getString(allBooks.getColumnIndex("book_type"))
                    val bookAddress = allBooks.getString(allBooks.getColumnIndex("book_address"))
                    val bookStatement = allBooks.getInt(allBooks.getColumnIndex("book_statement"))
                    val bookPicture = allBooks.getBlob(allBooks.getColumnIndex("book_picture"))
                    val bookPictureBitmap =
                        BitmapFactory.decodeByteArray(bookPicture, 0, bookPicture.size)
                    bookList.add(
                        Book(
                            id,
                            bookName,
                            authorName,
                            bookType,
                            bookAddress,
                            bookStatement,
                            bookPictureBitmap
                        )
                    )
                } while (allBooks.moveToNext())
                allBooks.close()
            }
        }
    }
}