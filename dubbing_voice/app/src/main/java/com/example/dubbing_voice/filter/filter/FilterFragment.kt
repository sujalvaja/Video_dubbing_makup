package com.example.dubbing_voice.filter.filter

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.daasuu.epf.EPlayerView
import com.daasuu.mp4compose.FillMode
import com.daasuu.mp4compose.Rotation
import com.daasuu.mp4compose.composer.Mp4Composer
import com.daasuu.mp4compose.composer.Mp4Composer.Listener
import com.example.dubbing_voice.R
import com.example.dubbing_voice.filter.filter.adapter.AddFilterAdapter
import com.example.dubbing_voice.filter.filter.interfaces.AddFilterListener
import com.example.dubbing_voice.filter.filter.utils.FilterSave
import com.example.dubbing_voice.filter.filter.utils.FilterType
import com.example.dubbing_voice.videosound.changer.Activity.SelectVideoActivity
import com.example.dubbing_voice.videosound.changer.Activity.VideoViewActivity
import com.example.dubbing_voice.videosound.changer.AppClass
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource.Factory
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import java.io.File

class FilterVideoFragment : Fragment(), AddFilterListener {

    private lateinit var mAppName: String
    private lateinit var mAppPath: File

    lateinit var player: SimpleExoPlayer
    lateinit var ePlayerView: EPlayerView

    private lateinit var filepath : String
    private lateinit var filename : String
    private lateinit var filterFilepath : String

    private var mPosition : Int = 0

    companion object {
        const val ARG_KEY_URI = "Filepath"

        fun newInstance(uri: String): FilterVideoFragment {
            val fragment = FilterVideoFragment()
            val bundle = Bundle()
            bundle.putString(ARG_KEY_URI, uri)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_preview_filter, container, false)
    }
     lateinit var toolbar : Toolbar
      var MUTE_AUDIO = "mute"

     lateinit var layoutAdapter : RelativeLayout
     lateinit var filterView : FrameLayout
     lateinit var recyclerView : RecyclerView
     lateinit var ic_done : ImageView
     lateinit var img_back : ImageView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar = view.findViewById(R.id.toolbar)

        filterView = view.findViewById(R.id.filterView)
        layoutAdapter = view.findViewById(R.id.layoutAdapter)
        recyclerView = view.findViewById(R.id.recyclerView)
        ic_done = view.findViewById(R.id.ic_done)
        img_back = view.findViewById(R.id.img_back)
        img_back.setOnClickListener(View.OnClickListener { activity?.onBackPressed() })
        setToolbar()
        ic_done.setOnClickListener(View.OnClickListener { saveVideoWithFilter() })
        mAppName = AppClass.folder_name +"/Filter_video"
        mAppPath = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), mAppName)

        filepath = arguments?.getString(ARG_KEY_URI) ?: ""
        filename = filepath.substring(filepath.lastIndexOf("/")+1)
        filterFilepath = "$mAppPath/Filter_video_$filename"

        setHasOptionsMenu(true)


        val adapter = AddFilterAdapter(this, filepath)

        recyclerView.apply {
            isNestedScrollingEnabled = false
            layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
            this.adapter = adapter
            onFlingListener = null
        }

        adapter.notifyDataSetChanged()
    }

    private fun setToolbar() {
        val supportToolbar = toolbar as Toolbar
        (activity as AppCompatActivity).setSupportActionBar(supportToolbar)
        supportToolbar.apply {
            navigationIcon = ContextCompat.getDrawable(context, R.drawable.back_icn)
            setNavigationOnClickListener {
                activity?.onBackPressed()
            }
        }
        activity?.title = ""
    }


    lateinit var loading_dialog : Dialog
    lateinit var dialog_txt_loading_process : TextView

    private fun LoadingDialog() {
        val dialog = Dialog(requireContext(), R.style.TransparentBackground)
        loading_dialog = dialog
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        this.loading_dialog.setContentView(R.layout.dialog_video_create_loader)
        this.dialog_txt_loading_process =
            this.loading_dialog.findViewById<View>(R.id.dialog_loading_process) as TextView

        this.loading_dialog.show()
        (this.loading_dialog.findViewById<View>(R.id.dialog_btn_cancel) as RelativeLayout).setOnClickListener {

            deleteDir(File(this.filterFilepath))

            loading_dialog.dismiss()

        }
    }

    private fun saveVideoWithFilter() {

        if (!mAppPath.exists()) {
            mAppPath.mkdirs()
        }
        LoadingDialog()


        if (mPosition != 0) {
            Mp4Composer(filepath, filterFilepath)
                    .rotation(Rotation.NORMAL)
                    .fillMode(FillMode.PRESERVE_ASPECT_FIT)
                    .filter(FilterSave.createGlFilter(FilterSave.createFilterList()[mPosition], context))
                    .listener(object : Listener {
                        override fun onProgress(progress: Double) {
                            //loading_dialog.dismiss()
                            loading_dialog.show()
                            Log.d(mAppName, "onProgress Filter = " + progress*100)
                        }

                        override fun onCompleted() {
                            Log.d(mAppName, "onCompleted() Filter : $filterFilepath")
                            loading_dialog?.dismiss()
                            activity?.finish()
                            CreateVideoScreen()
                        }

                        override fun onCanceled() {
                            loading_dialog?.dismiss()
                            Log.d(mAppName, "onCanceled")
                        }

                        override fun onFailed(exception: Exception) {
                            loading_dialog?.dismiss()
                            Log.e(mAppName, "onFailed() Filter", exception)
                        }
                    })
                    .start()
        } else {
            loading_dialog?.dismiss()
            Toast.makeText(context, "Not use filter", Toast.LENGTH_SHORT).show()
        }
    }
    fun deleteDir(file: File): Boolean {
        if (file.isDirectory) {
            val list = file.list()
            for (file2 in list) {
                if (!deleteDir(File(file, file2))) {
                    return false
                }
            }
        }
        return file.delete()
    }


    private fun setUpSimpleExoPlayer() {

        val dataSourceFactory = DefaultDataSourceFactory(
            requireContext(),
            Util.getUserAgent(requireContext(), mAppName)
        )
        val videoSource = Factory(dataSourceFactory)
            .createMediaSource(Uri.fromFile(File(filepath)))

        player = ExoPlayerFactory.newSimpleInstance(context).apply {
            prepare(videoSource)
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE
        }


    }

    private fun setUpGlPlayerView() {
        ePlayerView = EPlayerView(context).apply {setSimpleExoPlayer(player)
        }
        filterView.addView(ePlayerView)
        ePlayerView.onResume()
        ePlayerView.onResume()
    }

    override fun onResume() {
        super.onResume()
        setUpSimpleExoPlayer()
        setUpGlPlayerView()
    }

    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    private fun releasePlayer() {
        ePlayerView.onPause()
        player.stop()
        player.release()
    }

    override fun onClick(v: View, position: Int) {
        mPosition = position
        ePlayerView.setGlFilter(FilterType.createGlFilter(FilterType.createFilterList()[mPosition], context))
    }

    fun CreateVideoScreen() {

        val intent = Intent(context, VideoViewActivity::class.java)
            intent.putExtra("videopath", filterFilepath)
            Log.d("TAG12", "VideoEdit file path" + filterFilepath);
            intent.putExtra("frompage", "Mute")
            startActivity(intent)
    }


}

