package com.example.recipebook.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.recipebook.R
import com.example.recipebook.databinding.ItemCategoryBinding
import com.example.recipebook.databinding.ItemCategoryBigBinding
import com.example.recipebook.domain.model.Category

enum class CategoryLayoutType {
    SMALL,
    BIG
}

class CategoriesAdapter(
    private val layoutType: CategoryLayoutType = CategoryLayoutType.SMALL,
    private val onClick: (Category) -> Unit
) : ListAdapter<Category, RecyclerView.ViewHolder>(DiffCallback) {

    companion object {
         val PLACEHOLDER_DRAWABLE = R.drawable.ic_launcher_background
         val ERROR_DRAWABLE = R.drawable.ic_launcher_foreground
    }

    object DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean =
            oldItem == newItem
    }

    override fun getItemViewType(position: Int): Int {
        return when (layoutType) {
            CategoryLayoutType.SMALL -> 0
            CategoryLayoutType.BIG -> 1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val binding = ItemCategoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                SmallViewHolder(binding)
            }
            else -> {
                val binding = ItemCategoryBigBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                BigViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val category = getItem(position)
        when (holder) {
            is SmallViewHolder -> holder.bind(category)
            is BigViewHolder -> holder.bind(category)
        }
    }

    inner class SmallViewHolder(
        private val binding: ItemCategoryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.categoryName.text = category.name

            if (category.imageUrl.isNotEmpty()) {
                binding.categoryImage.load(category.imageUrl) {
                    crossfade(true)
                    placeholder(PLACEHOLDER_DRAWABLE)
                    error(ERROR_DRAWABLE)
                }
            } else {
                binding.categoryImage.setImageResource(ERROR_DRAWABLE)
            }

            binding.root.setOnClickListener { onClick(category) }
        }
    }

    inner class BigViewHolder(
        private val binding: ItemCategoryBigBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.categoryName.text = category.name

            if (category.imageUrl.isNotEmpty()) {
                binding.categoryImage.load(category.imageUrl) {
                    crossfade(true)
                    placeholder(PLACEHOLDER_DRAWABLE)
                    error(ERROR_DRAWABLE)
                }
            } else {
                binding.categoryImage.setImageResource(ERROR_DRAWABLE)
            }

            binding.root.setOnClickListener { onClick(category) }
        }
    }
}