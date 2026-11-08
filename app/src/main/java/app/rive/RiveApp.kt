package app.rive

import android.app.Application
import android.util.Log
import app.rive.data.local.AppDatabase
import app.rive.data.repository.HistoryRepository
import app.rive.data.repository.MovieRepository
import app.rive.data.repository.StreamRepository

class RiveApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var movieRepository: MovieRepository
        private set

    lateinit var streamRepository: StreamRepository
        private set

    lateinit var historyRepository: HistoryRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("RiveCrash", "Fatal exception in thread ${thread.name}", throwable)
        }

        try {
            database = AppDatabase.getInstance(this)
            movieRepository = MovieRepository()
            streamRepository = StreamRepository()
            historyRepository = HistoryRepository(database.historyDao(), database.bookmarkDao())
        } catch (e: Throwable) {
            Log.e("RiveCrash", "Error during RiveApp initialization", e)
        }
    }

    companion object {
        lateinit var instance: RiveApp
            private set
    }
}

