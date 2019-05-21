package com.example.artur.drawingapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.squareup.seismic.ShakeDetector
import kotlinx.android.synthetic.main.activity_drawing.*
import yuku.ambilwarna.AmbilWarnaDialog
import java.text.SimpleDateFormat
import java.util.*

private val TAG = "Permission"
private val REQUEST_CODE = 101
var rubberActive: Boolean = false
var boldActive: Boolean = false
var color: Int = Color.BLACK

class DrawingActivity : AppCompatActivity(), ShakeDetector.Listener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing)

        setupPermissions()

        val canvas = Canvas(this)

        var sensorManager: SensorManager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        var shakeDetector = ShakeDetector(this)
        shakeDetector.start(sensorManager)

        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar!!.title = "Paint"

        actionBar.setLogo(R.mipmap.ic_launcher)
        actionBar.setDisplayShowTitleEnabled(true)

        saveBt.setOnClickListener() {
            canvas.saveCanvas()
            val toast: Toast = Toast.makeText(this, "Image saved", Toast.LENGTH_LONG)
            toast.show()
        }
    }

    override fun hearShake() {
        clearCanvas()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_rubber -> {
                if (rubberActive == false) {
                    item.setIcon(R.drawable.brush)
                    rubberActive = true
                } else {
                    item.setIcon(R.drawable.rubber)
                    rubberActive = false
                }
                return true
            }
            R.id.action_bold -> {
                if (boldActive == false) {
                    item.setIcon(R.drawable.line)
                    boldActive = true
                } else {
                    item.setIcon(R.drawable.bold)
                    boldActive = false
                }
                return true
            }
            R.id.action_palette -> {
                openColorPicker()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openColorPicker() {
        val colorPicker = AmbilWarnaDialog(this, color, object : AmbilWarnaDialog.OnAmbilWarnaListener {
            override fun onCancel(dialog: AmbilWarnaDialog) {

            }

            override fun onOk(dialog: AmbilWarnaDialog, colorPicked: Int) {
                color = colorPicked
            }
        })
        colorPicker.show()
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission has been denied by user")
                    finish()
                } else {
                    Log.i(TAG, "Permission has been granted by user")
                }
            }
        }
    }
}
