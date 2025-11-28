package com.example.terminmenadzer.kalendar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import com.example.terminmenadzer.R

class KalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kalendar)

        // Pretpostavljamo da postoji dugme sa ovim ID-em
        val btnNazad = findViewById<Button>(R.id.btnNazadKalendar)
        btnNazad?.setOnClickListener {
            finish()
        }
    }
}