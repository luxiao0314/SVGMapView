package com.lucio.map

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapview.setOnProvinceClickLisener({
            if (it.contains("Nei", true)) {
                mapview.setData(R.raw.neimeng)
            }
        }, {
            mapview.setData(R.raw.chinahigh)
        })
    }
}