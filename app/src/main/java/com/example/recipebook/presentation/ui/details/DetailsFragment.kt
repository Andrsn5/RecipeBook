package com.example.recipebook.presentation.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.example.recipebook.R
import com.example.recipebook.databinding.FragmentDetailsBinding
import com.example.recipebook.domain.model.Recipe
import com.example.recipebook.presentation.adapter.IngredientsAdapter
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    private val args: DetailsFragmentArgs by navArgs()

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
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()

        // Загружаем данные рецепта
        viewModel.loadRecipe(args.recipeId)
    }

    private fun setupRecyclerView() {
        adapter = IngredientsAdapter(emptyList())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = this@DetailsFragment.adapter
            setHasFixedSize(true)
        }
    }

    private fun setupClickListeners() {
        binding.favoriteButton.setOnClickListener {
            viewModel.currentRecipe?.let { recipe ->
                viewModel.toggleFavorite(recipe.id)
                // Немедленное обновление иконки для лучшего UX
                updateFavoriteIcon(!recipe.favourite)
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                if (!isAdded) return@collect

                when (state) {
                    is UiState.Loading -> {
                        showLoading(true)
                    }
                    is UiState.Success -> {
                        showLoading(false)
                        state.data?.let { recipe ->
                            displayRecipe(recipe)
                        }
                    }
                    is UiState.Error -> {
                        showLoading(false)
                        showError(state.message)
                    }
                    is UiState.Empty -> {
                        showLoading(false)
                        showError("Рецепт не найден")
                    }
                }
            }
        }
    }

    private fun displayRecipe(recipe: Recipe) {
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

        // Установка текстовой информации
        binding.recipeTitle.text = recipe.name
        binding.recipeDescription.text = recipe.summary ?: "Описание отсутствует"

        // Обновление иконки избранного
        updateFavoriteIcon(recipe.favourite)

        // Обновление списка ингредиентов
        adapter.updateData(recipe.ingredients)

        // Сохраняем текущий рецепт во ViewModel для использования в кликах
        viewModel.setCurrentRecipe(recipe)
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        binding.favoriteButton.setImageResource(
            if (isFavorite) R.drawable.ic_favorite_selected
            else R.drawable.ic_favorite_noselected
        )
    }

    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        binding.recipeTitle.text = "Ошибка"
        binding.recipeDescription.text = message
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}