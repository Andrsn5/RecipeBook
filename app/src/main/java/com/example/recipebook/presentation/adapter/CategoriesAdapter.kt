package com.example.recipebook.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recipebook.databinding.ItemCategoryBinding
import com.example.recipebook.domain.model.Recipe


class CategoriesAdapter(
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<CategoriesAdapter.ViewHolder>() {

    private val items = mutableListOf<Recipe>()

    fun submitList(list: List<Recipe>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(
        private val binding: ItemCategoryBinding,
        private val onClick: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Recipe) {
            binding.categoryName.text = category.category
            binding.root.setOnClickListener { onClick(category.category) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size
}