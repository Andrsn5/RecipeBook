package com.example.recipebook.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.recipebook.R
import com.example.recipebook.databinding.ItemRecipeBinding
import com.example.recipebook.domain.model.Recipe

class RecipeAdapter(
    private val onClick: (Recipe) -> Unit,
    private val onFavClick: (Recipe) -> Unit
) : ListAdapter<Recipe, RecipeAdapter.RecipeViewHolder>(DiffCallback) {

    companion object {
         val PLACEHOLDER_DRAWABLE = R.drawable.ic_launcher_background
         val ERROR_DRAWABLE = R.drawable.ic_launcher_foreground
         val FAVORITE_SELECTED_DRAWABLE = R.drawable.ic_favorite_selected
         val FAVORITE_NO_SELECTED_DRAWABLE = R.drawable.ic_favorite_noselected
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding, onClick, onFavClick)
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

    class RecipeViewHolder(
        private val binding: ItemRecipeBinding,
        private val onClick: (Recipe) -> Unit,
        private val onFavClick: (Recipe) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            binding.recipeTitle.text = recipe.name

            if (!recipe.imageUrl.isNullOrEmpty()) {
                binding.recipeImage.load(recipe.imageUrl) {
                    crossfade(true)
                    placeholder(PLACEHOLDER_DRAWABLE)
                    error(ERROR_DRAWABLE)
                }
            } else {
                binding.recipeImage.setImageResource(ERROR_DRAWABLE)
            }

            updateFavoriteIcon(recipe.favourite)

            binding.root.setOnClickListener {
                onClick(recipe)
            }

            binding.favoriteButton.setOnClickListener {
                onFavClick(recipe)
            }
        }

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            binding.favoriteButton.setImageResource(
                if (isFavorite) FAVORITE_SELECTED_DRAWABLE
                else FAVORITE_NO_SELECTED_DRAWABLE
            )
        }
    }
}