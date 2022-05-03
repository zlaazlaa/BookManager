package com.example.bookmanager.ui.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.target.Target
import com.example.bookmanager.AddBook
import com.example.bookmanager.BookCardLayout
import com.example.bookmanager.R
import com.example.bookmanager.SQLite.Book
import com.example.bookmanager.SQLite.MyDatabaseHelper
import com.example.bookmanager.databinding.FragmentHomeBinding
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val bookList = ArrayList<Book>()


    private var dbHelper = MyDatabaseHelper(activity, "BookStore.db", 2)

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dbHelper = MyDatabaseHelper(activity, "BookStore.db", 2)
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

//        val textView: TextView = binding.textHome
//        homeViewModel.text.observe(viewLifecycleOwner) {
//            textView.text = it
//        }

        getBookList()


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        getBookList()
        showBookList()
    }

    override fun onResume() {
        super.onResume()
        getBookList()
        showBookList()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        view?.findViewById<Button>(R.id.button_show_books)?.setOnClickListener {
            getBookList()
            showBookList()
        }
        view?.findViewById<Button>(R.id.button_add_book)?.setOnClickListener {
            val intent = Intent(activity, AddBook::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        getBookList()
        showBookList()
        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    @SuppressLint("Range")
    fun getBookList() {
        bookList.clear()
        val db = dbHelper.writableDatabase
        val allBooks = db.query("Book", null, null, null, null, null, null)
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

    private fun showBookList() {
        val layoutManager = LinearLayoutManager(activity)
        val recyclerView: RecyclerView =
            view?.findViewById<View>(R.id.book_list) as RecyclerView
//        recyclerView.layoutManager = layoutManager
        recyclerView.layoutManager = layoutManager
        val adapter = BookAdapter(bookList)
        recyclerView.adapter = adapter
    }

    @SuppressLint("Recycle", "Range")
    private fun bigImageLoader(id: String) {
        val dialog = activity?.let { Dialog(it) }
        val image = ImageView(context)
        val dbHelper = MyDatabaseHelper(activity, "BookStore.db", 2)
        val db = dbHelper.writableDatabase
        val cursor = db.rawQuery("select * from book where id = ?", arrayOf(id))
        if (cursor.moveToFirst()) {
            do {
                val bookPicture = cursor.getBlob(cursor.getColumnIndex("book_picture"))
                val opts = BitmapFactory.Options()
                opts.inJustDecodeBounds = false //为true时，返回的bitmap为null
                val bitmap = BitmapFactory.decodeByteArray(bookPicture, 0, bookPicture.size, opts)
                image.setImageBitmap(bitmap)
            } while (cursor.moveToNext())
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