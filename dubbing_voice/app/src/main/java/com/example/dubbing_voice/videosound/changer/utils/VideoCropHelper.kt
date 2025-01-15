package com.example.dubbing_voice.videosound.changer.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import com.example.dubbing_voice.videosound.changer.bean.LocalVideoBean
import com.example.dubbing_voice.videosound.changer.utils.VideoFFCrop.FFListener
import com.example.dubbing_voice.videosound.changer.view.VHwCropView

object VideoCropHelper {
    const val WHA = 2 / 3f
     lateinit var destPath :String

    fun cropWpVideo(
        context: Context,
        videoBean: LocalVideoBean,
        mVCropView: VHwCropView,
        listener: FFListener
    ) {
        if (videoBean == null) {
            Log.e("TAG13", "cropWpVideo: videoBean=null");
            return
        }
        val srcVideo = videoBean.src_path.trim()
        var startPo = 0
        var duration = 0
        val srcW = videoBean.width
        val srcH = videoBean.height
        var width = 0
        var height = 0
        var x = 0
        var y = 0

        width = (srcH * WHA).toInt()
        height = srcH
        if (mVCropView != null) {
            val rectF = mVCropView.overlayView.cropViewRect
            x = (srcW * rectF.left / mVCropView.width).toInt()
            startPo = mVCropView.videoView.startPo / 1000
            duration = mVCropView.videoView.endPo / 1000 - startPo
        } else {
            x = (srcW - width) / 2
            startPo = 0
            duration = (videoBean.duration / 1000).toInt()
        }
        duration = if (duration <= 0) 1 else duration //最小为1
        y = 0
        Log.d("TAG13", "Media $videoBean====$x")
        var start = srcVideo.lastIndexOf(".")
        if (start == -1) {
            start = srcVideo.length
        }
        destPath = Environment.getExternalStorageDirectory().path +"/1234/cc.mp4"
        FileUtils.createDir(Environment.getExternalStorageDirectory().path +"/1234")
        VideoFFCrop.instance?.cropVideo(
            context,
            srcVideo,
            destPath,
            startPo,
            duration,
            listener
        )
    }


    fun getLocalVideoInfo(path: String?): LocalVideoBean {
        val info =
            LocalVideoBean()
        info.src_path = path
        val mmr = MediaMetadataRetriever()
        try {
            mmr.setDataSource(path)
            info.duration =
                java.lang.Long.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION))
            info.width =
                Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
            info.height =
                Integer.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            mmr.release()
        }
        return info
    }


    @JvmStatic
    fun getLocalVideoBitmap(
        path: String?,
        count: Int,
        width: Int,
        height: Int,
        listener: OnBitmapListener?
    ) {
        val task: AsyncTask<Any?, Any?, Any?> = object : AsyncTask<Any?, Any?, Any?>() {

            override fun doInBackground(vararg params: Any?): Any? {
                val mmr = MediaMetadataRetriever()
                try {
                    mmr.setDataSource(path)
                    val duration =
                        java.lang.Long.valueOf(mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)) * 1000
                    val inv = duration / count
                    var i: Long = 0
                    while (i < duration) {

                        val bitmap =
                            mmr.getFrameAtTime(i, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        val destBitmap = Bitmap.createScaledBitmap(bitmap!!, width, height, true)
                        Log.d(
                           "TAG13",
                            "getFrameAtTime " + i + "===" + destBitmap.width + "===" + destBitmap.height
                        )
                        bitmap.recycle()
                        publishProgress(destBitmap)
                        i += inv
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                } finally {
                    mmr.release()
                }
                return null
            }

            override fun onProgressUpdate(vararg values: Any?) {
                listener?.onBitmapGet(values[0] as Bitmap)
            }

            override fun onPostExecute(result: Any?) {}
        }
        task.execute()
    }

    interface OnBitmapListener {
        fun onBitmapGet(bitmap: Bitmap?)
    }
}