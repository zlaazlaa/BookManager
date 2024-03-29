package com.example.bookmanager.normal_class

import androidx.fragment.app.FragmentActivity

class SqliteClass {

    companion object {
        var mInstance: MyDatabaseHelper? = null
    }

    @Synchronized
    fun getDbHelper(context: FragmentActivity): MyDatabaseHelper? {
        if (mInstance == null) {
            mInstance = MyDatabaseHelper(context, "BookStore.db", 2)
        }
        return mInstance
    }

}