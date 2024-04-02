package com.example.liveearthmapuet.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.liveearthmapuet.R
import com.example.liveearthmapuet.YoutubePlayerActivity
import com.example.liveearthmapuet.classes.Misc
import com.example.liveearthmapuet.databinding.ItemCardAllCamBinding


class AllCamsAdapter(val context: Context, val arr: ArrayList<AllCamModel>) :
    RecyclerView.Adapter<AllCamsAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemCardAllCamBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCardAllCamBinding.bind(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_card_all_cam, parent, false
                )
            )
        )
    }

    override fun getItemCount(): Int {
        return arr.size
    }

    @SuppressLint("UseCompatLoadingForDrawables", "LogNotTimber")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {

            val options: RequestOptions = RequestOptions()
                .centerCrop()
                .placeholder(context.resources.getDrawable(R.drawable.bg_on_boarding_one))
                .error(context.resources.getDrawable(R.drawable.bg_on_boarding_one))

            Glide.with(context)
                .load("http://img.youtube.com/vi/${arr[position].url_mjpeg}/hqdefault.jpg")
                .apply(options).into(binding.imgTitle)

            binding.title.text = arr[position].cam_name
            binding.tvCountry.text = arr[position].cam_country

            binding.ivCategory.setImageResource(arr[position].titleImage!!)

            binding.root.setOnClickListener {
                val intent =
                    Intent(context, YoutubePlayerActivity::class.java)
                intent.putExtra("video_id", arr[position].url_mjpeg)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                Log.e(Misc.logKey, "onItemClick: ${arr[position].url_mjpeg}")
                context.startActivity(intent)
            }
        }
    }

}