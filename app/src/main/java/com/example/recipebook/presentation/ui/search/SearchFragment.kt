package com.example.recipebook.presentation.ui.search


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipebook.databinding.FragmentSearchBinding
import com.example.recipebook.presentation.adapter.RecipeAdapter
import com.example.recipebook.presentation.ui.favorite.FavoritesFragmentDirections
import com.example.recipebook.presentation.ui.state.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    val binding get() = _binding!!

    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: RecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(layoutInflater)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = RecipeAdapter(
            onClick = { recipe ->
                val action =
                    SearchFragmentDirections.actionSearchFragmentToHomeFragment(recipe.name)
                findNavController().navigate(action)
            },
            onFavClick = { recipe ->

            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        binding.searchInput.addTextChangedListener { text ->
            viewModel.search(text.toString())
        }

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when(state){
                    is UiState.Loading ->{
                        binding.progressBar.visibility = View.VISIBLE
                        binding.emptyText.visibility = View.GONE
                    }
                    is UiState.Success ->{
                        binding.progressBar.visibility = View.GONE
                        adapter.submitList(state.data)
                        binding.emptyText.visibility =
                            if (state.data.isEmpty()) View.VISIBLE else View.GONE
                    }
                    is UiState.Error ->{
                        binding.progressBar.visibility = View.GONE
                        binding.emptyText.visibility = View.VISIBLE
                        binding.emptyText.text = state.message
                    }
                    is UiState.Empty ->{
                        binding.progressBar.visibility = View.GONE
                        binding.emptyText.visibility = View.VISIBLE
                    }
                }
            }
        }
    }
}