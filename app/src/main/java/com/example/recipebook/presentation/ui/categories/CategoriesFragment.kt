package com.example.recipebook.presentation.ui.categories

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipebook.databinding.FragmentCategoriesBinding
import com.example.recipebook.presentation.adapter.CategoriesAdapterBig
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CategoriesFragment : Fragment() {

    private var _binding: FragmentCategoriesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CategoriesViewModel by viewModels()
    private lateinit var adapter: CategoriesAdapterBig

    companion object {
        private const val TAG = "CategoriesFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "onViewCreated: Fragment created")

        // ВАЖНО: сначала настраиваем адаптер и RecyclerView
        setupAdapter()
        setupRecyclerView()

        // Затем наблюдаем за данными
        observeViewModel()
    }

    private fun setupAdapter() {
        adapter = CategoriesAdapterBig { category ->
            Log.d(TAG, "Category clicked: ${category.name}")
            viewModel.onCategorySelected(category)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            // ВАЖНО: устанавливаем адаптер ДО установки layout manager
            adapter = this@CategoriesFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
        Log.d(TAG, "RecyclerView setup completed")
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categoriesState.collectLatest { state ->
                if (!isAdded) return@collectLatest

                Log.d(TAG, "State changed: $state")

                when (state) {
                    is UiState.Loading -> {
                        Log.d(TAG, "Loading state")
                        binding.progressBar.visibility = View.VISIBLE
                        binding.emptyView.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE
                    }
                    is UiState.Success -> {
                        Log.d(TAG, "Success state, data: ${state.data?.size} items")
                        binding.progressBar.visibility = View.GONE
                        val categories = state.data ?: emptyList()

                        if (categories.isEmpty()) {
                            Log.d(TAG, "No categories found")
                            binding.emptyView.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        } else {
                            Log.d(TAG, "Submitting ${categories.size} categories to adapter")
                            binding.emptyView.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE

                            // ВАЖНО: убедимся, что адаптер установлен перед submitList
                            if (binding.recyclerView.adapter == null) {
                                Log.e(TAG, "Adapter is null! Re-setting adapter")
                                binding.recyclerView.adapter = adapter
                            }

                            adapter.submitList(categories)
                        }
                    }
                    is UiState.Error -> {
                        Log.e(TAG, "Error state: ${state.message}")
                        binding.progressBar.visibility = View.GONE
                        binding.emptyView.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyView.text = state.message ?: "Произошла ошибка"
                    }
                    is UiState.Empty -> {
                        Log.d(TAG, "Empty state")
                        binding.progressBar.visibility = View.GONE
                        binding.emptyView.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: Refreshing categories")
        // Данные уже загружаются в init ViewModel, поэтому не нужно вызывать повторно
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}