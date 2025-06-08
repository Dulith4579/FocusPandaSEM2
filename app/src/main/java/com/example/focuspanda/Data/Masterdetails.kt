package com.example.focuspanda.Data

data class FeatureDetail(
    val name: String,
    val title: String,
    val description: String,
    val benefits: List<String>,
    val imageRes: String
)

data class FeatureDetailsResponse(
    val features: List<FeatureDetail>
)
