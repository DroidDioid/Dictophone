package ru.tim.dictophone.player

import android.app.Application
import androidx.lifecycle.*
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSource

/**
 * ViewModel для диалогового окна проигрывателя [PlayerFragment].
 * Реализует [DefaultLifecycleObserver] для отслеживания жизненного цикла приложения,
 * чтобы задавать и сбрасывать плеер.
 * @property itemPath путь к медиафайлу
 * @param app для определения контекста
 */
class PlayerViewModel(private val itemPath: String?, app: Application) :
    AndroidViewModel(app),
    DefaultLifecycleObserver {

    private val _player = MutableLiveData<Player?>()
    val player: LiveData<Player?>
        get() = _player
    private var contentPosition = 0L
    private var playWhenReady = true

    init {
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        setUpPlayer()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        releaseExoPlayer()
    }

    override fun onCleared() {
        super.onCleared()
        releaseExoPlayer()
        ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
    }

    /** Создаёт и настраивает плеер. */
    private fun setUpPlayer() {

        val dataSourceFactory = DefaultDataSource.Factory(getApplication())

        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(itemPath ?: return))

        val player = ExoPlayer.Builder(getApplication()).build()

        player.addMediaSource(mediaSource)
        player.playWhenReady = playWhenReady
        player.seekTo(contentPosition)

        _player.value = player
    }

    /** Сбрасывает плеер. */
    private fun releaseExoPlayer() {
        val player = _player.value ?: return
        _player.value = null

        contentPosition = player.contentPosition
        playWhenReady = player.playWhenReady
        player.release()
    }

}