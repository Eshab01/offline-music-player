package com.offlinemusicplayer.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.offlinemusicplayer.BuildConfig
import com.offlinemusicplayer.R
import com.offlinemusicplayer.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBar()
        setupViews()
        setupClickListeners()
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.about)
        }
    }

    private fun setupViews() {
        binding.apply {
            versionText.text = getString(R.string.version) + " " + BuildConfig.VERSION_NAME
            buildTypeText.text = getString(R.string.build_type) + " " + BuildConfig.BUILD_TYPE.replaceFirstChar { it.uppercase() }
        }
    }

    private fun setupClickListeners() {
        binding.privacyPolicyButton.setOnClickListener {
            // TODO: Replace with actual privacy policy URL
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com/privacy"))
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
