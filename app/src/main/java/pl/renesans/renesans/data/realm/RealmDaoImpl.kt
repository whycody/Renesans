package pl.renesans.renesans.data.realm

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.Realm
import io.realm.RealmResults
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.ArticleRealm
import pl.renesans.renesans.data.ArticlesList
import pl.renesans.renesans.data.ArticlesListRealm

class RealmDaoImpl(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val articlesRef = firestore.collection("articles")
    private val realmMapper = RealmMapperImpl(context)
    private lateinit var realm: Realm

    fun refreshRealmDatabase(){
        Realm.init(context)
        realm = Realm.getDefaultInstance()
        realmMapper.onCreate()
        articlesRef.get().addOnSuccessListener { task ->
            for(document in task.documents){
                val articlesList = document.toObject(ArticlesList::class.java)
                if(!articleListExists(articlesList)) insertArticlesList(articlesList)

                articlesRef.document(document.id).collection(document.id).get()
                    .addOnSuccessListener { articleTask ->
                    for(articleDoc in articleTask.documents){
                        val article = articleDoc.toObject(Article::class.java)
                        if(!articleExists(article)) insertArticle(article)
                    }
                }
            }
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

    fun checkRealm(){
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