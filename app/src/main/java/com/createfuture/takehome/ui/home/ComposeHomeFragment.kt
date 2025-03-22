package com.createfuture.takehome.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.createfuture.takehome.R
import com.createfuture.takehome.viewModel.CharacterViewModel
import com.createfuture.takehome.models.ApiCharacter


class ComposeHomeFragment : Fragment() {
    private val viewModel: CharacterViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ComposeView = ComposeView(requireContext()).apply {
        setContent {
            CharacterListScreen(viewModel)
        }
    }
}

@Composable
fun CharacterListScreen(viewModel: CharacterViewModel) {
    val characters by viewModel.characters
    val errorMessage by viewModel.errorMessage
    val isLoading by viewModel.isLoading
    val apiKey = stringResource(id = R.string.api_key)

    LaunchedEffect(Unit) {
        viewModel.loadCharacters(apiKey)
    }

    if (isLoading) {
        LoadingIndicator()
    } else if (errorMessage != null) {
        ErrorMessage(errorMessage!!)
    } else {
        CharacterList(characters)
    }
}

@Composable
fun CharacterList(characters: List<ApiCharacter>) {
    var searchQuery by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(painterResource(id = R.drawable.img_characters), contentScale = ContentScale.FillBounds)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SearchBar(searchQuery) { searchQuery = it }

        val filteredCharacters = characters.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }

        filteredCharacters.forEach { character ->
            CharacterCard(character)
        }
    }
}

@Composable
fun SearchBar(searchText: String, onSearchTextChange: (String) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .background(Color(android.graphics.Color.parseColor("#333336")), shape = RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        BasicTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        if (searchText.isEmpty()) {
            Text("Search", color = Color.Gray, fontSize = 16.sp)
        }
    }
}

@Composable
fun CharacterCard(character: ApiCharacter) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = character.name, color = Color.White, fontSize = 18.sp, modifier = Modifier.padding(bottom = 4.dp))
                InfoRow("Culture:", character.culture.ifEmpty { "Unknown" })
                InfoRow("Born:", character.born.ifEmpty { "Unknown" })
                InfoRow("Died:", character.died.ifEmpty { "Still Alive" })
            }
            Column(modifier = Modifier.padding(start = 8.dp), horizontalAlignment = Alignment.End) {
                Text(text = "Seasons:", color = Color.White, fontSize = 14.sp)
                Text(text = formatSeasons(character.tvSeries), color = Color.Gray, fontSize = 14.sp)
            }
        }
        Divider(color = Color.Gray, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = value, color = Color.Gray.copy(alpha = 0.8f), fontSize = 14.sp)
    }
}

@Composable
fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = Color.Red)
    }
}

fun formatSeasons(seasons: List<String>): String {
    val romanNumerals = mapOf(
        "Season 1" to "I", "Season 2" to "II", "Season 3" to "III",
        "Season 4" to "IV", "Season 5" to "V", "Season 6" to "VI",
        "Season 7" to "VII", "Season 8" to "VIII"
    )
    return seasons.mapNotNull { romanNumerals[it] }.joinToString(", ")
}