package com.example.androidexperiments

import android.graphics.Bitmap

interface ImageCallback {

    fun failed()

    fun success(bitmap: Bitmap)

}
