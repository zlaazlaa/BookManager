package com.example.bookmanager.normal_class

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookmanager.R
import com.example.bookmanager.databinding.FragmentNotificationsBinding
import java.io.ByteArrayOutputStream

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    private val handler: Handler = Handler()

    var bookList = ArrayList<Book>()

    var dbHelper = activity?.let { SqliteClass().getDbHelper(it) }

    val db = dbHelper?.writableDatabase

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        handler.post(Runnable { //这里写你原来要执行的业务逻辑
            bookList = DataSelected().getBookList()
        })
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
//
//        val textView: TextView = binding.textNotifications
//        notificationsViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let { it1 -> DataSelected().updateAllBooks(it1) }
        bookList = DataSelected().getBookList()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showBookList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showBookList() {
        val layoutManager = LinearLayoutManager(activity)
        val recyclerView: RecyclerView =
            view?.findViewById<View>(R.id.delete_list) as RecyclerView
//        recyclerView.layoutManager = layoutManager
        recyclerView.layoutManager = layoutManager
        val adapter = BookAdapter()
        recyclerView.adapter = adapter
        //下面是滑动时Glide停止加载，好像用处不大
//        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    Glide.with(activity).resumeRequests() //恢复Glide加载图片
//                } else {
//                    Glide.with(activity).pauseRequests() //禁止Glide加载图片
//                }
//            }
//        })
    }

    @SuppressLint("Recycle", "Range")
    private fun bigImageLoader(id: String) {
        val dialog = activity?.let { Dialog(it) }
        val image = ImageView(context)
        val DBHELPER = MyDatabaseHelper(activity, "BookStore.db", 2)
        val DB = DBHELPER.writableDatabase
        val cursor = DB.rawQuery("select * from book where id = ?", arrayOf(id))
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

        dialog?.setContentView(image)
        //将dialog周围的白块设置为透明
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        //显示
        dialog?.show()
        //点击图片取消
        image.setOnClickListener { dialog?.cancel() }
    }

    inner class BookAdapter() :
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
            val view = BookCardLayout2(context)
            val viewHolder = ViewHolder(view)
            viewHolder.bookPicture.setOnClickListener {
//                val bitmap = (viewHolder.bookPicture.drawable as BitmapDrawable).bitmap
                val id = viewHolder.bookId.text
                bigImageLoader(id as String)
            }
            view.findViewById<Button>(R.id.delete_button).setOnClickListener {
                val DBHELPER = MyDatabaseHelper(activity, "BookStore.db", 2)
                val DB = DBHELPER.writableDatabase
                DB.delete("Book", "id = ?", arrayOf("${viewHolder.bookId.text}"))
                activity?.let { it1 -> DataSelected().updateAllBooks(it1) }
                bookList = DataSelected().getBookList()
                showBookList()
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