package com.example.bookmanager

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bookmanager.MyApplication.Companion.context
import com.example.bookmanager.SQLite.Book
import com.example.bookmanager.SQLite.MyDatabaseHelper

class ShowBookDetails : AppCompatActivity() {

    private val bookList = ArrayList<Book>()

    @SuppressLint("Recycle", "Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_book_details)

        val dbHelper = MyDatabaseHelper(this, "BookStore.db", 2)

        val bookIdInDb = intent.getStringExtra("id_in_db")
        val db = dbHelper.writableDatabase
        val allBooks =
            db.query("Book", arrayOf("author_name"), "123", null, null, null, null)

        if (allBooks.moveToFirst()) {
//            bookList.clear()
            do {
                val id = allBooks.getInt(allBooks.getColumnIndex("id"))
//                val bookName = allBooks.getString(allBooks.getColumnIndex("book_name"))
//                val authorName = allBooks.getString(allBooks.getColumnIndex("author_name"))
//                val bookType = allBooks.getString(allBooks.getColumnIndex("book_type"))
//                val bookAddress = allBooks.getString(allBooks.getColumnIndex("book_address"))
//                val bookStatement = allBooks.getInt(allBooks.getColumnIndex("book_statement"))
//                val bookPicture = allBooks.getBlob(allBooks.getColumnIndex("book_picture"))
//                val bookPictureBitmap =
//                    BitmapFactory.decodeByteArray(bookPicture, 0, bookPicture.size)
                findViewById<TextView>(R.id.book_name_edit).text = id.toString()
//                findViewById<TextView>(R.id.author_name_edit).text = authorName
//                findViewById<TextView>(R.id.book_type_edit).text = bookType
//                findViewById<TextView>(R.id.book_address_edit).text = bookAddress
//                findViewById<ImageView>(R.id.book_picture).setImageBitmap(bookPictureBitmap)
            } while (allBooks.moveToNext())
        }

        findViewById<ImageView>(R.id.book_picture).setOnClickListener {
            val bitmap = (findViewById<ImageView>(R.id.book_picture).drawable as BitmapDrawable).bitmap
            bigImageLoader(bitmap)
        }

    }

    private fun bigImageLoader(bitmap: Bitmap) {
        val dialog = Dialog(this)
        val image = ImageView(context)
        image.setImageBitmap(bitmap)
        dialog.setContentView(image)
        //将dialog周围的白块设置为透明
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        //显示
        dialog.show()
        //点击图片取消
        image.setOnClickListener { dialog.cancel() }
    }
}