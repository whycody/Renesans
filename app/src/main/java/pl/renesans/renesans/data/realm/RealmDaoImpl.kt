package pl.renesans.renesans.data.realm

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults
import pl.renesans.renesans.R
import pl.renesans.renesans.data.*
import pl.renesans.renesans.data.article.ArticleDaoImpl
import pl.renesans.renesans.data.converter.ArticleConverterImpl
import pl.renesans.renesans.data.image.ImageDaoContract
import pl.renesans.renesans.data.image.ImageDaoImpl
import java.lang.Exception

class RealmDaoImpl(private val context: Context,
    private val realmInterractor: RealmContract.RealmInterractor? = null): RealmContract.RealmDao,
    ImageDaoContract.ImageDaoDownloadInterractor {

    private val firestore = FirebaseFirestore.getInstance()
    private val articlesRef = firestore.collection("articles")
    private val realmMapper = RealmMapperImpl(context)
    private val imageDao = ImageDaoImpl(context, downloadInterractor = this)
    private lateinit var realm: Realm
    private var allArticlesLists = 0
    private var downloadedArticlesLists = 0
    private var allArticles = 0
    private var downloadedArticlesPhotos = 0
    private val articleConverter = ArticleConverterImpl()
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var prefsEditor: SharedPreferences.Editor
    private var canDownloadPhotos = false

    override fun onCreate() {
        Realm.init(context)
        canDownloadPhotos = permissionIsGranted()
        realm = Realm.getInstance(RealmUtility.getDefaultConfig())
        realmMapper.onCreate()
        sharedPrefs = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
        prefsEditor = sharedPrefs.edit()
    }

    private fun permissionIsGranted() = (ContextCompat.checkSelfPermission(context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)

    override fun refreshRealmDatabase(firstDownload: Boolean){
        if(!isConnectionAvailable()) realmInterractor?.downloadFailure(true)
        else if(firstDownload) downloadArticlesListsWithArticles(firstDownload)
        else checkDbVersion(true)
    }

    private fun isConnectionAvailable(): Boolean {
        return try {
            val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = cm.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        } catch (_: Exception) { false }
    }

    private fun downloadArticlesListsWithArticles(firstDownload: Boolean){
        realmInterractor?.startedLoading()
        if(firstDownload) checkDbVersion(false)
        articlesRef.get().addOnSuccessListener { task ->
            realmInterractor?.downloadedProgress(10)
            for(document in task.documents){
                val articlesList = document.toObject(ArticlesList::class.java)
                checkArticlesList(articlesList)
                allArticlesLists = task.documents.size
                downloadArticlesFromList(document, articlesList)
            }
        }.addOnFailureListener{ realmInterractor?.downloadFailure() }
    }

    private fun checkDbVersion(refreshDatabase: Boolean){
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

    private fun updateDatabaseVersion(databaseVersion: DatabaseVersion?){
        realm.executeTransaction{ getRealmDatabaseVersion()?.deleteFromRealm() }
        insertDatabaseVersion(databaseVersion)
    }

    private fun insertDatabaseVersion(databaseVersion: DatabaseVersion?){
        realm.beginTransaction()
        val realmDatabaseVersion = realm.createObject(DatabaseVersionRealm::class.java)
        realmDatabaseVersion.version = databaseVersion?.version
        realm.commitTransaction()
    }

    private fun getRealmDatabaseVersion(): DatabaseVersionRealm? =
        realm.where<DatabaseVersionRealm>(DatabaseVersionRealm::class.java)
            .findFirst()

    private fun checkArticlesList(articlesList: ArticlesList?){
        if(!articleListExists(articlesList)) insertArticlesList(articlesList)
        else if(!articlesListIsEqualWithDb(articlesList)) updateArticlesList(articlesList)
    }

    private fun updateArticlesList(articlesList: ArticlesList?){
        realm.executeTransaction{ getRealmArticlesListWithId(articlesList?.id)?.deleteFromRealm() }
        insertArticlesList(articlesList)
    }

    private fun getRealmArticlesListWithId(id: String?): ArticlesListRealm? =
        realm.where<ArticlesListRealm>(ArticlesListRealm::class.java)
            .contains("id", id)
            .findFirst()

    private fun downloadArticlesFromList(document: DocumentSnapshot, articlesList: ArticlesList?) {
        articlesRef.document(document.id).collection(document.id).get()
            .addOnSuccessListener { articleTask ->
                downloadedArticlesLists++
                realmInterractor?.downloadedProgress(getPercentageOfDownload())
                checkArticlesDocuments(articleTask, articlesList)
                checkAllArticlesHasBeenDownloaded()
            }
    }

    private fun checkArticlesDocuments(articleTask: QuerySnapshot, articlesList: ArticlesList?) {
        for(articleDoc in articleTask.documents){
            if(articlesList?.type == ARTICLE) checkArticle(articleDoc)
            else checkPhotoArticle(articleDoc)
        }
    }

    private fun checkArticle(articleDoc: DocumentSnapshot){
        val article = articleDoc.toObject(Article::class.java)
        if(!articleExists(article)) insertArticle(article)
        else if(!articleIsEqualWithDb(article)) updateArticle(article)
    }

    private fun updateArticle(article: Article?){
        realm.executeTransaction{ getRealmArticleWithId(article?.objectId)?.deleteFromRealm() }
        insertArticle(article)
    }

    private fun getRealmArticleWithId(id: String?): ArticleRealm? =
        realm.where<ArticleRealm>(ArticleRealm::class.java)
            .contains("objectId", id)
            .findFirst()

    private fun checkPhotoArticle(articleDoc: DocumentSnapshot){
        val photoArticle = articleDoc.toObject(PhotoArticle::class.java)
        if(!photoArticleExists(photoArticle)) insertPhotoArticle(photoArticle)
        if(!photoArticleIsEqualWithDb(photoArticle)) updatePhotoArticle(photoArticle)
    }

    private fun updatePhotoArticle(photoArticle: PhotoArticle?){
        realm.executeTransaction{ getRealmPhotoArticleWithId(photoArticle?.objectId)?.deleteFromRealm() }
        insertPhotoArticle(photoArticle)
    }

    private fun getRealmPhotoArticleWithId(id: String?): PhotoArticleRealm? =
        realm.where<PhotoArticleRealm>(PhotoArticleRealm::class.java)
            .contains("objectId", id)
            .findFirst()

    private fun checkAllArticlesHasBeenDownloaded(){
        if(downloadedArticlesLists == allArticlesLists) {
            Log.d("MOJTAG", "Realm database is complete")
            prefsEditor.putBoolean(ALL_DOWNLOADED, true)
            prefsEditor.commit()
            if(canDownloadPhotos) downloadAllPhotos()
            else realmInterractor?.downloadSuccessful()
        }
    }

    private fun downloadAllPhotos(){
        for(article in getAllArticles())
            if(article.objectId != null) {
                allArticles ++
                val id = article.objectId!! + "_0"
                imageDao.loadPhoto(id = id, bothQualities = false)
            }
    }

    private fun articleListExists(articlesList: ArticlesList?): Boolean
            = realm.where(ArticlesListRealm::class.java)
            .equalTo("id", articlesList?.id).findFirst() != null

    private fun articlesListIsEqualWithDb(articlesList: ArticlesList?): Boolean
            = articlesList == getArticlesListWithId(articlesList?.id!!)

    private fun articleExists(article: Article?): Boolean
            = realm.where(ArticleRealm::class.java)
            .equalTo("objectId", article?.objectId).findFirst() != null

    private fun articleIsEqualWithDb(article: Article?): Boolean
        = article == getArticleWithId(article?.objectId!!)

    private fun photoArticleExists(photoArticle: PhotoArticle?): Boolean
            = realm.where(PhotoArticleRealm::class.java)
        .equalTo("objectId", photoArticle?.objectId).findFirst() != null

    private fun photoArticleIsEqualWithDb(photoArticle: PhotoArticle?): Boolean
            = photoArticle == getPhotoArticleWithId(photoArticle?.objectId!!)

    private fun insertArticlesList(articlesList: ArticlesList?){
        realm.beginTransaction()
        val realmArticlesList =
            realm.createObject(ArticlesListRealm::class.java)
        realmMapper.setPropertiesOfArticlesListRealm(articlesList, realmArticlesList)
        realm.commitTransaction()
    }

    private fun insertArticle(article: Article?){
        realm.beginTransaction()
        val realmArticle =
            realm.createObject(ArticleRealm::class.java)
        realmMapper.setPropertiesOfArticleRealm(article, realmArticle)
        realm.commitTransaction()
    }

    private fun insertPhotoArticle(photoArticle: PhotoArticle?){
        realm.beginTransaction()
        val realmPhotoArticle =
            realm.createObject(PhotoArticleRealm::class.java)
        realmMapper.setPropertiesOfPhotoArticleRealm(photoArticle, realmPhotoArticle)
        realm.commitTransaction()
    }

    override fun checkRealm(){
        val allArticlesLists: RealmResults<ArticlesListRealm> = realm
            .where<ArticlesListRealm>(ArticlesListRealm::class.java).findAll().sort("id")
        val allArticles: RealmResults<ArticleRealm> = realm
            .where<ArticleRealm>(ArticleRealm::class.java).findAll().sort("objectId")
        val allPhotoArticles: RealmResults<PhotoArticleRealm> = realm
            .where<PhotoArticleRealm>(PhotoArticleRealm::class.java).findAll().sort("objectId")
        for(articlesList in allArticlesLists)
            Log.d("MOJTAG", "ArticlesList: $articlesList")
        for(article in allArticles)
            Log.d("MOJTAG", "Article: $article")
        for(photoArticle in allPhotoArticles)
            Log.d("MOJTAG", "PhotoArticle: $photoArticle")
    }

    override fun checkRealmLists(){
        val allArticlesLists: RealmResults<ArticlesListRealm> = realm
            .where<ArticlesListRealm>(ArticlesListRealm::class.java).findAll().sort("index")
        allArticlesLists.forEach{
            Log.d("MOJTAG", "List: ${it.name}")
            val allArticles = getRealmArticlesFromListWithId(it.id)
            allArticles.forEach{ Log.d("MOJTAG", "Article: $it") }
            Log.d("MOJTAG", " ")
        }
    }

    override fun realmDatabaseIsEmpty(): Boolean =
        (realm.where(ArticleRealm::class.java).findFirst() == null) ||
                !sharedPrefs.getBoolean(ALL_DOWNLOADED, false)

    override fun getCityWithCityKey(cityKey: String) =
        realm.where(PhotoArticleRealm::class.java)
            .equalTo("cityKey", cityKey)
            .equalTo("objectType", ArticleDaoImpl.CITY_TYPE)
            .findFirst()?.title

    override fun getAllArticles(): List<Article> {
        realm = Realm.getInstance(RealmUtility.getDefaultConfig())
        val allArticlesFromList = mutableListOf<Article>()
        addAllArticlesToList(allArticlesFromList)
        addAllPhotoArticlesToList(allArticlesFromList)
        return allArticlesFromList.toList()
    }

    private fun addAllArticlesToList(allArticlesFromList: MutableList<Article>){
        val allArticles: RealmResults<ArticleRealm> =
            realm.where<ArticleRealm>(ArticleRealm::class.java)
                .findAll()
                .sort("objectId")
        allArticles.forEach{ allArticlesFromList.add(realmMapper.getArticleFromRealm(it)!!) }
    }

    private fun addAllPhotoArticlesToList(allArticlesFromList: MutableList<Article>){
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

    override fun getArticlesItemsFromSearchHistory(): List<ArticleItem> {
        val articlesItemsList = mutableListOf<ArticleItem>()
        getSearchHistoryRealmList()?.forEach {
            val articleItem = realmMapper.getArticleItem(getArticleWithId(it))
            articleItem.searchHistoryItem = true
            articlesItemsList.add(articleItem)
        }
        return articlesItemsList
    }

    private fun getSearchHistoryRealmList(): RealmList<String>? =
        getSearchHistoryRealm()?.listOfIdsOfLastSearchedItems

    override fun addItemToSearchHistoryRealm(id: String) {
        val searchHistoryRealm = getSearchHistoryRealm()
        if(searchHistoryRealm == null) insertSearchHistoryToRealm(id)
        else addIdToSearchHistoryRealm(id)
    }

    override fun deleteItemFromSearchHistoryRealm(id: String) {
        realm = Realm.getInstance(RealmUtility.getDefaultConfig())
        realm.beginTransaction()
        val searchHistoryRealm = getSearchHistoryRealm()
        val indexOfItem =  searchHistoryRealm?.listOfIdsOfLastSearchedItems!!.indexOf(id)
        searchHistoryRealm.listOfIdsOfLastSearchedItems?.removeAt(indexOfItem)
        realm.commitTransaction()
    }

    private fun insertSearchHistoryToRealm(id: String){
        realm.beginTransaction()
        val searchHistoryRealm =
            realm.createObject(SearchHistoryRealm::class.java)
        searchHistoryRealm.listOfIdsOfLastSearchedItems?.add(id)
        realm.commitTransaction()
    }

    private fun addIdToSearchHistoryRealm(id: String){
        realm.beginTransaction()
        val searchHistoryRealm = getSearchHistoryRealm()
        if(searchHistoryRealm?.listOfIdsOfLastSearchedItems!!.contains(id))
            searchHistoryRealm.listOfIdsOfLastSearchedItems!!.move(
                searchHistoryRealm.listOfIdsOfLastSearchedItems!!.indexOf(id), 0)
        else searchHistoryRealm.listOfIdsOfLastSearchedItems?.add(0, id)
        checkSizeOfSavedHistory(searchHistoryRealm)
        realm.commitTransaction()
    }

    private fun checkSizeOfSavedHistory(searchHistoryRealm: SearchHistoryRealm){
        if(searchHistoryRealm.listOfIdsOfLastSearchedItems?.size!! > 10)
            searchHistoryRealm.listOfIdsOfLastSearchedItems!!.removeAt(
                searchHistoryRealm.listOfIdsOfLastSearchedItems?.size!! - 1)
    }

    private fun getSearchHistoryRealm(): SearchHistoryRealm?{
        realm = Realm.getInstance(RealmUtility.getDefaultConfig())
        return realm.where(SearchHistoryRealm::class.java).findFirst()
    }

    private fun getRealmArticlesFromListWithId(id: String?): RealmResults<ArticleRealm> =
        realm.where<ArticleRealm>(ArticleRealm::class.java)
            .contains("objectId", id)
            .findAll()
            .sort("index")

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
            .equalTo("type", PHOTO_ARTICLE)
            .findFirst()

    private fun getPhotosArticlesFromList(id: String?): RealmResults<PhotoArticleRealm> =
        realm.where<PhotoArticleRealm>(PhotoArticleRealm::class.java)
            .contains("objectId", id)
            .findAll()
            .sort("objectType")

    override fun getArticlesListWithId(id: String): ArticlesList {
        return realmMapper.getArticlesListFromRealm(realm
            .where<ArticlesListRealm>(ArticlesListRealm::class.java)
            .contains("id", id)
            .findFirst())
    }

    override fun getArticleWithId(id: String): Article {
        realm = Realm.getInstance(RealmUtility.getDefaultConfig())
        val articleRealm = realm
            .where<ArticleRealm>(ArticleRealm::class.java)
            .contains("objectId", id)
            .findFirst()
        return if(articleRealm != null) realmMapper.getArticleFromRealm(articleRealm)!!
        else articleConverter.convertPhotoArticleToArticle(getPhotoArticleWithId(id))
    }

    private fun getPhotoArticleWithId(id: String): PhotoArticle {
        realm = Realm.getInstance(RealmUtility.getDefaultConfig())
        val articlePhoto = realmMapper.getPhotoArticleFromRealm(realm
            .where(PhotoArticleRealm::class.java)
            .contains("objectId", id)
            .findFirst())
        if(articlePhoto.cityKey != null){
            val city = getCity(articlePhoto.cityKey!!)
            if(city!=null) articlePhoto.header = Header(
                content = hashMapOf(Pair(context.resources.getString(R.string.city), city)))
        }
        return articlePhoto
    }

    private fun getCity(cityKey: String) = getCityWithCityKey(cityKey)

    override fun downloadFailed() = refreshProgress()

    override fun donwloadSuccess() = refreshProgress()

    override fun photoExists() = refreshProgress()


    private fun refreshProgress(){
        downloadedArticlesPhotos++
        realmInterractor?.downloadedProgress(getPercentageOfDownload())
        checkAllDownloaded()
    }

    private fun getPercentageOfDownload(): Int =
        if(canDownloadPhotos) ((downloadedArticlesLists.toDouble()/allArticlesLists.toDouble())*50).toInt() +
                ((downloadedArticlesPhotos.toDouble()/allArticles.toDouble())*50).toInt()
        else ((downloadedArticlesLists.toDouble()/allArticlesLists.toDouble())*100).toInt()

    private fun checkAllDownloaded() {
        if(allArticles == downloadedArticlesPhotos) realmInterractor?.downloadSuccessful()
    }

    companion object{
        const val ARTICLE = "ARTICLE"
        const val PHOTO_ARTICLE = "PHOTO_ARTICLE"
        const val ALL_DOWNLOADED = "ALL_DOWNLOADED"
    }
}