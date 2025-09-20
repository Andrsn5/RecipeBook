package com.example.recipebook.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.R

class IngredientsAdapter(private val listName: List<String>,private val listImagine: List<String>): RecyclerView.Adapter<IngredientsAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_details, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.itemText.text = listName[position]
        val imageResourceId = listImagine[position].toIntOrNull() ?: 0
        if (imageResourceId != 0) {
            holder.itemImagine.setImageResource(imageResourceId)
        }
    }

    override fun getItemCount(): Int = listName.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val itemText = itemView.findViewById<TextView>(R.id.productTittle)
        val itemImagine = itemView.findViewById<ImageView>(R.id.productImage)
    }
}