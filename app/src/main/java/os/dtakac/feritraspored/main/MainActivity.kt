package os.dtakac.feritraspored.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import os.dtakac.feritraspored.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}