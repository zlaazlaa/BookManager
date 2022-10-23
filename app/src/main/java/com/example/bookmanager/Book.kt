package com.example.bookmanager.normal_class

import android.graphics.Bitmap
import java.sql.Blob

class Book(
    val id: Int,
    val bookName: String,
    val authorName: String,
    val bookType: String,
    val bookAddress: String,
    val bookStatement: Int,
    val bookPicture: Bitmap
)