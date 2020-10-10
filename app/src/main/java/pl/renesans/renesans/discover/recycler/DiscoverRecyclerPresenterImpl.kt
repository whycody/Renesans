package pl.renesans.renesans.discover.recycler

import android.graphics.Bitmap
import android.net.Uri
import pl.renesans.renesans.data.*
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.realm.RealmContract

class DiscoverRecyclerPresenterImpl(private val articleId: String,
                                    private val realmDao: RealmContract.RealmDao,
                                    private val discoverView: DiscoverContract.DiscoverView):
    DiscoverContract.DiscoverRecyclerPresenter, ImageDaoContract.ImageDaoInterractor {

    private val articlesList: List<ArticleItem> = realmDao.getArticlesItemsFromListWithId(articleId)
    private val holders = hashMapOf<Int, DiscoverRowHolder>()

    init {
        discoverView.setDiscoverTitle(getArticlesListTitle())
    }

    private fun getArticlesListTitle() = realmDao.getArticlesListWithId(articleId).name!!

    override fun itemClicked(pos: Int) = discoverView.startArticleActivity(getArticleFromPos(pos)!!)

    private fun getArticleFromPos(pos: Int) = realmDao.getArticleWithId(articlesList[pos].objectId!!)

    override fun getItemCount() = articlesList.size

    override fun onBindViewHolder(holder: DiscoverRowHolder, position: Int) {
        resetVariables(holder)
        setupDiscoverRowHolder(holder, position)
        discoverView.loadPhoto(position, "${articlesList[position].objectId!!}_0")
    }

    private fun setupDiscoverRowHolder(holder: DiscoverRowHolder, position: Int) {
        with(holder) {
            holders[position] = this
            setArticleTitle(articlesList[position].title!!)
            setArticlePhotoSize(discoverView.getObjectType())
            setOnRowClickListener(position)
        }
    }

    private fun resetVariables(holder: DiscoverRowHolder) {
        with(holder) {
            setArticleTitle(" ")
            setOnRowClickListener(0)
            setArticleDrawablePhoto()
        }
    }

    override fun loadPhotoFromUri(photoUri: Uri, pos: Int) {
        holders[pos]?.setArticleUriPhoto(photoUri)
    }

    override fun loadPhotoFromBitmap(photoBitmap: Bitmap, pos: Int) {
        holders[pos]?.setArticleBitmapPhoto(photoBitmap)
    }
}