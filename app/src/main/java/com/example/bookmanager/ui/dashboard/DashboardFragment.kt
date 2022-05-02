package com.example.bookmanager.ui.dashboard

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bookmanager.R
import com.example.bookmanager.SQLite.Book
import com.example.bookmanager.SQLite.MyDatabaseHelper
import com.example.bookmanager.databinding.FragmentDashboardBinding
import com.google.android.material.button.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
        val cursor = db.query("Book", null, null, null, null, null, null)
        var count = 0
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
//                bookList.add(
//                    Book(
//                        id,
//                        bookName,
//                        authorName,
//                        bookType,
//                        bookAddress,
//                        bookStatement,
//                        bookPictureBitmap
//                    )
//                )

                if (hashMap[bookType] == null) {
                    count++
                    hashMap[bookType] = 1
                    val btn = activity?.let { MaterialButton(it) }
                    btn?.text = bookType
                    btn?.id = count
                    btn?.textSize = 20F
                    view.findViewById<LinearLayout>(R.id.book_type_list).addView(btn)
                }
                if(bookType == activity?.findViewById<MaterialButton>(1)?.text) {
//                    val bookTag =
//
//                    view.findViewById<LinearLayout>(R.id.book_list).addView()
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
    }
}