package pl.renesans.renesans.sources

import android.graphics.Bitmap
import android.net.Uri
import pl.renesans.renesans.data.Article

interface SourcesContract {

    interface SourcesView {

        fun getArticleObject(): Article

        fun startUrlActivity(url: String)
    }

    interface SourcesRowView {

        fun setSourceBitmapPhoto(bitmap: Bitmap)

        fun setSourceUriPhoto(uri: Uri)

        fun setTitle(title: String)

        fun setDescription(description: String)

        fun setOnClickListener(pos: Int)
    }

    interface SourcesPresenter {

        fun itemClicked(pos: Int)

        fun getItemCount(): Int

        fun onBindViewHolder(holder: SourcesRowHolder, position: Int)
    }
}