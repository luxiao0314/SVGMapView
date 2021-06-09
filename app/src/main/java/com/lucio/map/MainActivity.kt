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
            if (it.contains("nei", true)) {
                mapview.setData(R.raw.neimeng)
            } else if (it.contains("tai", true)) {
                mapview.setData(R.raw.taiwanhigh)
            }
        }, {
            mapview.setData(R.raw.chinahigh)
        })
    }
}