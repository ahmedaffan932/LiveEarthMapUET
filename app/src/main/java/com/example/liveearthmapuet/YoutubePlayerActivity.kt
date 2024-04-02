package com.example.liveearthmapuet

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.liveearthmapuet.classes.Misc.Companion.setAppLanguage
import com.example.liveearthmapuet.databinding.ActivityYoutubePlayerBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class YoutubePlayerActivity : AppCompatActivity() {
    lateinit var binding: ActivityYoutubePlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAppLanguage()
        binding = ActivityYoutubePlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val youtubePlayerView = YouTubePlayerView(this)
//        youtubePlayerView.enableAutomaticInitialization = false

        binding.root.addView(youtubePlayerView)

        lifecycle.addObserver(youtubePlayerView)

        youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = intent.getStringExtra("video_id").toString()
                youTubePlayer.loadVideo(videoId, 0f)

            }
        })


//        val options: IFramePlayerOptions =
//            IFramePlayerOptions.Builder().fullscreen(1).controls(1).build()
//        youtubePlayerView.initialize(listener, options)
//
//        youtubePlayerView.addFullscreenListener(object : FullscreenListener{
//            override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
//
//            }
//
//            override fun onExitFullscreen() {
//
//            }
//
//        })
    }
}