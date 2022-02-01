package com.example.sentiancesdksample_app_android

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun initWithUserLinking(view: View) {
        Toast.makeText(this, "Init with userlinking", Toast.LENGTH_SHORT).show();
        startNextActivity()
    }

    fun initWithoutUserLinking(view: View) {
        Toast.makeText(this, "Init without userlinking", Toast.LENGTH_SHORT).show();
        startNextActivity()
    }

    private fun startNextActivity(){
        val intent = Intent(this, Dashboard::class.java)
        startActivity(intent)
    }
}