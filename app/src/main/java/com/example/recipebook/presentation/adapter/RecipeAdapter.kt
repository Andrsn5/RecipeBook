package com.example.recipebook.presentation.adapter

import com.example.recipebook.R
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.recipebook.databinding.ItemRecipeBinding
import com.example.recipebook.domain.model.Recipe

class RecipeAdapter(
    private val onClick: (Recipe) -> Unit,
    private val onFavClick: (Recipe) -> Unit
) : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object DiffCallback : DiffUtil.ItemCallback<Recipe>() {
        override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean =
            oldItem == newItem
    }

    inner class RecipeViewHolder(private val binding: ItemRecipeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            binding.recipeTitle.text = recipe.name

            // Загрузка изображения
            if (!recipe.imageUrl.isNullOrEmpty()) {
                binding.recipeImage.load(recipe.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_background)
                    error(R.drawable.ic_launcher_foreground)
                }
            } else {
                binding.recipeImage.setImageResource(R.drawable.ic_launcher_foreground)
            }

            // Обновление иконки избранного
            updateFavoriteIcon(recipe.favourite)

            binding.root.setOnClickListener {
                onClick(recipe)
            }

            binding.favoriteButton.setOnClickListener {
                onFavClick(recipe)
                // Немедленное обновление иконки для лучшего UX
                updateFavoriteIcon(!recipe.favourite)
            }
        }

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            binding.favoriteButton.setImageResource(
                if (isFavorite) R.drawable.ic_favorite_selected
                else R.drawable.ic_favorite_noselected
            )
        }
    }
}