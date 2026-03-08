package com.ratbyansa.moviedb.ui.screen.trailer.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun YoutubeVideoPlayer(
    videoKey: String,
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    val cleanVideoId = remember(videoKey) {
        videoKey.replace("[^a-zA-Z0-9_\\-]".toRegex(), "")
    }

    var youtubePlayer by remember { mutableStateOf<YouTubePlayer?>(null) }
    var currentLoadedId by remember { mutableStateOf("") }

    LaunchedEffect(cleanVideoId, youtubePlayer) {
        if (youtubePlayer != null && cleanVideoId.length == 11 && cleanVideoId != currentLoadedId) {
            currentLoadedId = cleanVideoId
            youtubePlayer?.loadVideo(cleanVideoId, 0f)
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            val playerView = YouTubePlayerView(context).apply {
                layoutParams = android.view.ViewGroup.LayoutParams(
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                    android.view.ViewGroup.LayoutParams.MATCH_PARENT
                )
            }

            lifecycleOwner.lifecycle.addObserver(playerView)

            playerView.enableAutomaticInitialization = false
            val options = IFramePlayerOptions.Builder(context)
                .controls(1)
                .autoplay(1)
                .build()

            playerView.initialize(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youtubePlayer = youTubePlayer
                }
            }, options)

            playerView
        },
        onRelease = { view ->
            lifecycleOwner.lifecycle.removeObserver(view)
            view.release()
        }
    )
}