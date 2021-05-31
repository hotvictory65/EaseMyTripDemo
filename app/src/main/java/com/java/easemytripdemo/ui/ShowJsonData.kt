package com.java.easemytripdemo.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.java.easemytripdemo.R
import org.json.JSONArray
import org.json.JSONObject

class ShowJsonData : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_json_data)


        val showData = findViewById<TextView>(R.id.tv_show_data)

        if(intent.extras!= null)
        {
            val data = intent.getStringExtra("JsonData")
            if(data != null)
            {
                val json =  JSONArray(data)
                showData.text = json.toString(4)
            }
        }

    }
}