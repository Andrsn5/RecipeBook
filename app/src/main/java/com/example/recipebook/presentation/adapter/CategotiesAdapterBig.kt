package com.example.recipebook.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.recipebook.R
import com.example.recipebook.databinding.ItemCategoryBigBinding
import com.example.recipebook.domain.model.Category

class CategoriesAdapterBig(
    private val onClick: (Category) -> Unit
) : ListAdapter<Category, CategoriesAdapterBig.ViewHolder>(DiffCallback) {

    object DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean =
            oldItem == newItem
    }

    inner class ViewHolder(
        private val binding: ItemCategoryBigBinding,
        private val onClick: (Category) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.categoryName.text = category.name

            // Загружаем изображение категории
            if (!category.imageUrl.isNullOrEmpty()) {
                binding.categoryImage.load(category.imageUrl) {
                    crossfade(true)
                    placeholder(R.drawable.ic_launcher_background)
                    error(R.drawable.ic_launcher_foreground)
                }
            } else {
                binding.categoryImage.setImageResource(R.drawable.ic_launcher_foreground)
            }

            binding.root.setOnClickListener {
                onClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBigBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}