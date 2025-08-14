package com.offlinemusicplayer.equalizer

import android.media.audiofx.Equalizer
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EqualizerController(audioSessionId: Int) {
    private var equalizer: Equalizer? = null
    private val _isAvailable = MutableStateFlow(false)
    val isAvailable: StateFlow<Boolean> = _isAvailable.asStateFlow()
    
    private val _isEnabled = MutableStateFlow(false)
    val isEnabled: StateFlow<Boolean> = _isEnabled.asStateFlow()
    
    private val _presets = MutableStateFlow<List<String>>(emptyList())
    val presets: StateFlow<List<String>> = _presets.asStateFlow()
    
    private val _currentPreset = MutableStateFlow<String?>(null)
    val currentPreset: StateFlow<String?> = _currentPreset.asStateFlow()
    
    private val _bands = MutableStateFlow<List<EqualizerBand>>(emptyList())
    val bands: StateFlow<List<EqualizerBand>> = _bands.asStateFlow()

    data class EqualizerBand(
        val index: Short,
        val centerFreq: Int,
        val level: Short,
        val minLevel: Short,
        val maxLevel: Short
    )

    init {
        try {
            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = false
            }
            _isAvailable.value = true
            initializeBands()
            initializePresets()
        } catch (e: Exception) {
            _isAvailable.value = false
        }
    }

    private fun initializeBands() {
        equalizer?.let { eq ->
            val bandList = mutableListOf<EqualizerBand>()
            val numberOfBands = eq.numberOfBands
            
            for (i in 0 until numberOfBands) {
                val band = i.toShort()
                bandList.add(
                    EqualizerBand(
                        index = band,
                        centerFreq = eq.getCenterFreq(band),
                        level = eq.getBandLevel(band),
                        minLevel = eq.bandLevelRange[0],
                        maxLevel = eq.bandLevelRange[1]
                    )
                )
            }
            _bands.value = bandList
        }
    }

    private fun initializePresets() {
        equalizer?.let { eq ->
            val presetList = mutableListOf<String>()
            val numberOfPresets = eq.numberOfPresets
            
            for (i in 0 until numberOfPresets) {
                presetList.add(eq.getPresetName(i.toShort()))
            }
            _presets.value = presetList
        }
    }

    fun setEnabled(enabled: Boolean) {
        equalizer?.let { eq ->
            eq.enabled = enabled
            _isEnabled.value = enabled
        }
    }

    fun setBandLevel(bandIndex: Short, level: Short) {
        equalizer?.let { eq ->
            if (_isEnabled.value) {
                eq.setBandLevel(bandIndex, level)
                updateBandLevel(bandIndex, level)
            }
        }
    }

    fun usePreset(presetIndex: Short) {
        equalizer?.let { eq ->
            if (_isEnabled.value && presetIndex < eq.numberOfPresets) {
                eq.usePreset(presetIndex)
                _currentPreset.value = eq.getPresetName(presetIndex)
                // Update band levels to reflect the preset
                initializeBands()
            }
        }
    }

    fun usePreset(presetName: String) {
        equalizer?.let { eq ->
            val presetIndex = _presets.value.indexOf(presetName).toShort()
            if (presetIndex >= 0) {
                usePreset(presetIndex)
            }
        }
    }

    private fun updateBandLevel(bandIndex: Short, level: Short) {
        val currentBands = _bands.value.toMutableList()
        val bandIndexInt = bandIndex.toInt()
        if (bandIndexInt < currentBands.size) {
            currentBands[bandIndexInt] = currentBands[bandIndexInt].copy(level = level)
            _bands.value = currentBands
        }
        // Clear current preset since we're manually adjusting
        _currentPreset.value = null
    }

    fun release() {
        try {
            equalizer?.release()
        } catch (e: Exception) {
            // Ignore
        }
        equalizer = null
        _isAvailable.value = false
    }

    companion object {
        fun isEqualizerAvailable(): Boolean {
            return try {
                val testEqualizer = Equalizer(0, 0)
                testEqualizer.release()
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}