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


        setupAdapter()
        setupRecyclerView()


        observeViewModel()
    }

    private fun setupAdapter() {
        adapter = CategoriesAdapterBig { category ->
            viewModel.onCategorySelected(category)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            adapter = this@CategoriesFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categoriesState.collectLatest { state ->
                if (!isAdded) return@collectLatest


                when (state) {
                    is UiState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.emptyView.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE
                    }
                    is UiState.Success -> {
                        binding.progressBar.visibility = View.GONE
                        val categories = state.data ?: emptyList()

                        if (categories.isEmpty()) {
                            binding.emptyView.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        } else {
                            Log.d(TAG, "Submitting ${categories.size} categories to adapter")
                            binding.emptyView.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE

                            if (binding.recyclerView.adapter == null) {
                                binding.recyclerView.adapter = adapter
                            }

                            adapter.submitList(categories)
                        }
                    }
                    is UiState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        binding.emptyView.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyView.text = state.message ?: "Произошла ошибка"
                    }
                    is UiState.Empty -> {
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}