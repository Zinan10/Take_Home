package com.createfuture.takehome.models

data class ApiCharacter(
    val name: String,
    val gender: String,
    val culture: String,
    val born: String,
    val died: String,
    val aliases: List<String>, // //first issue is with list type it should be string instead of int
    val tvSeries: List<String>,
    val playedBy: List<String>,
)