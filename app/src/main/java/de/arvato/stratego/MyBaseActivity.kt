package de.arvato.stratego

import android.app.Activity
import android.content.SharedPreferences
import android.content.res.Configuration
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.os.PowerManager
import android.os.PowerManager.WakeLock
import android.preference.PreferenceActivity
import android.util.Log
import android.view.*
import android.widget.Toast

class MyBaseActivity : Activity() {
    var _wakeLock: WakeLock? = null
    private var _onResumeTimeMillies: Long = 0
    var spSound: SoundPool? = null
    var _itickTock = 0
    var _ihorseNeigh = 0
    var _ismallNeigh = 0
    var _ihorseSnort = 0
    var _ihorseRunAway = 0
    var _imove = 0
    var _icapture = 0
    var _fVolume = 1.0f

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prepareWindowSettings()
        _wakeLock = wakeLock
        spSound = SoundPool(7, AudioManager.STREAM_MUSIC, 0)
        _itickTock = spSound!!.load(this, R.raw.ticktock, 1)
        _ihorseNeigh = spSound!!.load(this, R.raw.horseneigh, 1)
        _ismallNeigh = spSound!!.load(this, R.raw.smallneigh, 2)
        _ihorseSnort = spSound!!.load(this, R.raw.horsesnort, 1)
        _ihorseRunAway = spSound!!.load(this, R.raw.horserunaway, 1)
        _imove = spSound!!.load(this, R.raw.move, 1)
        _icapture = spSound!!.load(this, R.raw.capture, 1)
    }

    override fun onResume() {
        val prefs = prefs
        if (prefs.getBoolean("wakeLock", true)) {
            _wakeLock!!.acquire()
        }
        _onResumeTimeMillies = System.currentTimeMillis()
        super.onResume()
    }

    override fun onPause() {
        if (_wakeLock!!.isHeld) {
            _wakeLock!!.release()
        }
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // API 5+ solution
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    val prefs: SharedPreferences
        get() = getPrefs(this)

    val wakeLock: WakeLock
        get() {
            val pm = this.getSystemService(POWER_SERVICE) as PowerManager
            return pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "MyBaseActivity:DoNotDimScreen")
        }

    fun doToast(text: String?) {
        val t = Toast.makeText(this, text, Toast.LENGTH_LONG)
        t.setGravity(Gravity.BOTTOM, 0, 0)
        t.show()
    }

    fun soundTickTock() {
        spSound!!.play(_itickTock, _fVolume, _fVolume, 1, 0, 1f)
    }

    fun soundHorseNeigh() {
        spSound!!.play(_ihorseNeigh, _fVolume, _fVolume, 1, 0, 1f)
    }

    fun soundSmallNeigh() {
        spSound!!.play(_ismallNeigh, _fVolume, _fVolume, 2, 0, 1f)
    }

    fun soundHorseSnort() {
        spSound!!.play(_ihorseSnort, _fVolume, _fVolume, 1, 0, 1f)
    }

    fun soundHorseRunAway() {
        spSound!!.play(_ihorseRunAway, _fVolume, _fVolume, 1, 0, 1f)
    }

    fun soundMove() {
        spSound!!.play(_imove, _fVolume, _fVolume, 1, 0, 1f)
    }

    fun soundCapture() {
        spSound!!.play(_icapture, _fVolume, _fVolume, 1, 0, 1f)
    }

    private fun prepareWindowSettings() {
        Factory.prepareWindowSettings(this);
    }

    companion object Factory{
        const val TAG = "MyBaseActivity"
        // @see http://stackoverflow.com/questions/9739498/android-action-bar-not-showing-overflow
        @JvmOverloads
        fun makeActionOverflowMenuShown(activity: Activity?) { //devices with hardware menu button (e.g. Samsung Note) don't show action overflow menu
            try {
                val config = ViewConfiguration.get(activity)
                val menuKeyField =
                    ViewConfiguration::class.java.getDeclaredField("sHasPermanentMenuKey")
                if (menuKeyField != null) {
                    menuKeyField.isAccessible = true
                    menuKeyField.setBoolean(config, false)
                }
            } catch (e: Exception) {
                Log.d("main", e.localizedMessage)
            }
        }

        fun getPrefs(activity: Activity): SharedPreferences {
            return activity.getSharedPreferences("ChessPlayer", MODE_PRIVATE)
        }

        @JvmOverloads
        fun prepareWindowSettings(activity: Activity) {
            val prefs = getPrefs(activity)
            if (prefs.getBoolean("fullScreen", true)) {
                activity.window.setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
            }
            val configOrientation = activity.resources.configuration.orientation
            if (configOrientation == Configuration.ORIENTATION_LANDSCAPE) {
                if (false == activity is PreferenceActivity) {
                    activity.requestWindowFeature(Window.FEATURE_NO_TITLE)
                }
            } else {
                try {
                    activity.actionBar!!.setDisplayHomeAsUpEnabled(true)
                } catch (ex: Exception) {
                }
            }
        }
    }
}