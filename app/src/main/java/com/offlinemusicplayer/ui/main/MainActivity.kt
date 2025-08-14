package com.offlinemusicplayer.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.offlinemusicplayer.MusicPlayerApplication
import com.offlinemusicplayer.R
import com.offlinemusicplayer.data.model.Track
import com.offlinemusicplayer.databinding.ActivityMainBinding
import com.offlinemusicplayer.scanner.MusicScanner
import com.offlinemusicplayer.service.MusicPlayerService
import com.offlinemusicplayer.ui.about.AboutActivity
import com.offlinemusicplayer.ui.adapter.TracksAdapter
import com.offlinemusicplayer.ui.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var tracksAdapter: TracksAdapter

    private var mediaController: MediaController? = null

    private val permissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted ->
            if (granted) {
                checkNotificationPermission()
                loadMusicFiles()
            } else {
                Snackbar.make(binding.root, getString(R.string.permission_denied), Snackbar.LENGTH_LONG).show()
            }
        }

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted ->
            if (!granted) {
                Snackbar
                    .make(
                        binding.root,
                        "Notification permission denied. Media controls may not work.",
                        Snackbar.LENGTH_LONG,
                    ).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewModel()
        setupRecyclerView()
        setupFab()
        checkPermissions()
        observeViewModel()
        initializeMediaController()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaController?.release()
        mediaController = null
    }

    private fun setupViewModel() {
        viewModel = MainViewModel((application as MusicPlayerApplication).repository)
    }

    private fun setupRecyclerView() {
        tracksAdapter =
            TracksAdapter(
                onTrackClick = ::onTrackClick,
                onMoreClick = ::onMoreClick,
            )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = tracksAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupFab() {
        binding.fabPlay.setOnClickListener {
            mediaController?.let { controller ->
                if (controller.isPlaying) {
                    controller.pause()
                } else {
                    controller.play()
                }
            } ?: run {
                Snackbar.make(binding.root, "Media service not ready", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean = false

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.updateSearchQuery(newText ?: "")
                    return true
                }
            },
        )
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.action_settings -> {
                Snackbar.make(binding.root, "Settings coming soon", Snackbar.LENGTH_SHORT).show()
                true
            }
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun checkPermissions() {
        val permission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                checkNotificationPermission()
                loadMusicFiles()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                showPermissionRationale()
            }
            else -> {
                permissionLauncher.launch(permission)
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showPermissionRationale() {
        Snackbar
            .make(
                binding.root,
                getString(R.string.permission_storage_rationale),
                Snackbar.LENGTH_INDEFINITE,
            ).setAction(getString(R.string.grant_permission)) {
                val permission =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Manifest.permission.READ_MEDIA_AUDIO
                    } else {
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    }
                permissionLauncher.launch(permission)
            }.show()
    }

    private fun loadMusicFiles() {
        // Initialize music scanner and start scanning
        lifecycleScope.launch {
            try {
                val scanner = MusicScanner(this@MainActivity, viewModel.repository)
                val count = scanner.scanMusicLibrary()
                Snackbar.make(binding.root, "Found $count music files", Snackbar.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Snackbar.make(binding.root, "Error scanning music: ${e.message}", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.tracks.collectLatest { pagingData ->
                tracksAdapter.submitData(pagingData)
            }
        }
    }

    private fun initializeMediaController() {
        val sessionToken = SessionToken(this, android.content.ComponentName(this, MusicPlayerService::class.java))
        val controllerFuture = MediaController.Builder(this, sessionToken).buildAsync()
        controllerFuture.addListener({
            try {
                mediaController = controllerFuture.get()
            } catch (e: Exception) {
                // Service not available
            }
        }, mainExecutor)
    }

    private fun onTrackClick(track: Track) {
        mediaController?.let { controller ->
            // Create MediaItem from Track and start playback
            val mediaItem =
                MediaItem
                    .Builder()
                    .setUri(track.uri)
                    .setMediaMetadata(
                        androidx.media3.common.MediaMetadata
                            .Builder()
                            .setTitle(track.title)
                            .setArtist(track.artist)
                            .setAlbumTitle(track.album)
                            .build(),
                    ).build()

            controller.setMediaItem(mediaItem)
            controller.prepare()
            controller.play()

            Snackbar.make(binding.root, "Playing: ${track.title}", Snackbar.LENGTH_SHORT).show()
        } ?: run {
            Snackbar.make(binding.root, "Media service not ready", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun onMoreClick(track: Track) {
        Snackbar.make(binding.root, "More options for: ${track.title}", Snackbar.LENGTH_SHORT).show()
    }
}
