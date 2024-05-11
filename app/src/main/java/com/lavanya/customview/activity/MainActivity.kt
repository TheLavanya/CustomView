package com.lavanya.customview.activity

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.lavanya.customview.R
import com.lavanya.customview.views.CustomRatingBar

class MainActivity : AppCompatActivity(), CustomRatingBar.OnStarChangeListener {

    protected var mRb: CustomRatingBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mRb = findViewById<View>(R.id.rb1) as CustomRatingBar
        mRb!!.setOnStarChangeListener(this)
        var editText = findViewById<EditText>(R.id.edittext)
        editText.setShadowLayer(editText.extendedPaddingBottom.toFloat(), 0f, 0f, Color.TRANSPARENT)

    }

    override fun onStarChange(ratingBar: CustomRatingBar?, star: Int) {
        Log.d("MainActivity", "star:$star")
    }
}