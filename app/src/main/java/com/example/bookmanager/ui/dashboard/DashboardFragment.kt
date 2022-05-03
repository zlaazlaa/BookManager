package com.example.bookmanager.ui.dashboard

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookmanager.BookCardLayout
import com.example.bookmanager.R
import com.example.bookmanager.SQLite.Book
import com.example.bookmanager.SQLite.MyDatabaseHelper
import com.example.bookmanager.databinding.FragmentDashboardBinding
import com.google.android.material.button.*
import java.io.ByteArrayOutputStream


class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val bookList = ArrayList<Book>()

    private var dbHelper = MyDatabaseHelper(activity, "BookStore.db", 2)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

//        val textView: TextView = binding.textDashboard
//        dashboardViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dbHelper = MyDatabaseHelper(activity, "BookStore.db", 2)
    }

    @SuppressLint("Range", "ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val db = dbHelper.writableDatabase
        val hashMap = HashMap<String, Int>()
//        val cursor = db.query("Book", arrayOf("book_type"), null, null, null, null, null)
        val cursor = db.rawQuery("SELECT DISTINCT book_type FROM Book", null)
        var count = 0
        if (cursor.moveToFirst()) {
            do {
                val bookType = cursor.getString(cursor.getColumnIndex("book_type"))
                if (count == 0) {
                    showTypeList(bookType)
                    count++
                }
                val btn = activity?.let { MaterialButton(it) }
                btn?.text = bookType
                btn?.id = count
                btn?.textSize = 20F
                btn?.setOnClickListener {
                    val text = btn.text
                    showTypeList(text as String)
                }
                view.findViewById<LinearLayout>(R.id.book_type_list).addView(btn)

            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    @SuppressLint("Range", "Recycle")
    fun showTypeList(bookTypeToShow: String) {
        view?.findViewById<TextView>(R.id.book_type_to_show)?.text = bookTypeToShow
        bookList.clear()
        val db = dbHelper.writableDatabase
        val cursor =
            db.query("Book", null, "book_type" + "=?", arrayOf(bookTypeToShow), null, null, null)
        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndex("id"))
                val bookName = cursor.getString(cursor.getColumnIndex("book_name"))
                val authorName = cursor.getString(cursor.getColumnIndex("author_name"))
                val bookType = cursor.getString(cursor.getColumnIndex("book_type"))
                val bookAddress = cursor.getString(cursor.getColumnIndex("book_address"))
                val bookStatement = cursor.getInt(cursor.getColumnIndex("book_statement"))
                val bookPicture = cursor.getBlob(cursor.getColumnIndex("book_picture"))
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
            } while (cursor.moveToNext())
        }
        val layoutManager = LinearLayoutManager(activity)
        val recyclerView: RecyclerView =
            view?.findViewById<View>(R.id.book_list_to_show) as RecyclerView
        recyclerView.layoutManager = layoutManager
        val adapter = BookAdapter(bookList)
        recyclerView.adapter = adapter
    }

    @SuppressLint("Recycle", "Range", "ResourceType")
    private fun bigImageLoader(id: String) {
        val dialog = activity?.let { Dialog(it) }
        val image1212 = ImageView(context)
//        val wm = activity?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        val width = wm.defaultDisplay.width
//        val height = wm.defaultDisplay.height
//        image.layoutParams = ViewGroup.LayoutParams(1080, 1920)
        val dbHelper = MyDatabaseHelper(activity, "BookStore.db", 2)
        val db = dbHelper.writableDatabase
        val cursor = db.rawQuery("select * from book where id = ?", arrayOf(id))
        if (cursor.moveToFirst()) {
            do {
                val bookPicture = cursor.getBlob(cursor.getColumnIndex("book_picture"))
                val opts = BitmapFactory.Options()
                opts.inJustDecodeBounds = false //为true时，返回的bitmap为null
                val bitmap = BitmapFactory.decodeByteArray(bookPicture, 0, bookPicture.size, opts)
                image1212.setImageBitmap(bitmap)
            } while (cursor.moveToNext())
        }

        dialog?.setContentView(image1212)
        //将dialog周围的白块设置为透明
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        //显示
        dialog?.show()
        //点击图片取消
        image1212.setOnClickListener { dialog?.cancel() }
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

            val drawable: Bitmap = book.bookPicture
            val os = ByteArrayOutputStream()
            drawable.compress(Bitmap.CompressFormat.JPEG, 100, os)
            Glide.with(activity)
                .load(os.toByteArray())//图片路径
//                .skipMemoryCache(true)//跳过内存缓存
//                .diskCacheStrategy(DiskCacheStrategy.NONE)//不要在disk硬盘缓存
                .override(90, 130)
                .into(holder.bookPicture)//图片控件

            os.close()

            holder.bookPicture.setImageBitmap(book.bookPicture)
            holder.bookId.isVisible = false
//            holder.bookStatement.text = {
//                if (book.bookStatement == 1) {
////                    holder.bookAddress.text = book.bookAddress
//                    "在架"
//                } else {
//                    holder.bookAddress.text = ""
//                    "借出"
//                }
//            }.toString()
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