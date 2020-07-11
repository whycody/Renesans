package pl.renesans.renesans.data.realm

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import io.realm.Realm
import io.realm.RealmResults
import pl.renesans.renesans.data.*
import java.lang.Exception


class RealmDaoImpl(private val context: Context,
    private val realmInterractor: RealmContract.RealmInterractor? = null): RealmContract.RealmDao {

    private val firestore = FirebaseFirestore.getInstance()
    private val articlesRef = firestore.collection("articles")
    private val realmMapper = RealmMapperImpl(context)
    private lateinit var realm: Realm
    private var allArticlesLists = 0
    private var downloadedArticlesLists = 0

    override fun onCreate() {
        Realm.init(context)
        realm = Realm.getDefaultInstance()
        realmMapper.onCreate()
    }

    override fun refreshRealmDatabase(){
        if(isConnectionAvailable()) downloadArticlesListsWithArticles()
        else realmInterractor?.downloadFailure(true)
    }

    private fun isConnectionAvailable(): Boolean {
        return try {
            val cm = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = cm.activeNetworkInfo
            networkInfo != null && networkInfo.isConnected
        } catch (_: Exception) { false }
    }

    private fun downloadArticlesListsWithArticles(){
        articlesRef.get().addOnSuccessListener { task ->
            for(document in task.documents){
                val articlesList = document.toObject(ArticlesList::class.java)
                if(!articleListExists(articlesList)) insertArticlesList(articlesList)
                allArticlesLists = task.documents.size
                downloadArticlesFromList(document, articlesList)
            }
        }.addOnFailureListener{ realmInterractor?.downloadFailure() }
    }

    private fun downloadArticlesFromList(document: DocumentSnapshot, articlesList: ArticlesList?) {
        articlesRef.document(document.id).collection(document.id).get()
            .addOnSuccessListener { articleTask ->
                downloadedArticlesLists++
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
    }

    private fun checkPhotoArticle(articleDoc: DocumentSnapshot){
        val photoArticle = articleDoc.toObject(PhotoArticle::class.java)
        if(!photoArticleExists(photoArticle)) insertPhotoArticle(photoArticle)
    }

    private fun checkAllArticlesHasBeenDownloaded(){
        if(downloadedArticlesLists == allArticlesLists) {
            Log.d("MOJTAG", "Everything has been downloaded")
            realmInterractor?.downloadSuccessful()
            checkRealm()
            downloadedArticlesLists = 0
        }
    }

    private fun articleListExists(articlesList: ArticlesList?): Boolean
            = realm.where(ArticlesListRealm::class.java)
            .equalTo("id", articlesList?.id).findFirst() != null

    private fun articleExists(article: Article?): Boolean
            = realm.where(ArticleRealm::class.java)
            .equalTo("objectId", article?.objectId).findFirst() != null

    private fun photoArticleExists(photoArticle: PhotoArticle?): Boolean
            = realm.where(PhotoArticleRealm::class.java)
        .equalTo("objectId", photoArticle?.objectId).findFirst() != null

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

    override fun realmDatabaseIsEmpty(): Boolean {
        return realm.where(ArticleRealm::class.java).findFirst() == null
    }

    companion object{
        const val ARTICLE = "ARTICLE"
        const val PHOTO_ARTICLE = "PHOTO_ARTICLE"
    }
}