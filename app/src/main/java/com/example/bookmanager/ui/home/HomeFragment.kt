package com.example.bookmanager.ui.home

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bookmanager.R
import com.example.bookmanager.SQLite.Book
import com.example.bookmanager.SQLite.DataSelected
import com.example.bookmanager.ShowBookSearch
import com.example.bookmanager.databinding.FragmentHomeBinding
import com.example.bookmanager.normal_class.AddBook
import com.example.bookmanager.normal_class.BookCardLayout
import com.example.bookmanager.normal_class.SqliteClass
import java.io.ByteArrayOutputStream


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val handler: Handler = Handler()

    private var bookList = ArrayList<Book>()

    private var dbHelper = activity?.let { SqliteClass().getDbHelper(it) }

    val createTag = 0

    private val temp = SqliteClass()//实例化对象，为了获得静态数据

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        activity?.let { temp.getDbHelper(it) }
        dbHelper = activity?.let { temp.getDbHelper(it) }
//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        handler.post(Runnable { //这里写你原来要执行的业务逻辑
            bookList = DataSelected().getBookList()
        })


        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        activity?.let { it1 -> DataSelected().updateAllBooks(it1) }
        bookList = DataSelected().getBookList()
        super.onCreate(savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        showBookList()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        handler.post(Runnable { //这里写你原来要执行的业务逻辑
//            showBookList()
        })
    }

    override fun onResume() {
        super.onResume()
        handler.post(Runnable { //这里写你原来要执行的业务逻辑
//            showBookList()
        })
    }

    @SuppressLint("Recycle", "Range")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.findViewById<Button>(R.id.button_show_books)?.setOnClickListener {
            activity?.let { it1 -> DataSelected().updateAllBooks(it1) }
            bookList = DataSelected().getBookList()
            showBookList()
        }
        view?.findViewById<Button>(R.id.button_add_book)?.setOnClickListener {
            val intent = Intent(activity, AddBook::class.java)
            startActivityForResult(intent, 222)
        }


        view?.findViewById<EditText>(R.id.search_edit_text)
            ?.setOnEditorActionListener { _, i, _ ->
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    val txt = view?.findViewById<EditText>(R.id.search_edit_text)!!.text.toString()
                    val intent = Intent(activity, ShowBookSearch::class.java)
                    intent.putExtra("txt", txt)
                    startActivity(intent)
                }
                false
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            222 -> if (resultCode == RESULT_OK) {
                activity?.let { it1 -> DataSelected().updateAllBooks(it1) }
                bookList = DataSelected().getBookList()
                showBookList()
            }
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        handler.post(Runnable { //这里写你原来要执行的业务逻辑
//            showBookList()
        })
        return super.onCreateAnimation(transit, enter, nextAnim)
    }


    private fun showBookList() {
        val layoutManager = LinearLayoutManager(activity)
        val recyclerView: RecyclerView =
            view?.findViewById<View>(R.id.book_list) as RecyclerView
//        recyclerView.layoutManager = layoutManager
        recyclerView.layoutManager = layoutManager
        val adapter = BookAdapter(bookList)
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
        val db = dbHelper?.writableDatabase
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

        dialog?.setContentView(image)
        //将dialog周围的白块设置为透明
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        //显示
        dialog?.show()
        //点击图片取消
        image.setOnClickListener { dialog?.cancel() }
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