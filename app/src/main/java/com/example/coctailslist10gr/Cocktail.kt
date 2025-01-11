package com.example.coctailslist10gr

data class Cocktail(
    val title: String,
    val base: String,
    val liqueur1: String,
    val liqueur2: String,
    val puree: String?,
    val syrup: String?,
    val juice: String?,
    val bitters: String?,
    val topUp: String?,
    val garnish: String?,
    val glass: String,
    val image: String
)

