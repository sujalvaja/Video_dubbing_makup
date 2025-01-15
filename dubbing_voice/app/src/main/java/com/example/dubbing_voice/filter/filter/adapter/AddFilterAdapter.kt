package com.example.dubbing_voice.filter.filter.adapter

import android.media.ThumbnailUtils
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dubbing_voice.R
import com.example.dubbing_voice.filter.filter.interfaces.AddFilterListener
import com.example.dubbing_voice.filter.filter.utils.FilterType
import com.example.dubbing_voice.filter.utils.MotionTouchListener
import jp.co.cyberagent.android.gpuimage.GPUImage
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter
import jp.co.cyberagent.android.gpuimage.GPUImageToneCurveFilter

class AddFilterAdapter(private val mListener: AddFilterListener,
                       private val mFilename: String): RecyclerView.Adapter<AddFilterAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_filter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, mListener, mFilename)
    }

    override fun getItemCount(): Int {
        return FilterType.createFilterList().size
    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        lateinit var thumbnails: ImageView
        lateinit var textFilterName: TextView

        fun bind(position: Int, listener: AddFilterListener, filename: String) {
             thumbnails= itemView.findViewById<ImageView>(R.id.thumbnails)
            textFilterName= itemView.findViewById<TextView>(R.id.textFilterName)

            itemView.apply {
                setOnTouchListener(MotionTouchListener())
                setOnClickListener { v -> listener.onClick(v, position) }
            }

            var bitmap = ThumbnailUtils.createVideoThumbnail(filename, MediaStore.Video.Thumbnails.MICRO_KIND)

            val filterFile = arrayListOf<String>()
            filterFile.addAll(
                    listOf(
                            "acv/afterglow.acv",
                            "acv/alice_in_wonderland.acv",
                            "acv/ambers.acv",
                            "acv/aurora.acv",
                            "acv/blue_poppies.acv",
                            "acv/blue_yellow_field.acv",
                            "acv/carousel.acv",
                            "acv/cold_desert.acv",
                            "acv/cold_heart.acv",
                            "acv/digital_film.acv",
                            "acv/documentary.acv",
                            "acv/electric.acv",
                            "acv/ghosts_in_your_head.acv",
                            "acv/good_luck_charm.acv",
                            "acv/green_envy.acv",
                            "acv/hummingbirds.acv",
                            "acv/kiss_kiss.acv",
                            "acv/left_hand_blues.acv",
                            "acv/light_parades.acv",
                            "acv/lullabye.acv",
                            "acv/moth_wings.acv",
                            "acv/moth_wings.acv",
                            "acv/old_postcards_01.acv",
                            "acv/old_postcards_02.acv",
                            "acv/peacock_feathers.acv",
                            "acv/pistol.acv",
                            "acv/ragdoll.acv",
                            "acv/rose_thorns_01.acv",
                            "acv/rose_thorns_02.acv",
                            "acv/set_you_free.acv",
                            "acv/snow_white.acv",
                            "acv/toes_in_the_ocean.acv",
                            "acv/wild_at_heart.acv",
                            "acv/window_warmth.acv")
            )

            val gpuImage = GPUImage(itemView.context)
            gpuImage.setImage(bitmap)

            val gpuFilter = GPUImageToneCurveFilter()

            when (position) {
                1 -> gpuImage.setFilter(GPUImageGrayscaleFilter())
                else -> {
                    if (position > 1) {
                        val inputFilter = itemView.context.assets.open(filterFile[position-2])
                        gpuFilter.setFromCurveFileInputStream(inputFilter)
                        inputFilter.close()

                        gpuImage.setFilter(gpuFilter)
                    }
                }
            }

            try {
                bitmap = gpuImage.bitmapWithFilterApplied
                itemView.apply {
                    thumbnails.setImageBitmap(bitmap)
                    textFilterName.text = when (position) {
                        0 -> "normal"
                        1 -> "grayscale"
                        else -> filterFile[position-2].drop(4).dropLast(4)
                    }
                }
            } catch (e: NullPointerException) {

            }

        }
    }
}
