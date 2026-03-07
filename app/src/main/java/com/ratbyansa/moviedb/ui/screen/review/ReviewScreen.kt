package com.ratbyansa.moviedb.ui.screen.review

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.ratbyansa.moviedb.data.remote.model.MovieDetailResponse
import com.ratbyansa.moviedb.ui.screen.review.component.MovieReviewHeader
import com.ratbyansa.moviedb.ui.screen.review.component.ReviewItem
import com.ratbyansa.moviedb.ui.viewmodel.ReviewViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    movie: MovieDetailResponse,
    onBackClick: () -> Unit,
    viewModel: ReviewViewModel = koinViewModel()
) {
    val reviews = viewModel.reviewResult.collectAsLazyPagingItems()
    val headerColor = Color(0xFF0D253F)

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Menjadikan ikon status bar berwarna putih (light status bars = false)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            // Opsional: Jika ingin warna status bar sama persis dengan toolbar
            window.statusBarColor = headerColor.toArgb()
        }
    }

    // Trigger pengambilan data saat movieId tersedia
    LaunchedEffect(movie.id) {
        viewModel.setMovieId(movie.id)
    }

    val displayTotalReviews = movie.voteCount

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Reviews", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = headerColor)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    MovieReviewHeader(movie = movie, totalReviews = displayTotalReviews, backgroundColor = headerColor)
                }

                items(
                    count = reviews.itemCount,
                    key = reviews.itemKey { it.id }
                ) { index ->
                    val review = reviews[index]
                    if (review != null) {
                        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                            ReviewItem(review = review)
                        }
                    }
                }

                // Handling Loading State di bagian bawah (saat scroll)
                when (val state = reviews.loadState.append) {
                    is LoadState.Loading -> {
                        item {
                            Box(Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                    is LoadState.Error -> {
                        item {
                            ErrorRetryItem(message = "Gagal memuat lebih banyak", onRetry = { reviews.retry() })
                        }
                    }
                    else -> {}
                }
            }

            // Initial Loading State (saat pertama kali buka)
            if (reviews.loadState.refresh is LoadState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            // Empty State
            if (reviews.loadState.refresh is LoadState.NotLoading && reviews.itemCount == 0) {
                Text(
                    text = "Belum ada review untuk film ini.",
                    modifier = Modifier.align(Alignment.Center),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun ErrorRetryItem(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, style = MaterialTheme.typography.bodyMedium)
        TextButton(onClick = onRetry) {
            Text("Coba Lagi")
        }
    }
}