package com.java.easemytripdemo.ui

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.java.easemytripdemo.databinding.CustomRowBinding
import com.java.easemytripdemo.entity.TripData

class MainActivityAdapter: RecyclerView.Adapter<MainActivityAdapter.MyViewHolder>() {

    private var userList = emptyList<TripData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val binding = CustomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        var count  = 0
        for(list in userList)
        {
            count+= list.locations.size
        }

       return count
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        for(list in userList)
        {
            holder.latitude.text = "Lat : ${list.locations[position].latitude.toString()}"
            holder.longitude.text ="Long : ${list.locations[position].longitude.toString()}"
            holder.timeStamp.text =list.locations[position].timestamp
        }
    }

    fun setData(user: List<TripData>){
        for(list in user)
        {
            userList
        }
        this.userList = user
        notifyDataSetChanged()
    }

    class MyViewHolder(binding: CustomRowBinding): RecyclerView.ViewHolder(binding.root) {

        val timeStamp = binding.tvTimeStamp
        val latitude  = binding.tvLat
        val longitude = binding.tvLong

    }


}