package com.example.bookmanager.normal_class

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.bookmanager.R
import com.example.bookmanager.normal_class.MyApplication.Companion.context
import com.example.bookmanager.SQLite.Book
import com.example.bookmanager.SQLite.MyDatabaseHelper
import com.google.android.material.button.MaterialButton

class ShowBookDetails : AppCompatActivity() {

    private val bookList = ArrayList<Book>()

    val dbHelper = MyDatabaseHelper(this, "BookStore.db", 2)

    @SuppressLint("Recycle", "Range")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_book_details)



        val bookIdInDb = intent.getStringExtra("id_in_db")
        val db = dbHelper.writableDatabase

        val allBooks = db.query(
            "Book",
            null,
            "id" + "=?",
            arrayOf(bookIdInDb),
            null,
            null,
            null
        )


        if (allBooks.moveToFirst()) {
//            bookList.clear()
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
                findViewById<TextView>(R.id.book_id_edit).text = id.toString()
                findViewById<TextView>(R.id.book_name_edit).text = bookName.toString()
                findViewById<TextView>(R.id.author_name_edit).text = authorName
                findViewById<TextView>(R.id.book_type_edit).text = bookType
                if(bookStatement == 1) {//目前在架
                    findViewById<TextView>(R.id.book_address_edit).text = bookAddress
                    findViewById<TextView>(R.id.book_statement).text = "在架"
                    findViewById<LinearLayout>(R.id.borrow_layout).isVisible = true
                }
                else {//目前借出
                    findViewById<LinearLayout>(R.id.address_layout).isGone = true
                    findViewById<TextView>(R.id.book_statement).text = "已借出"
                    findViewById<LinearLayout>(R.id.up_book_layout).isVisible = true
                }
                findViewById<ImageView>(R.id.book_picture).setImageBitmap(bookPictureBitmap)
            } while (allBooks.moveToNext())
        }

        findViewById<ImageView>(R.id.book_picture).setOnClickListener {
            val bitmap = (findViewById<ImageView>(R.id.book_picture).drawable as BitmapDrawable).bitmap
            bigImageLoader(bitmap)
        }

        findViewById<MaterialButton>(R.id.confirm_up).setOnClickListener {
            val value = ContentValues()
            value.put("book_address", findViewById<EditText>(R.id.book_address_to_set).text.toString())
            value.put("book_statement", 1)//改为在架
            db.update("Book", value, "id = ?", arrayOf(findViewById<TextView>(R.id.book_id_edit).text.toString()))
            Toast.makeText(context, "操作成功", Toast.LENGTH_LONG).show()
            finish()
        }

        findViewById<MaterialButton>(R.id.confirm_borrow).setOnClickListener {
            val value = ContentValues()
            value.put("book_statement", 0)//改为借出
            db.update("Book", value, "id = ?", arrayOf(findViewById<TextView>(R.id.book_id_edit).text.toString()))
            Toast.makeText(context, "操作成功", Toast.LENGTH_LONG).show()
            finish()
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