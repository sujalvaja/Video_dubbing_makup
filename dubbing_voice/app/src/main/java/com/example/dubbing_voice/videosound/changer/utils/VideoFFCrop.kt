package com.example.dubbing_voice.videosound.changer.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*


@SuppressLint("StaticFieldLeak")
class VideoFFCrop private constructor() {

    fun init(context: Context) {
        val task: AsyncTask<Any?, Any?, Any?> =
            object : AsyncTask<Any?, Any?, Any?>() {
                override fun doInBackground(vararg params: Any?): Any? {
                    val executablePath = context.applicationInfo.nativeLibraryDir + "/ffmpeg.so"
                    Log("TAG13", "initializing...")
                    var ffcutSrc: InputStream? = null
                    var ffcutDest: FileOutputStream? = null
                    try {
                        val exFile = File(executablePath)
                        ffcutSrc = context.assets.open("ffmpeg.so")
                        if (exFile != null && exFile.exists() && ffcutSrc.available()
                                .toLong() == exFile.length()
                        ) {
                            Log("TAG13", "initialized already...")
                            return null
                        }
                        ffcutDest = FileOutputStream(executablePath)
                        Log("TAG13", "copying executable...")
                        val buf = ByteArray(96 * 1024)
                        var length = 0
                        while (ffcutSrc.read(buf).also { length = it } != -1) {
                            ffcutDest.write(buf, 0, length)
                        }
                        ffcutDest.flush()
                        ffcutDest.close()
                        ffcutSrc.close()
                        Log("TAG13", "executable is copyed, applying permissions...")
                        val chmod =
                            Runtime.getRuntime().exec("/system/bin/chmod 755 $executablePath")
                        chmod.waitFor()
                        Log("TAG13", "ffcut is initialized")
                    } catch (e: Exception) {
                        Log(
                            "TAG13",
                            "ffcut initialization is failed, " + e.javaClass.name + ": " + e.message
                        )
                    } finally {
                        if (ffcutSrc != null) {
                            try {
                                ffcutSrc.close()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                        if (ffcutDest != null) {
                            try {
                                ffcutDest.close()
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                    return null
                }

                override fun onProgressUpdate(vararg values: Any?) {}
                override fun onPostExecute(result: Any?) {}
            }
        task.execute()
    }

    fun cropVideo(
        context: Context, srcVideoPath: String, destPath: String, start: Int,
        duration: Int, listener: FFListener?
    ) {
        val task: AsyncTask<String?, Int?, Int> = object : AsyncTask<String?, Int?, Int>() {
            override fun doInBackground(vararg params: String?): Int? {
                val cmd = params[0]
                Log("TAG13", "running command $cmd")
                return try {
                    val ffcut = Runtime.getRuntime().exec(cmd)
                    val error = ffcut.errorStream
                    val errorScanner = Scanner(error)
                    var count = 0
                    while (errorScanner.hasNextLine()) {
                        val line = errorScanner.nextLine()
                        Log("TAG13", "ffmpeg: $line")
                        publishProgress(++count)
                    }
                    ffcut.waitFor()
                } catch (e: Exception) {
                    Log("TAG13", "exception " + e.javaClass.name + ": " + e.message)
                    200
                }
            }

            override fun onProgressUpdate(vararg values: Int?) {

                val fz = values[0]
                val fm = duration * 1000 / 100
                var progress = (fz?.times(100) ?: 0) / fm
                progress = if (progress > 99) 99 else progress
                listener?.onProgress(progress)
            }

            override fun onPostExecute(result: Int) {
                Log("TAG13", "ffmpeg is finished with code $result")
                if (result == 0) {
                    if (listener != null) {
                        listener.onProgress(100)
                        listener.onFinish()
                        Log("TAG13", "finished ，video path = $destPath")

                        Toast.makeText(context, "video path = $destPath", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    listener?.onFail("crop doInBackground exception")
                }
            }

            override fun onCancelled() {
                listener?.onFail("crop canceled")
            }
        }

        val cmd = (context.applicationInfo.nativeLibraryDir + "/ffmpeg.so" + " -y -i "
                + "" + srcVideoPath + ""
                + " -ss " + start + " -t " + duration //                + " -strict -2 -vf crop=" + width + ":" + height + ":" + x + ":" + y + " -preset fast "
                + " -c copy "
                + "" + destPath + "")
        android.util.Log.d("TAG13", "cropVideo: $cmd")
        task.execute(cmd)

    }


    fun Log(TAG: String?, msg: String?) {
        if (isDebug) {
            android.util.Log.d("TAG13", msg!!)
        }
    }

    interface FFListener {

        fun onProgress(progress: Int?)


        fun onFinish()


        fun onFail(msg: String?)
    }

    companion object {
        const val TAG = "VideoFFCrop"
        private var mVideoFFCrop: VideoFFCrop? = null
        private const val isDebug = true

        val instance: VideoFFCrop?
            get() {
                if (mVideoFFCrop == null) {
                    synchronized(VideoFFCrop::class.java) {
                        if (mVideoFFCrop == null) {
                            mVideoFFCrop = VideoFFCrop()
                        }
                    }
                }
                return mVideoFFCrop
            }
    }
}