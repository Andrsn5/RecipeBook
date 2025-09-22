package com.example.recipebook.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.recipebook.R
import com.example.recipebook.data.remote.recipeRemote.IngredientDto
import com.example.recipebook.databinding.ItemProductBinding

class IngredientsAdapter(private var ingredients: List<IngredientDto>) :
    RecyclerView.Adapter<IngredientsAdapter.MyViewHolder>() {

    fun updateData(newIngredients: List<IngredientDto>) {
        ingredients = newIngredients
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(ingredients[position])
    }

    override fun getItemCount(): Int = ingredients.size

    class MyViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredient: IngredientDto) {
            binding.productTittle.text = ingredient.name


            if (!ingredient.imageUrl.isNullOrEmpty()) {
                if (ingredient.imageUrl.startsWith("http")) {
                    binding.productImage.load(ingredient.imageUrl) {
                        crossfade(true)
                        placeholder(R.drawable.ic_launcher_background)
                        error(R.drawable.ic_launcher_foreground)
                    }
                } else {
                    val imageResourceId = ingredient.imageUrl.toIntOrNull() ?: 0
                    if (imageResourceId != 0) {
                        binding.productImage.setImageResource(imageResourceId)
                    } else {
                        binding.productImage.setImageResource(R.drawable.ic_launcher_foreground)
                    }
                }
            } else {
                binding.productImage.setImageResource(R.drawable.ic_launcher_foreground)
            }
        }
    }
}