package com.example.recipebook.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.R
import com.example.recipebook.data.remote.recipeRemote.IngredientDto

class IngredientsAdapter(private var ingredients: List<IngredientDto>): RecyclerView.Adapter<IngredientsAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return MyViewHolder(view)
    }

    fun updateData(newIngredients: List<IngredientDto>) {
        ingredients = newIngredients
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.itemText.text = ingredients[position].name
        val imageResourceId = ingredients[position].imageUrl.toIntOrNull() ?: 0
        if (imageResourceId != 0) {
            holder.itemImagine.setImageResource(imageResourceId)
        }
    }

    override fun getItemCount(): Int = ingredients.size

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val itemText = itemView.findViewById<TextView>(R.id.productTittle)
        val itemImagine = itemView.findViewById<ImageView>(R.id.productImage)
    }
}