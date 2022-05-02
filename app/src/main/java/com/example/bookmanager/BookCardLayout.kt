package com.example.bookmanager

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity


class BookCardLayout(context: Context?) :
    LinearLayout(context) {
    init {
        LayoutInflater.from(context).inflate(R.layout.book_card, this)
        findViewById<LinearLayout>(R.id.book_card).setOnClickListener {
            val intent = Intent(context, ShowBookDetails::class.java)
            intent.putExtra("id_in_db", findViewById<TextView>(R.id.book_id).text.toString())
            context?.startActivity(intent)
        }
    }
}