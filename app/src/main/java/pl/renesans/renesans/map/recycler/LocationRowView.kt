package pl.renesans.renesans.map.recycler

import android.graphics.drawable.Drawable

interface LocationRowView {

    fun setDrawable(drawable: Drawable)

    fun setText(text: String)

    fun setOnRowClickListener(pos: Int)

    fun setTextColor(color: Int)
}