package de.arvato.stratego

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.Toast
import java.util.*

/*import jwtc.android.chess.ics.ICSClient
import jwtc.android.chess.puzzle.practice
import jwtc.android.chess.puzzle.puzzle
import jwtc.android.chess.tools.pgntool*/


class MainActivity : Activity() {
    val TAG = "start"
    var _ssActivity = ""
    var _list: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val getData =
            getSharedPreferences("ChessPlayer", Context.MODE_PRIVATE)
        var myLanguage = getData.getString("localelanguage", "")
        val current = resources.configuration.locale
        val language = current.language
        if (myLanguage == "") { // localelanguage not used yet? then use device default locale
            myLanguage = language
        }
        val locale =
            Locale(myLanguage) // myLanguage is current language
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(
            config,
            baseContext.resources.displayMetrics
        )
        setContentView(R.layout.activity_main)
        if (intent.getBooleanExtra("RESTART", false)) {
            finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        val title =
            resources.getStringArray(R.array.start_menu)
        _list = findViewById<View>(R.id.ListStart) as ListView
        _list!!.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                _ssActivity = parent.getItemAtPosition(position).toString()
                try {
                    val i = Intent()
                    Log.i("start", _ssActivity)
                    if (_ssActivity == getString(R.string.start_play)) {
                        i.setClass(this@MainActivity, StartPlayActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        startActivity(i)
                    } else if (_ssActivity == getString(R.string.start_practice)) {
                        //i.setClass(this@MainActivity, practice::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        startActivity(i)
                    } else if (_ssActivity == getString(R.string.start_puzzles)) {
                        //i.setClass(this@MainActivity, puzzle::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        startActivity(i)
                    } else if (_ssActivity == getString(R.string.start_about)) {
                        i.setClass(this@MainActivity, HtmlActivity::class.java)
                        i.putExtra(HtmlActivity.HELP_MODE, "about")
                        startActivity(i)
                    } else if (_ssActivity == getString(R.string.start_ics)) {
                        //i.setClass(this@start, ICSClient::class.java)
                        //startActivity(i)
                    } else if (_ssActivity == getString(R.string.start_pgn)) {
                        //i.setClass(this@start, pgntool::class.java)
                        //i.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                        //startActivity(i)
                    } else if (_ssActivity == getString(R.string.start_globalpreferences)) {
                        //i.setClass(this@start, ChessPreferences::class.java)
                        //startActivityForResult(i, 0)
                    } else if (_ssActivity == getString(R.string.menu_help)) {
                        i.setClass(this@MainActivity, HtmlActivity::class.java)
                        i.putExtra(HtmlActivity.HELP_MODE, "help")
                        startActivity(i)
                    }
                } catch (ex: Exception) {
                    val t: Toast = Toast.makeText(
                        this@MainActivity,
                        R.string.toast_could_not_start_activity,
                        Toast.LENGTH_LONG
                    )
                    t.setGravity(Gravity.BOTTOM, 0, 0)
                    t.show()
                }
            }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (resultCode == 1) {
            Log.i(TAG, "finish and restart")
            val intent = Intent(this, MainActivity::class.java)
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.putExtra("RESTART", true)
            startActivity(intent)
        }
    }

    override fun onPostResume() {
        super.onPostResume()
        val getData =
            getSharedPreferences("ChessPlayer", Context.MODE_PRIVATE)
        if (getData.getBoolean("RESTART", false)) {
            finish()
            val intent = Intent(this, MainActivity::class.java)
            //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            val editor = getData.edit()
            editor.putBoolean("RESTART", false)
            editor.apply()
            startActivity(intent)
        }
    }
    /*override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)

    fab.setOnClickListener { view ->
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }
}

override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
}

override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    return when (item.itemId) {
        R.id.action_settings -> true
        else -> super.onOptionsItemSelected(item)
    }
}*/
}
