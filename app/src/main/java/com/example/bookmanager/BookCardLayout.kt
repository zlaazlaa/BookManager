package com.example.bookmanager

import android.content.Context
import android.content.Intent
<<<<<<< HEAD:app/src/main/java/com/example/bookmanager/normal_class/BookCardLayout.kt
import android.os.Build
=======
import android.util.AttributeSet
>>>>>>> parent of db14b7a (列表更完善，数据库加载优化):app/src/main/java/com/example/bookmanager/BookCardLayout.kt
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewParent
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
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context?.startActivity(intent)
        }
    }
}