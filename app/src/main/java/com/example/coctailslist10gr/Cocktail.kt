package com.example.coctailslist10gr

data class Cocktail(
    val title: String,
    val base: String,
    val liqueur1: String,
    val liqueur1Amount: Int,
    val liqueur2: String,
    val liqueur2Amount: Int,
    val puree: String,
    val pureeAmount: Int,
    val syrup: String,
    val syrupAmount: Int,
    val juice: String,
    val juiceAmount: Int,
    val bitters: String,
    val bittersAmount: Int,
    val garnish: String,
    val glass: String
)
