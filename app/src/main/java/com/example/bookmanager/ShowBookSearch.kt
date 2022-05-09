package com.example.bookmanager

import android.annotation.SuppressLint
import android.app.Dialog
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookmanager.SQLite.Book
import com.example.bookmanager.SQLite.MyDatabaseHelper
import com.example.bookmanager.normal_class.BookCardLayout
import com.example.bookmanager.normal_class.MyApplication.Companion.context
import java.io.ByteArrayOutputStream

class ShowBookSearch : AppCompatActivity() {
    private val dbHelper = MyDatabaseHelper(this, "BookStore.db", 2)
    private var bookList = ArrayList<Book>()
    companion object {
        var txt : String = ""
    }

    @SuppressLint("Recycle", "Range", "CutPasteId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_book_search)

        txt = intent.getStringExtra("txt").toString()
        findViewById<EditText>(R.id.search_edit_text).setText(txt)
        show(txt)

        findViewById<EditText>(R.id.search_edit_text).setOnEditorActionListener { _, i, _ ->
            bookList.clear()
            txt = findViewById<EditText>(R.id.search_edit_text)!!.text.toString()
            show(txt)
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                txt = findViewById<EditText>(R.id.search_edit_text)!!.text.toString()
                show(txt)
            }
            false
        }
    }

    override fun onResume() {
        super.onResume()
        show(txt)
    }

    private fun show(txt : String) {
        val db = dbHelper.writableDatabase
        val queryString =
            "SELECT  * FROM book where book_name like '%$txt%' or author_name like '%$txt%' or book_address like '%$txt%'"
        val cursor2 = db!!.rawQuery(queryString, null)
        showCursor(cursor2)
    }

    @SuppressLint("Range")
    fun showCursor(cursor2: Cursor) {
        bookList.clear()
        if (cursor2.moveToFirst()) {
            do {
                val id = cursor2.getInt(cursor2.getColumnIndex("id"))
                val bookName = cursor2.getString(cursor2.getColumnIndex("book_name"))
                val authorName = cursor2.getString(cursor2.getColumnIndex("author_name"))
                val bookType = cursor2.getString(cursor2.getColumnIndex("book_type"))
                val bookAddress = cursor2.getString(cursor2.getColumnIndex("book_address"))
                val bookStatement = cursor2.getInt(cursor2.getColumnIndex("book_statement"))
                val bookPicture = cursor2.getBlob(cursor2.getColumnIndex("book_picture"))
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
            } while (cursor2.moveToNext())
        }
        val recyclerView = findViewById<RecyclerView>(R.id.book_list_search)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = BookAdapter(bookList)
        recyclerView.adapter = adapter
    }

    @SuppressLint("Recycle", "Range")
    private fun bigImageLoader(id: String) {
        val dialog = Dialog(this)
        val image = ImageView(context)
        val db = dbHelper.writableDatabase
        val cursor = db?.rawQuery("select * from book where id = ?", arrayOf(id))
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    val bookPicture = cursor.getBlob(cursor.getColumnIndex("book_picture"))
                    val opts = BitmapFactory.Options()
                    opts.inJustDecodeBounds = false //为true时，返回的bitmap为null
                    val bitmap =
                        BitmapFactory.decodeByteArray(bookPicture, 0, bookPicture.size, opts)
                    image.setImageBitmap(bitmap)
                } while (cursor.moveToNext())
            }
        }

        dialog.setContentView(image)
        //将dialog周围的白块设置为透明
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        //显示
        dialog.show()
        //点击图片取消
        image.setOnClickListener { dialog.cancel() }
    }

    inner class BookAdapter(private val bookList: List<Book>) :
        RecyclerView.Adapter<BookAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val bookId: TextView = view.findViewById(R.id.book_id)
            val bookName: TextView = view.findViewById(R.id.book_name)
            val authorName: TextView = view.findViewById(R.id.author_name)
            val bookType: TextView = view.findViewById(R.id.book_type)
            val bookAddress: TextView = view.findViewById(R.id.book_address)
            val bookStatement: TextView = view.findViewById(R.id.book_statement)
            val bookPicture: ImageView = view.findViewById(R.id.book_picture)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//            val view =
//                LayoutInflater.from(parent.context).inflate(R.layout.book_card, parent, false)
            val view = BookCardLayout(context)
            val viewHolder = ViewHolder(view)
            viewHolder.bookPicture.setOnClickListener {
//                val bitmap = (viewHolder.bookPicture.drawable as BitmapDrawable).bitmap
                val id = viewHolder.bookId.text
                bigImageLoader(id as String)
            }
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val book = bookList[position]
            holder.bookId.text = book.id.toString()
            holder.authorName.text = book.authorName
            holder.bookName.text = book.bookName
            holder.bookType.text = book.bookType
            holder.bookId.isVisible = false
            val drawable: Bitmap = book.bookPicture
            val os = ByteArrayOutputStream()
            drawable.compress(Bitmap.CompressFormat.JPEG, 100, os)
            Glide.with(this@ShowBookSearch)
                .load(os.toByteArray())//图片路径
//                .skipMemoryCache(true)//跳过内存缓存
//                .diskCacheStrategy(DiskCacheStrategy.NONE)//不要在disk硬盘缓存
                .override(90, 130)
                .into(holder.bookPicture)//图片控件

            os.close()

            if (book.bookStatement == 1) {
                holder.bookStatement.text = "在架-"
                holder.bookAddress.text = book.bookAddress
            } else {
                holder.bookStatement.text = "借出"
                holder.bookAddress.text = ""
            }

        }

        override fun getItemCount() = bookList.size
    }

}