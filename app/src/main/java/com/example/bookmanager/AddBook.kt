package com.example.bookmanager

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.bookmanager.SQLite.MyDatabaseHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.internal.ContextUtils.getActivity
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.AccessController.getContext


class AddBook : AppCompatActivity() {
    private val takePhoto = 1
    private lateinit var imageUri: Uri
    private lateinit var outputImage: File
    private val dbHelper = MyDatabaseHelper(this, "BookStore.db", 2)

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)

        findViewById<EditText>(R.id.book_name_edit).addTextChangedListener(EditListener())
        findViewById<EditText>(R.id.author_name_edit).addTextChangedListener(EditListener())
        findViewById<EditText>(R.id.book_type_edit).addTextChangedListener(EditListener())
        findViewById<EditText>(R.id.book_address_edit).addTextChangedListener(EditListener())

        findViewById<MaterialButton>(R.id.cancel_button).setOnClickListener {
            finish()
        }

        " id integer primary key autoincrement," +
                "book_name text," +
                "author_name text," +
                "book_type text," +
                "book_address text" +
                " val bookPicture : Blob)"
        findViewById<MaterialButton>(R.id.confirm_button).setOnClickListener {
            val bookName = findViewById<EditText>(R.id.book_name_edit).text.toString()
            val authorName = findViewById<EditText>(R.id.author_name_edit).text.toString()
            val bookType = findViewById<EditText>(R.id.book_type_edit).text.toString()
            val bookAddress = findViewById<EditText>(R.id.book_address_edit).text.toString()
            val bookPictureBitmap =
                (findViewById<ImageView>(R.id.book_picture).drawable as BitmapDrawable).bitmap
            val os = ByteArrayOutputStream()
            bookPictureBitmap.compress(Bitmap.CompressFormat.JPEG, 50, os)
            os.close()

            val value = ContentValues().apply {
                put("book_name", bookName)
                put("author_name", authorName)
                put("book_type", bookType)
                put("book_address", bookAddress)
                put("book_statement", 1)
                put("book_picture", os.toByteArray())
            }
            val db = dbHelper.writableDatabase
            db.insert("Book", null, value)
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show()
            finish()
        }


        findViewById<Button>(R.id.take_photo).setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf("Manifest.permission.CAMERA"),
                    1
                )
            }
            outputImage = File(externalCacheDir, "output_image.jpg")
            if (outputImage.exists()) {
                outputImage.delete()
            }
            outputImage.createNewFile()
            imageUri =
                FileProvider.getUriForFile(
                    this,
                    "com.example.BookManager.fileprovider",
                    outputImage
                )
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(intent, takePhoto)
        }


        findViewById<ImageView>(R.id.book_picture).setOnClickListener {
            val bitmap =
                (findViewById<ImageView>(R.id.book_picture).drawable as BitmapDrawable).bitmap
            bigImageLoader(bitmap)
        }
    }


    //方法里直接实例化一个imageView不用xml文件，传入bitmap设置图片
    private fun bigImageLoader(bitmap: Bitmap) {
        val dialog = Dialog(this)
        val image = ImageView(this)
        image.setImageBitmap(bitmap)
        dialog.setContentView(image)
        //将dialog周围的白块设置为透明
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        //显示
        dialog.show()
        //点击图片取消
        image.setOnClickListener {
            dialog.cancel()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            takePhoto -> {
                if (resultCode == Activity.RESULT_OK) {
                    val bitmap =
                        BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))
                    findViewById<ImageView>(R.id.book_picture).setImageBitmap(
                        rotateIfRequired(
                            bitmap
                        )
                    )
                }
            }
        }
    }

    private fun rotateIfRequired(bitmap: Bitmap?): Bitmap? {
        val exif = ExifInterface(outputImage.path)
        return when (exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap?, degree: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotateBitmap =
            bitmap?.let { Bitmap.createBitmap(it, 0, 0, bitmap.width, bitmap.height, matrix, true) }
        bitmap?.recycle()
        return rotateBitmap
    }

    inner class EditListener : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            val bookName = findViewById<EditText>(R.id.book_name_edit).text.toString()
            val authorName = findViewById<EditText>(R.id.author_name_edit).text.toString()
            val bookType = findViewById<EditText>(R.id.book_type_edit).text.toString()
            val bookAddress = findViewById<EditText>(R.id.book_address_edit).text.toString()
            val confirmButton = findViewById<Button>(R.id.confirm_button)
            if (bookName != "" && authorName != "" && bookType != "" && bookAddress != "") {
                confirmButton.isEnabled = true
            }
        }

        override fun afterTextChanged(p0: Editable?) {
            val bookName = findViewById<EditText>(R.id.book_name_edit).text.toString()
            val authorName = findViewById<EditText>(R.id.author_name_edit).text.toString()
            val bookType = findViewById<EditText>(R.id.book_type_edit).text.toString()
            val bookAddress = findViewById<EditText>(R.id.book_address_edit).text.toString()
            val confirmButton = findViewById<Button>(R.id.confirm_button)
            confirmButton.isEnabled =
                bookName != "" && authorName != "" && bookType != "" && bookAddress != ""
        }
    }

    private fun createDatabase() {
        dbHelper.writableDatabase
    }


//    @SuppressLint("UseCompatLoadingForDrawables")
//    fun img(id: Int): ByteArray? {
//        //图片转换成字节
//        val baos = ByteArrayOutputStream()
//        val bitmap = (resources.getDrawable(id) as BitmapDrawable).bitmap
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
//        return baos.toByteArray()
//    }

}