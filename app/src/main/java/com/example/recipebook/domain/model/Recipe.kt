package com.example.recipebook.domain.model

data class Recipe(val id: String,
                  val name: String,
                  val description: String,
                  val imageUrl: String?,
                  val category: String,
                  val ingredients: List<String> = emptyList(),
                  val favourite: Boolean)
