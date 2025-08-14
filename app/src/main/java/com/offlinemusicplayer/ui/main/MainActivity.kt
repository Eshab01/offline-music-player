package com.offlinemusicplayer.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.offlinemusicplayer.MusicPlayerApplication
import com.offlinemusicplayer.R
import com.offlinemusicplayer.data.model.Track
import com.offlinemusicplayer.databinding.ActivityMainBinding
import com.offlinemusicplayer.ui.about.AboutActivity
import com.offlinemusicplayer.ui.adapter.TracksAdapter
import com.offlinemusicplayer.ui.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tracksAdapter: TracksAdapter
    
    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Factory((application as MusicPlayerApplication).repository)
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadMusicFiles()
        } else {
            showPermissionRationale()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        
        setupRecyclerView()
        setupFab()
        setupSearch()
        checkPermissions()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        tracksAdapter = TracksAdapter(
            onTrackClick = ::onTrackClick,
            onMoreClick = ::onMoreClick
        )
        
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = tracksAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupFab() {
        binding.fabPlay.setOnClickListener {
            // TODO: Handle play/pause action
            Snackbar.make(binding.root, "Play/Pause functionality coming soon", Snackbar.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.updateSearchQuery(newText ?: "")
                return true
            }
        })
        
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // TODO: Open settings
                Snackbar.make(binding.root, "Settings coming soon", Snackbar.LENGTH_SHORT).show()
                true
            }
            R.id.action_about -> {
                startActivity(Intent(this, AboutActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupSearch() {
        // Search setup is now handled in onCreateOptionsMenu
    }

    private fun checkPermissions() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
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

    private fun showPermissionRationale() {
        Snackbar.make(
            binding.root,
            getString(R.string.permission_storage_rationale),
            Snackbar.LENGTH_INDEFINITE
        ).setAction(getString(R.string.grant_permission)) {
            val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Manifest.permission.READ_MEDIA_AUDIO
            } else {
                Manifest.permission.READ_EXTERNAL_STORAGE
            }
            permissionLauncher.launch(permission)
        }.show()
    }

    private fun loadMusicFiles() {
        // TODO: Implement music file scanning
        Snackbar.make(binding.root, "Music scanning functionality coming soon", Snackbar.LENGTH_SHORT).show()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.tracks.collectLatest { pagingData ->
                tracksAdapter.submitData(pagingData)
            }
        }
    }

    private fun onTrackClick(track: Track) {
        // TODO: Start playback
        Snackbar.make(binding.root, "Playing: ${track.displayTitle}", Snackbar.LENGTH_SHORT).show()
    }

    private fun onMoreClick(track: Track) {
        // TODO: Show options menu
        Snackbar.make(binding.root, "More options for: ${track.displayTitle}", Snackbar.LENGTH_SHORT).show()
    }
}