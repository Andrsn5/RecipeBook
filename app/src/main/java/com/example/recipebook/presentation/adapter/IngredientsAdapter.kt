package com.example.recipebook.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.recipebook.R
import com.example.recipebook.databinding.ItemProductBinding
import com.example.recipebook.presentation.model.IngredientUi

class IngredientsAdapter : ListAdapter<IngredientUi, IngredientsAdapter.ViewHolder>(DiffCallback) {

    companion object {
         val PLACEHOLDER_DRAWABLE = R.drawable.ic_launcher_background
         val ERROR_DRAWABLE = R.drawable.ic_launcher_foreground
    }

    object DiffCallback : DiffUtil.ItemCallback<IngredientUi>() {
        override fun areItemsTheSame(oldItem: IngredientUi, newItem: IngredientUi): Boolean =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: IngredientUi, newItem: IngredientUi): Boolean =
            oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredient: IngredientUi) {
            binding.productTittle.text = ingredient.name

            if (!ingredient.imageUrl.isNullOrEmpty()) {
                binding.productImage.load(ingredient.imageUrl) {
                    crossfade(true)
                    placeholder(PLACEHOLDER_DRAWABLE)
                    error(ERROR_DRAWABLE)
                }
            } else {
                binding.productImage.setImageResource(ERROR_DRAWABLE)
            }
        }
    }
}