package pl.renesans.renesans.sources

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import pl.renesans.renesans.data.*

class SourcesPresenterImpl(val context: Context, val view: SourcesContract.SourcesView):
    SourcesContract.SourcesPresenter, ImageDaoContract.ImageDaoInterractor {

    private lateinit var article: Article
    private var sourcesList = mutableListOf<Source>()
    private val holders: MutableList<SourcesRowHolder> = mutableListOf()
    private lateinit var imageDao: ImageDaoContract.ImageDao

    override fun onCreate() {
        article = view.getArticleObject()
        imageDao = ImageDaoImpl(context, this)
        getSourcesList()
    }

    private fun getSourcesList(){
        if(article.source!=null) sourcesList.add(article.source!!)
        article.listOfPhotos?.forEach { photo -> addPhotoSourceToList(photo) }
    }

    private fun addPhotoSourceToList(photo: Photo){
        if(photo.source!=null){
            photo.source!!.srcDescription = photo.description
            photo.source!!.photoId = photo.objectId
            sourcesList.add(photo.source!!)
        }
    }

    override fun itemClicked(pos: Int) {
        val url = sourcesList[pos].url
        if(url!=null) view.startUrlActivity(url)
    }

    override fun getItemCount(): Int {
        return sourcesList.size
    }

    override fun onBindViewHolder(holder: SourcesRowHolder, position: Int) {
        resetVariables(holder)
        refreshHoldersList(holder, position)
        holder.setDescription(sourcesList[position].page!!)
        holder.setTitle(sourcesList[position].srcDescription!!)
        holder.setOnClickListener(position)
        imageDao.loadPhotoInBothQualities(position, sourcesList[position].photoId!!)
    }

    private fun refreshHoldersList(holder: SourcesRowHolder, position: Int){
        if(holders.size-1<position || holders.isEmpty()) holders.add(position, holder)
        else holders[position] = holder
    }

    private fun resetVariables(holder: SourcesRowHolder){
        holder.setTitle(" ")
        holder.setDescription(" ")
        holder.setOnClickListener(0)
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        holders[pos].setSourceUriPhoto(photoUri)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        holders[pos].setSourceBitmapPhoto(photoBitmap)
    }
}