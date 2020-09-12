package pl.renesans.renesans.data.realm

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.realm.Realm
import io.realm.RealmResults
import pl.renesans.renesans.R
import pl.renesans.renesans.article.ArticleActivity
import pl.renesans.renesans.data.*
import pl.renesans.renesans.data.article.ArticleDaoImpl
import pl.renesans.renesans.data.converter.ArticleConverterImpl
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl
import pl.renesans.renesans.utility.connection.ConnectionUtilityImpl

class RealmDaoImpl(private val context: Context,
    private val realmInterractor: RealmContract.RealmInterractor? = null): RealmContract.RealmDao,
    ImageDaoContract.ImageDaoDownloadInterractor {

    private val firestore = FirebaseFirestore.getInstance()
    private val articlesRef = firestore.collection("articles")
    private val realmMapper = RealmMapperImpl(context)
    private val imageDao = ImageDaoImpl(context, downloadInterractor = this)
    private val connectionUtility = ConnectionUtilityImpl(context)
    private var realm: Realm
    private val articleConverter = ArticleConverterImpl()
    private val sharedPrefs: SharedPreferences
    private val prefsEditor: SharedPreferences.Editor
    private var canDownloadPhotos = false
    private var allArticlesLists = 0
    private var downloadedArticlesLists = 0
    private var allArticles = 0
    private var downloadedArticlesPhotos = 0

    init {
        Realm.init(context)
        canDownloadPhotos = permissionIsGranted()
        realm = Realm.getInstance(RealmUtility.getDefaultConfig())
        sharedPrefs = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
        prefsEditor = sharedPrefs.edit()
    }

    private fun permissionIsGranted() = (ContextCompat.checkSelfPermission(context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

    override fun refreshRealmDatabase(firstDownload: Boolean){
        if(!connectionUtility.isConnectionAvailable()) realmInterractor?.downloadFailure(true)
        else if(firstDownload) downloadArticlesListsWithArticles(firstDownload)
        else checkDbVersion(true)
    }

    private fun downloadArticlesListsWithArticles(firstDownload: Boolean) {
        realmInterractor?.startedLoading()
        downloadedArticlesLists = 0
        if(firstDownload) checkDbVersion(false)
        articlesRef.get().addOnSuccessListener { task ->
            realmInterractor?.downloadedProgress(10)
            for(document in task.documents){
                val articlesList = document.toObject(ArticlesList::class.java)
                checkArticlesList(articlesList)
                allArticlesLists = task.documents.size
                downloadArticlesFromList(document, articlesList, firstDownload)
            }
        }.addOnFailureListener{ realmInterractor?.downloadFailure() }
    }

    private fun checkDbVersion(refreshDatabase: Boolean) {
        firestore.collection("version").document("current-db")
            .get().addOnSuccessListener {
                val dbVersion = it.toObject(DatabaseVersion::class.java)
                val currentDbVersion = getRealmDatabaseVersion()
                if(dbVersion?.version != currentDbVersion?.version) {
                    updateDatabaseVersion(dbVersion)
                    if(refreshDatabase) downloadArticlesListsWithArticles(false)
                }else realmInterractor?.databaseIsUpToDate()
            }.addOnFailureListener{ if(refreshDatabase) realmInterractor?.downloadFailure() }
    }

    override fun getDatabaseVersion(): String {
        val version = getRealmDatabaseVersion()
        return if(version?.version != null) version.version.toString()
        else context.getString(R.string.no_information)
    }

    private fun updateDatabaseVersion(databaseVersion: DatabaseVersion?) {
        realm.executeTransaction{ getRealmDatabaseVersion()?.deleteFromRealm() }
        insertDatabaseVersion(databaseVersion)
    }

    private fun insertDatabaseVersion(databaseVersion: DatabaseVersion?) {
        realm.beginTransaction()
        val realmDatabaseVersion = realm.createObject(DatabaseVersionRealm::class.java)
        realmDatabaseVersion.version = databaseVersion?.version
        realm.commitTransaction()
    }

    private fun getRealmDatabaseVersion(): DatabaseVersionRealm? =
        realm.where<DatabaseVersionRealm>(DatabaseVersionRealm::class.java)
            .findFirst()

    private fun checkArticlesList(articlesList: ArticlesList?) {
        if(!articleListExists(articlesList)) insertArticlesList(articlesList)
        else if(!articlesListIsEqualWithDb(articlesList)) updateArticlesList(articlesList)
    }

    private fun updateArticlesList(articlesList: ArticlesList?) {
        realm.executeTransaction{ getRealmArticlesListWithId(articlesList?.id)?.deleteFromRealm() }
        insertArticlesList(articlesList)
    }

    private fun getRealmArticlesListWithId(id: String?): ArticlesListRealm? =
        realm.where<ArticlesListRealm>(ArticlesListRealm::class.java)
            .contains(ID, id)
            .findFirst()

    private fun downloadArticlesFromList(document: DocumentSnapshot,
                                         articlesList: ArticlesList?, firstDownload: Boolean) {
        articlesRef.document(document.id).collection(document.id).get()
            .addOnSuccessListener { articleTask ->
                downloadedArticlesLists++
                realmInterractor?.downloadedProgress(getPercentageOfDownload())
                checkArticlesDocuments(articleTask, articlesList)
                checkAllArticlesHasBeenDownloaded(firstDownload)
            }
    }

    private fun checkArticlesDocuments(articleTask: QuerySnapshot, articlesList: ArticlesList?) {
        for(articleDoc in articleTask.documents){
            if(articlesList?.type == ARTICLE) checkArticle(articleDoc)
            else checkPhotoArticle(articleDoc)
        }
    }

    private fun checkArticle(articleDoc: DocumentSnapshot) {
        val article = articleDoc.toObject(Article::class.java)
        if(!articleExists(article)) insertArticle(article)
        else if(!articleIsEqualWithDb(article)) updateArticle(article)
    }

    private fun updateArticle(article: Article?) {
        realm.executeTransaction{ getRealmArticleWithId(article?.objectId)?.deleteFromRealm() }
        insertArticle(article)
    }

    private fun getRealmArticleWithId(id: String?): ArticleRealm? =
        realm.where<ArticleRealm>(ArticleRealm::class.java)
            .contains(OBJECT_ID, id)
            .findFirst()

    private fun checkPhotoArticle(articleDoc: DocumentSnapshot) {
        val photoArticle = articleDoc.toObject(PhotoArticle::class.java)
        if(!photoArticleExists(photoArticle)) insertPhotoArticle(photoArticle)
        if(!photoArticleIsEqualWithDb(photoArticle)) updatePhotoArticle(photoArticle)
    }

    private fun updatePhotoArticle(photoArticle: PhotoArticle?) {
        realm.executeTransaction{ getRealmPhotoArticleWithId(photoArticle?.objectId)?.deleteFromRealm() }
        insertPhotoArticle(photoArticle)
    }

    private fun getRealmPhotoArticleWithId(id: String?): PhotoArticleRealm? =
        realm.where<PhotoArticleRealm>(PhotoArticleRealm::class.java)
            .contains(OBJECT_ID, id)
            .findFirst()

    private fun checkAllArticlesHasBeenDownloaded(firstDownload: Boolean) {
        if(downloadedArticlesLists == allArticlesLists) {
            prefsEditor.putBoolean(ALL_DOWNLOADED, true)
            prefsEditor.commit()
            if(canDownloadPhotos && firstDownload) downloadAllPhotos()
            else realmInterractor?.downloadSuccessful()
        }
    }

    private fun downloadAllPhotos() {
        for(article in getAllArticles())
            if (article.objectId != null) downloadPhotoWithId(article.objectId!!)
        val additionalPhotos = listOf("Z0", "Z1", "Z2", "Z3", "Z4", "Z5", "Z6", "Z7")
        for(id in additionalPhotos) downloadPhotoWithId(id)
    }

    private fun downloadPhotoWithId(id: String) {
        allArticles++
        val photoId = id + "_0"
        imageDao.loadPhoto(id = photoId, bothQualities = false)
    }

    private fun articleListExists(articlesList: ArticlesList?): Boolean
            = realm.where(ArticlesListRealm::class.java)
            .equalTo(ID, articlesList?.id).findFirst() != null

    private fun articlesListIsEqualWithDb(articlesList: ArticlesList?): Boolean
            = articlesList == getArticlesListWithId(articlesList?.id!!)

    private fun articleExists(article: Article?): Boolean
            = realm.where(ArticleRealm::class.java)
            .equalTo(OBJECT_ID, article?.objectId).findFirst() != null

    private fun articleIsEqualWithDb(article: Article?): Boolean
        = article == getArticleWithId(article?.objectId!!)

    private fun photoArticleExists(photoArticle: PhotoArticle?): Boolean
            = realm.where(PhotoArticleRealm::class.java)
        .equalTo(OBJECT_ID, photoArticle?.objectId).findFirst() != null

    private fun photoArticleIsEqualWithDb(photoArticle: PhotoArticle?): Boolean
            = photoArticle == getPhotoArticleWithId(photoArticle?.objectId!!)

    private fun insertArticlesList(articlesList: ArticlesList?) {
        realm.beginTransaction()
        val realmArticlesList =
            realm.createObject(ArticlesListRealm::class.java)
        realmMapper.setPropertiesOfArticlesListRealm(articlesList, realmArticlesList)
        realm.commitTransaction()
    }

    private fun insertArticle(article: Article?) {
        realm.beginTransaction()
        val realmArticle =
            realm.createObject(ArticleRealm::class.java)
        realmMapper.setPropertiesOfArticleRealm(article, realmArticle)
        realm.commitTransaction()
    }

    private fun insertPhotoArticle(photoArticle: PhotoArticle?) {
        realm.beginTransaction()
        val realmPhotoArticle =
            realm.createObject(PhotoArticleRealm::class.java)
        realmMapper.setPropertiesOfPhotoArticleRealm(photoArticle, realmPhotoArticle)
        realm.commitTransaction()
    }

    override fun realmDatabaseIsEmpty(): Boolean =
        (realm.where(ArticleRealm::class.java).findFirst() == null) ||
                !sharedPrefs.getBoolean(ALL_DOWNLOADED, false)

    override fun getCityWithCityKey(cityKey: String) =
        realm.where(PhotoArticleRealm::class.java)
            .equalTo(CITY_KEY, cityKey)
            .equalTo(OBJECT_TYPE, ArticleDaoImpl.CITY_TYPE)
            .findFirst()?.title

    override fun getAllArticles(): List<Article> {
        realm = Realm.getInstance(RealmUtility.getDefaultConfig())
        val allArticlesFromList = mutableListOf<Article>()
        addAllArticlesToList(allArticlesFromList)
        addAllPhotoArticlesToList(allArticlesFromList)
        return allArticlesFromList.toList()
    }

    private fun addAllArticlesToList(allArticlesFromList: MutableList<Article>) {
        val allArticles: RealmResults<ArticleRealm> =
            realm.where<ArticleRealm>(ArticleRealm::class.java)
                .findAll()
                .sort(OBJECT_ID)
        allArticles.forEach{ allArticlesFromList.add(realmMapper.getArticleFromRealm(it)!!) }
    }

    private fun addAllPhotoArticlesToList(allArticlesFromList: MutableList<Article>) {
        val allPhotoArticles = getPhotoArticles(false)
        allPhotoArticles.forEach{
            allArticlesFromList.add(articleConverter.convertPhotoArticleToArticle(it))
        }
    }

    override fun getArticlesLists(): List<ArticlesList> {
        val allArticlesLists: RealmResults<ArticlesListRealm> = realm
            .where<ArticlesListRealm>(ArticlesListRealm::class.java).findAll().sort("index")
        val articlesLists = mutableListOf<ArticlesList>()
        allArticlesLists.forEach{ articlesLists.add(realmMapper.getArticlesListFromRealm(it)) }
        return articlesLists.toList()
    }

    override fun getArticlesFromListWithId(id: String): List<Article> {
        val allArticles = getRealmArticlesFromListWithId(id)
        val allArticlesFromList = mutableListOf<Article>()
        allArticles.forEach{ allArticlesFromList.add(realmMapper.getArticleFromRealm(it)!!) }
        return allArticlesFromList.toList()
    }

    override fun getArticlesItemsFromListWithId(id: String): List<ArticleItem> {
        val allArticles = getRealmArticlesFromListWithId(id)
        val allArticlesFromList = mutableListOf<ArticleItem>()
        allArticles.forEach{ allArticlesFromList.add(realmMapper.getArticleItemFromRealm(it)) }
        return allArticlesFromList.toList()
    }

    override fun getArticlesItemsFromLocalList(localListId: String,
                                               onlyPhotoArticles: Boolean): List<ArticleItem> {
        val articlesItemsList = mutableListOf<ArticleItem>()
        getLocalArticlesList(localListId)?.listOfLocalArticles?.forEach {
            val articleItem = realmMapper.getArticleItem(getArticleWithId(it.id!!))
            if(localListId == SEARCH_HISTORY) articleItem.searchHistoryItem = true
            if(!onlyPhotoArticles || it.type == ArticleActivity.PHOTO_ARTICLE)
                articlesItemsList.add(articleItem)
        }
        return articlesItemsList
    }

    override fun addItemToLocalArticlesList(localListId: String, articleId: String) {
        val searchHistoryRealm = getLocalArticlesList(localListId)
        if(searchHistoryRealm == null) insertLocalArticlesListToRealm(localListId, articleId)
        else addArticleToLocalArticlesList(localListId, articleId)
    }

    override fun deleteItemFromLocalArticlesList(localListId: String, articleId: String) {
        realm = Realm.getInstance(RealmUtility.getDefaultConfig())
        realm.beginTransaction()
        val localArticlesList = getLocalArticlesList(localListId)
        val indexOfItem =  localArticlesList?.listOfLocalArticles!!.indexOfFirst { it.id == articleId }
        localArticlesList.listOfLocalArticles?.removeAt(indexOfItem)
        realm.commitTransaction()
    }

    private fun insertLocalArticlesListToRealm(localListId: String, articleId: String){
        realm.beginTransaction()
        val localArticlesList =
            realm.createObject(LocalArticlesListRealm::class.java)
        localArticlesList.id = localListId
        localArticlesList.listOfLocalArticles?.add(0, LocalArticleRealm(articleId, getTypeOfArticle(articleId)))
        realm.commitTransaction()
    }

    private fun addArticleToLocalArticlesList(localListId: String, articleId: String){
        realm.beginTransaction()
        val localArticlesList = getLocalArticlesList(localListId)
        if(articleIsInLocalList(localListId, articleId)) localArticlesList?.listOfLocalArticles!!
            .move(localArticlesList.listOfLocalArticles!!.indexOfFirst { it.id == articleId }, 0)
        else localArticlesList?.listOfLocalArticles?.add(0,
            LocalArticleRealm(articleId, getTypeOfArticle(articleId)))
        if(localListId == SEARCH_HISTORY) checkSizeOfSavedHistory(localArticlesList!!)
        realm.commitTransaction()
    }

    override fun articleIsInLocalList(localListId: String, articleId: String): Boolean {
        val localArticlesList = getLocalArticlesList(localListId)
        return if(localArticlesList == null) false
        else localArticlesList.listOfLocalArticles!!.any{ it.id == articleId }
    }

    private fun checkSizeOfSavedHistory(searchHistoryLocalList: LocalArticlesListRealm){
        if(searchHistoryLocalList.listOfLocalArticles?.size!! > 10)
            searchHistoryLocalList.listOfLocalArticles!!.removeAt(
                searchHistoryLocalList.listOfLocalArticles?.size!! - 1)
    }

    private fun getLocalArticlesList(id: String): LocalArticlesListRealm? {
        realm = Realm.getInstance(RealmUtility.getDefaultConfig())
        return realm.where(LocalArticlesListRealm::class.java)
            .contains(ID, id).findFirst()
    }

    private fun getRealmArticlesFromListWithId(id: String?): RealmResults<ArticleRealm> =
        realm.where<ArticleRealm>(ArticleRealm::class.java)
            .contains(OBJECT_ID, id)
            .findAll()
            .sort(INDEX)

    override fun getPhotoArticles(withCities: Boolean): List<PhotoArticle> {
        val photoArticlesList = getPhotosArticlesListRealm()
        val allPhotoArticles = getPhotosArticlesFromList(photoArticlesList?.id)
        val allPhotoArticlesFromList = mutableListOf<PhotoArticle>()
        for(photoArticle in allPhotoArticles)
            if(photoArticle.objectType == ArticleDaoImpl.PLACE_TYPE || withCities)
                allPhotoArticlesFromList.add(realmMapper.getPhotoArticleFromRealm(photoArticle))
        return allPhotoArticlesFromList.toList()
    }

    private fun getPhotosArticlesListRealm(): ArticlesListRealm? =
        realm.where<ArticlesListRealm>(ArticlesListRealm::class.java)
            .equalTo(TYPE, PHOTO_ARTICLE)
            .findFirst()

    private fun getPhotosArticlesFromList(id: String?): RealmResults<PhotoArticleRealm> =
        realm.where<PhotoArticleRealm>(PhotoArticleRealm::class.java)
            .contains(OBJECT_ID, id)
            .findAll()
            .sort(OBJECT_TYPE)

    override fun getArticlesListWithId(id: String) =
        realmMapper.getArticlesListFromRealm(realm
            .where<ArticlesListRealm>(ArticlesListRealm::class.java)
            .contains(ID, id)
            .findFirst())

    override fun getArticleWithId(id: String): Article {
        realm = Realm.getInstance(RealmUtility.getDefaultConfig())
        val articleRealm = realm
            .where<ArticleRealm>(ArticleRealm::class.java)
            .contains(OBJECT_ID, id)
            .findFirst()
        return if(articleRealm != null) realmMapper.getArticleFromRealm(articleRealm)!!
        else articleConverter.convertPhotoArticleToArticle(getPhotoArticleWithId(id))
    }

    override fun getPhotoArticleWithId(id: String): PhotoArticle {
        realm = Realm.getInstance(RealmUtility.getDefaultConfig())
        val articlePhoto = realmMapper.getPhotoArticleFromRealm(realm
            .where(PhotoArticleRealm::class.java)
            .contains(OBJECT_ID, id)
            .findFirst())
        if(articlePhoto.cityKey != null){
            val city = getCityWithCityKey(articlePhoto.cityKey!!)
            if(city!=null) articlePhoto.header = Header(
                content = hashMapOf(Pair(context.resources.getString(R.string.city), city)))
        }
        return articlePhoto
    }

    override fun getTypeOfArticle(id: String): String {
        val articlePhoto = realm
            .where(PhotoArticleRealm::class.java)
            .contains(OBJECT_ID, id)
            .findFirst()
        return if(articlePhoto != null) ArticleActivity.PHOTO_ARTICLE
        else ArticleActivity.ARTICLE
    }

    override fun downloadFailed() = refreshProgress()

    override fun downloadSuccess() = refreshProgress()

    override fun photoExists() = refreshProgress()

    private fun refreshProgress(){
        downloadedArticlesPhotos++
        realmInterractor?.downloadedProgress(getPercentageOfDownload())
        checkAllDownloaded()
    }

    private fun getPercentageOfDownload() =
        if(canDownloadPhotos) ((downloadedArticlesLists.toDouble()/allArticlesLists.toDouble())*50).toInt() +
                ((downloadedArticlesPhotos.toDouble()/allArticles.toDouble())*50).toInt()
        else ((downloadedArticlesLists.toDouble()/allArticlesLists.toDouble())*100).toInt()

    private fun checkAllDownloaded() {
        if(allArticles == downloadedArticlesPhotos)
            realmInterractor?.downloadSuccessful()
    }

    companion object{
        const val ARTICLE = "ARTICLE"
        const val PHOTO_ARTICLE = "PHOTO_ARTICLE"
        const val ALL_DOWNLOADED = "ALL_DOWNLOADED"
        const val SEARCH_HISTORY = "SEARCH_HISTORY"
        const val MARKED_ARTICLES = "MARKED_ARTICLES"

        const val OBJECT_ID = "objectId"
        const val OBJECT_TYPE = "objectType"
        const val ID = "id"
        const val TYPE = "type"
        const val INDEX = "index"
        const val CITY_KEY = "cityKey"
    }
}