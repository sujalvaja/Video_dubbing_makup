package com.example.dubbing_voice.videosound.changer.Activity

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.dubbing_voice.R
import com.example.dubbing_voice.videosound.changer.AppClass
import com.example.dubbing_voice.videosound.changer.bean.LocalVideoBean
import com.example.dubbing_voice.videosound.changer.utils.VideoCropHelper
import com.example.dubbing_voice.videosound.changer.utils.VideoFFCrop
import com.example.dubbing_voice.videosound.changer.view.VDurationCutView
import com.example.dubbing_voice.videosound.changer.view.VHwCropView
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID

class VideoCropPreActivity : AppCompatActivity(), View.OnClickListener,
    VDurationCutView.IOnRangeChangeListener {

    private var srcVideo = Environment.getExternalStorageDirectory().absolutePath+ ".mp4"

    private var mVCropView: VHwCropView? = null
    private var mCutView: VDurationCutView? = null
    private var mCropBtn: ImageView? = null
    private var ic_done: ImageView? = null
    private var img_back: ImageView? = null
    private var mLocalVideoInfo: LocalVideoBean? = null



    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VideoFFCrop.instance?.init(this)
        setContentView(R.layout.video_crop)

        srcVideo = intent.extras?.getString("Filepath").toString()

        mVCropView = findViewById<View>(R.id.crop_view) as VHwCropView
        mCutView = findViewById<View>(R.id.cut_view) as VDurationCutView
        mCropBtn = findViewById<View>(R.id.tv_ok) as ImageView
        ic_done = findViewById<View>(R.id.ic_done) as ImageView
        img_back = findViewById<View>(R.id.img_back) as ImageView
        mCropBtn!!.setOnClickListener(this)
        mCutView!!.setRangeChangeListener(this)

        ic_done!!.setOnClickListener(View.OnClickListener { saveVideoToInternalStorage() })
        img_back!!.setOnClickListener(View.OnClickListener { onBackPressed() })

        mLocalVideoInfo = VideoCropHelper.getLocalVideoInfo(srcVideo)
        this.mLocalVideoInfo?.duration?.let {
            mVCropView!!.videoView.setLocalPath(
                srcVideo,
                it.toInt()
            )
        }
        mCutView!!.setMediaFileInfo(mLocalVideoInfo)

    }

    private fun saveVideoToInternalStorage() {
        val uuid = UUID.randomUUID()
        try {
            val currentFile: File = File(VideoCropHelper.destPath)
            val externalStoragePublicDirectory = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES + File.separator + AppClass.folder_name + "/Trim_video"
            )
            if (!externalStoragePublicDirectory.exists()) {
                externalStoragePublicDirectory.mkdirs()
            }
            val fileName = String.format("$uuid.mp4")
            val newfile = File(externalStoragePublicDirectory, fileName)
            if (currentFile.exists()) {
                val inputStream: InputStream = FileInputStream(currentFile)
                val outputStream: OutputStream = FileOutputStream(newfile)
                val buf = ByteArray(1024)
                var len: Int
                while (inputStream.read(buf).also { len = it } > 0) {
                    outputStream.write(buf, 0, len)
                }

                outputStream.flush()
                inputStream.close()
                outputStream.close()
                Toast.makeText(applicationContext, "Video has just saved!!", Toast.LENGTH_LONG)
                    .show()
                CreateVideoScreen()
            } else {
                Toast.makeText(
                    applicationContext,
                    "Video has failed for saving!!",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onResume() {
        super.onResume()
        mVCropView?.videoView?.start()
    }

    override fun onPause() {
        super.onPause()
        mVCropView?.videoView?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mVCropView?.videoView?.stopPlayback()
    }

    override fun onClick(v: View) {
        Log.d(
            "TAG13",
            "mVideoView " + mLocalVideoInfo?.width + "===" + mLocalVideoInfo?.height
        )
        VideoCropHelper.cropWpVideo(
            this@VideoCropPreActivity,
            mLocalVideoInfo!!,
            mVCropView!!,
            object : VideoFFCrop.FFListener {
                override fun onProgress(progress: Int?) {
                    Log.d("TAG13", "progress: $progress")

                }


                override fun onFinish() {
                    Log.d("TAG13", "finished")
                    Toast.makeText(applicationContext ,"video trim finish ",Toast.LENGTH_LONG)
                    saveVideoToInternalStorage()

                }

                override fun onFail(msg: String?) {
                    Log.d("TAG13", "failed")
                }
            })
    }

    override fun onKeyDown() {}
    override fun onKeyUp(startTime: Int, endTime: Int) {
        mVCropView!!.setStarEndPo(startTime, endTime)
    }
    fun CreateVideoScreen() {
        val intent = Intent(this, VideoViewActivity::class.java)
        intent.putExtra("videopath",VideoCropHelper.destPath)
        Log.d("TAG12", "VideoEdit file path" + VideoCropHelper.destPath);
        intent.putExtra("frompage", "Mute")
        startActivity(intent)
    }
}