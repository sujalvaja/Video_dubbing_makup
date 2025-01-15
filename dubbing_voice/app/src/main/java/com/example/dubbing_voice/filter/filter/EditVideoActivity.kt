package com.example.dubbing_voice.filter.filter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Window
import android.view.WindowManager
import com.example.dubbing_voice.R

class EditVideoActivity : AppCompatActivity() {

    companion object {
        const val BUNDLE_KEY_URI = "Filepath"

        fun newIntent(context: Context?, uri: String?) : Intent {
            val intent = Intent(context, EditVideoActivity::class.java)
            val bundle = Bundle()
            bundle.putString(BUNDLE_KEY_URI, uri)
            intent.putExtras(bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_edit_video)

        savedInstanceState ?: supportFragmentManager.beginTransaction()
                .replace(R.id.container,
                    FilterVideoFragment.newInstance(intent.extras?.getString(BUNDLE_KEY_URI).toString())
                )
                .commit()
    }

}