package com.example.recipebook.presentation.ui.details

import com.example.recipebook.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.recipebook.databinding.FragmentDetailsBinding
import com.example.recipebook.presentation.adapter.IngredientsAdapter
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsFragment: Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!


    private lateinit var adapter: IngredientsAdapter
    private val viewModel: DetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recipeId = arguments?.getLong("recipeId").toString() ?: return

        viewModel.loadRecipe(recipeId)

        lifecycleScope.launch {


            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
            binding.recyclerView.adapter = adapter

            viewModel.state.collect { state ->
                when (state) {
                    is UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val recipe = state.data
                        if (recipe != null) {
                            binding.recipeImage.load(recipe.imageUrl) {
                                placeholder(R.drawable.side_nav_bar)
                            }
                            binding.recipeTitle.text = recipe.name
                            binding.recipeCategory.text = recipe.category ?: ""
                            binding.recipeDescription.text = recipe.description ?: ""
                            binding.favoriteButton.setImageResource(
                                if (recipe.favourite) R.drawable.ic_favorite_selected
                                else R.drawable.ic_favorite_noselected
                            )
                            binding.favoriteButton.setOnClickListener {
                                viewModel.toggleFavorite(recipe.id)
                            }
                            adapter = IngredientsAdapter(recipe.ingredients,recipe.ingredientsImagine)
                        }
                    }
                    is UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.recipeTitle.text = state.message
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}