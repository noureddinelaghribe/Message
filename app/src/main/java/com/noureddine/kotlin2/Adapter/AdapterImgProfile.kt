package com.noureddine.kotlin2.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.noureddine.kotlin2.R

class AdapterImgProfile(val images: List<Int>, val onImageClickListener: OnImageClickListener): RecyclerView.Adapter<AdapterImgProfile.ImageViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterImgProfile.ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_img_user, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterImgProfile.ImageViewHolder, position: Int) {
        val img = images[position]
        holder.imgProfile.setImageResource(img)
        holder.imgProfile.setOnClickListener { onImageClickListener.onImageClick(img) }
    }

    override fun getItemCount(): Int {
        return images.size
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ShapeableImageView = itemView.findViewById(R.id.image_preview)
    }

    // Interface for click events
    interface OnImageClickListener {
        fun onImageClick(imageItem: Int)
    }

}