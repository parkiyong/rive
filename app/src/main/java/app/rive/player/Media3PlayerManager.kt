package app.rive.player

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import app.rive.data.model.StreamCaption
import app.rive.data.model.StreamSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class PlayerUiState(
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = true,
    val currentPositionMs: Long = 0L,
    val durationMs: Long = 0L,
    val errorMessage: String? = null,
    val selectedQuality: String? = null
)

@OptIn(UnstableApi::class)
class Media3PlayerManager(
    private val context: Context,
    private val onProgressUpdate: (positionMs: Long, durationMs: Long) -> Unit
) {
    private var exoPlayer: ExoPlayer? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var progressJob: Job? = null

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private val httpDataSourceFactory = DefaultHttpDataSource.Factory()
        .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
        .setAllowCrossProtocolRedirects(true)
        .setConnectTimeoutMs(15000)
        .setReadTimeoutMs(20000)

    fun initializePlayer(startPositionMs: Long = 0L): ExoPlayer {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                playWhenReady = true
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        val isBuffering = playbackState == Player.STATE_BUFFERING
                        _uiState.value = _uiState.value.copy(
                            isBuffering = isBuffering,
                            durationMs = duration.coerceAtLeast(0L)
                        )
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
                        if (isPlaying) {
                            startProgressTracking()
                        } else {
                            progressJob?.cancel()
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = error.localizedMessage ?: "Playback error",
                            isBuffering = false
                        )
                    }
                })
            }
        }
        if (startPositionMs > 0) {
            exoPlayer?.seekTo(startPositionMs)
        }
        return exoPlayer!!
    }

    fun playStream(
        source: StreamSource,
        captions: List<StreamCaption>? = null,
        initialPositionMs: Long = 0L
    ) {
        val player = initializePlayer(initialPositionMs)
        val uri = Uri.parse(source.url)

        val subtitleConfigs = captions?.mapNotNull { cap ->
            try {
                MediaItem.SubtitleConfiguration.Builder(Uri.parse(cap.file))
                    .setMimeType(if (cap.type == "srt") MimeTypes.APPLICATION_SUBRIP else MimeTypes.TEXT_VTT)
                    .setLanguage(cap.language ?: "en")
                    .setLabel(cap.label ?: "English")
                    .setSelectionFlags(if (cap.language?.equals("en", true) == true) C.SELECTION_FLAG_DEFAULT else 0)
                    .build()
            } catch (_: Exception) {
                null
            }
        } ?: emptyList()

        val mediaItem = MediaItem.Builder()
            .setUri(uri)
            .setSubtitleConfigurations(subtitleConfigs)
            .build()

        val mediaSource: MediaSource = if (source.isHls) {
            HlsMediaSource.Factory(httpDataSourceFactory)
                .setAllowChunklessPreparation(true)
                .createMediaSource(mediaItem)
        } else {
            ProgressiveMediaSource.Factory(httpDataSourceFactory)
                .createMediaSource(mediaItem)
        }

        player.setMediaSource(mediaSource)
        player.prepare()
        if (initialPositionMs > 0) {
            player.seekTo(initialPositionMs)
        }
        player.play()

        _uiState.value = _uiState.value.copy(
            selectedQuality = source.quality,
            errorMessage = null
        )
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                exoPlayer?.let { player ->
                    val pos = player.currentPosition
                    val dur = player.duration.coerceAtLeast(0L)
                    _uiState.value = _uiState.value.copy(
                        currentPositionMs = pos,
                        durationMs = dur
                    )
                    if (dur > 0 && pos > 0) {
                        onProgressUpdate(pos, dur)
                    }
                }
                delay(1000)
            }
        }
    }

    fun seekTo(positionMs: Long) {
        exoPlayer?.seekTo(positionMs)
    }

    fun togglePlayPause() {
        exoPlayer?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun release() {
        progressJob?.cancel()
        exoPlayer?.release()
        exoPlayer = null
    }
}

