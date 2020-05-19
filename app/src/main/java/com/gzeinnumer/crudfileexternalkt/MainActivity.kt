package com.gzeinnumer.crudfileexternalkt

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    var permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val enterText = findViewById<EditText>(R.id.enterText)

        findViewById<View>(R.id.save).setOnClickListener {
            if (!enterText.text.toString().isEmpty()) {
                makeFile(enterText.text.toString())
            } else {
                enterText.requestFocus()
                enterText.error = "Tidak Boleh Kosong"
            }
        }

        val readText = findViewById<EditText>(R.id.readText)
        findViewById<View>(R.id.read).setOnClickListener {
            if (checkPermissions()) {
                readText.setText(readFile())
            }
        }

        findViewById<View>(R.id.delete).setOnClickListener {
            if (checkPermissions()) {
                if (deleteFile()) {
                    enterText.setText("")
                    readText.setText("")
                    Toast.makeText(this@MainActivity, "Success hapus file", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@MainActivity, "Gagal hapus file", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun makeFile(text: String) {
        //untuk android 10, pakai ini
        //android:requestLegacyExternalStorage="true"
        if (checkPermissions()) {
            val file = File(Environment.getExternalStorageDirectory().toString() + "/CrudFileExternalkt")
            if (!file.exists()) {
                file.mkdirs()
            }
            try {
                val gpxfile = File(file, "sample.txt")
                val writer = FileWriter(gpxfile)
                writer.append(text)
                writer.flush()
                writer.close()
                Toast.makeText(this@MainActivity, "Saved your text", Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Add text to file " + e.message, Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Beri izin dulu", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readFile(): String {
        var myData = ""
        val myExternalFile = File(Environment.getExternalStorageDirectory().toString() + "/CrudFileExternalkt", "sample.txt")
        try {
            val fis = FileInputStream(myExternalFile)
            val `in` = DataInputStream(fis)
            val br = BufferedReader(InputStreamReader(`in`))
            var strLine: String
            while (br.readLine().also { strLine = it } != null) {
                myData = """
                    $myData$strLine
                    
                    """.trimIndent()
            }
            br.close()
            `in`.close()
            fis.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this@MainActivity, "Read text to file " + e.message, Toast.LENGTH_SHORT).show()
        }
        return myData
    }

    private fun deleteFile(): Boolean {
        val file = File(Environment.getExternalStorageDirectory().toString() + "/CrudFileExternalkt", "sample.txt")
        return file.delete()
    }


    var MULTIPLE_PERMISSIONS = 1

    private fun checkPermissions(): Boolean {
        var result: Int
        val listPermissionsNeeded: MutableList<String> = ArrayList()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(applicationContext, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (listPermissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (grantResults.size > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                val perStr = StringBuilder()
                for (per in permissions) {
                    perStr.append("\n").append(per)
                }
            }
        }
    }
}
