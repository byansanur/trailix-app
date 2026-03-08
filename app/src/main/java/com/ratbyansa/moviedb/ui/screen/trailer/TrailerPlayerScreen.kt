package com.ratbyansa.moviedb.ui.screen.trailer

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.ratbyansa.moviedb.data.remote.model.MovieDetailResponse
import com.ratbyansa.moviedb.ui.screen.trailer.component.GenreBadge
import com.ratbyansa.moviedb.ui.screen.trailer.component.RelatedVideoItem
import com.ratbyansa.moviedb.ui.screen.trailer.component.YoutubeVideoPlayer
import com.ratbyansa.moviedb.ui.viewmodel.VideoViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrailerPlayerScreen(
    videoKey: String,
    movie: MovieDetailResponse,
    onBackClick: () -> Unit,
    viewModel: VideoViewModel = koinViewModel()
) {
    val view = LocalView.current
    val context = LocalContext.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    var currentPlayingKey by remember { mutableStateOf(videoKey) }
    val videoList by viewModel.videoList.collectAsState()

    LaunchedEffect(movie.id) {
        viewModel.getMovieTrailer(movie.id)
    }

    val hours = movie.runtime / 60
    val minutes = movie.runtime % 60
    val durationText = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
    val year = if (movie.releaseDate.length >= 4) movie.releaseDate.take(4) else ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${movie.title} Trailer",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        fontSize = 18.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Back", tint = Color.Black)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        shareTrailer(
                            context = context,
                            movieTitle = movie.title,
                            videoKey = currentPlayingKey,
                            overview = movie.overview
                        )
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF3F4F6))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                YoutubeVideoPlayer(
                    videoKey = currentPlayingKey,
                    modifier = Modifier.fillMaxSize()
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                item {
                    Surface(
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = movie.title,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF111827)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                movie.genres.take(3).forEach { genre ->
                                    GenreBadge(genre.name)
                                }
                                Text("•", color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text(durationText, color = Color(0xFF4B5563), fontSize = 13.sp)
                                Text("•", color = Color.Gray, fontWeight = FontWeight.Bold)
                                Text(year, color = Color(0xFF4B5563), fontSize = 13.sp)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = movie.overview,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF374151),
                                lineHeight = 22.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "More like this",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }

                val relatedVideos = videoList.filter { it.site == "YouTube" }

                items(relatedVideos) { video ->
                    val isPlaying = video.key == currentPlayingKey

                    RelatedVideoItem(
                        title = video.name,
                        type = video.type,
                        date = video.publishedAt.take(10),
                        videoKey = video.key,
                        isPlaying = isPlaying,
                        onClick = {
                            if (!isPlaying) {
                                currentPlayingKey = video.key
                            }
                        }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

private fun shareTrailer(context: Context, movieTitle: String, videoKey: String, overview: String) {
    val youtubeUrl = "https://www.youtube.com/watch?v=$videoKey"
    val shareMessage = """
        Tonton trailer keren dari film "$movieTitle"! 🎬
        
        $overview
        
        Saksikan di sini: $youtubeUrl
    """.trimIndent()
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TITLE, "Bagikan Trailer $movieTitle")
        putExtra(Intent.EXTRA_TEXT, shareMessage)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Bagikan lewat...")
    context.startActivity(shareIntent)
}