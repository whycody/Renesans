package pl.renesans.renesans.data.realm

import android.content.Context
import android.net.ConnectivityManager
import android.telecom.ConnectionService
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.Realm
import io.realm.RealmResults
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.ArticleRealm
import pl.renesans.renesans.data.ArticlesList
import pl.renesans.renesans.data.ArticlesListRealm
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
        if(isConnectionAvailable()) downloadArticlesToRealmDatabase()
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

    private fun downloadArticlesToRealmDatabase(){
        articlesRef.get().addOnSuccessListener { task ->
            for(document in task.documents){
                val articlesList = document.toObject(ArticlesList::class.java)
                if(!articleListExists(articlesList)) insertArticlesList(articlesList)
                allArticlesLists = task.documents.size
                articlesRef.document(document.id).collection(document.id).get()
                    .addOnSuccessListener { articleTask ->
                        downloadedArticlesLists++
                        for(articleDoc in articleTask.documents){
                            val article = articleDoc.toObject(Article::class.java)
                            if(!articleExists(article)) insertArticle(article)
                        }
                        checkAllArticlesHasBeenDownloaded()
                    }
            }
        }.addOnFailureListener{ realmInterractor?.downloadFailure() }
    }

    private fun checkAllArticlesHasBeenDownloaded(){
        if(downloadedArticlesLists == allArticlesLists) {
            Log.d("MOJTAG", "Everything has been downloaded")
            realmInterractor?.downloadSuccessful()
            downloadedArticlesLists = 0
        }
    }

    private fun articleListExists(articlesList: ArticlesList?): Boolean
            = realm.where(ArticlesListRealm::class.java)
            .equalTo("id", articlesList?.id).findFirst() != null

    private fun articleExists(article: Article?): Boolean
            = realm.where(ArticleRealm::class.java)
            .equalTo("objectId", article?.objectId).findFirst() != null

    private fun insertArticlesList(articlesList: ArticlesList?){
        realm.beginTransaction()
        val realmArticlesList =
            realm.createObject(ArticlesListRealm::class.java, generateIdForListRealm())
        realmMapper.setPropertiesOfArticlesListRealm(articlesList, realmArticlesList)
        realm.commitTransaction()
    }

    private fun insertArticle(article: Article?){
        realm.beginTransaction()
        val realmArticle =
            realm.createObject(ArticleRealm::class.java, generateIdForArticleRealm())
        realmMapper.setPropertiesOfArticleRealm(article, realmArticle)
        realm.commitTransaction()
    }

    override fun checkRealm(){
        Realm.init(context)
        realm = Realm.getDefaultInstance()
        val allArticlesLists: RealmResults<ArticlesListRealm> = realm
            .where<ArticlesListRealm>(ArticlesListRealm::class.java).findAll().sort("id")
        val allArticles: RealmResults<ArticleRealm> = realm
            .where<ArticleRealm>(ArticleRealm::class.java).findAll().sort("objectId")
        for(articlesList in allArticlesLists)
            Log.d("MOJTAG", articlesList.toString())
        for(article in allArticles)
            Log.d("MOJTAG", article.toString())
    }

    override fun realmDatabaseIsEmpty(): Boolean {
        return realm.where(ArticleRealm::class.java).findFirst() == null
    }

    private fun generateIdForListRealm(): Int {
        return if (realm.where<ArticlesListRealm>(ArticlesListRealm::class.java)
                .max("realmId") == null) 0
        else realm.where<ArticlesListRealm>(ArticlesListRealm::class.java)
            .max("realmId")!!.toInt() + 1
    }

    private fun generateIdForArticleRealm(): Int {
        return if (realm.where<ArticleRealm>(ArticleRealm::class.java)
                .max("realmId") == null) 0
        else realm.where<ArticleRealm>(ArticleRealm::class.java)
            .max("realmId")!!.toInt() + 1
    }
}