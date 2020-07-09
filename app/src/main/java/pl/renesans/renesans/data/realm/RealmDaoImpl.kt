package pl.renesans.renesans.data.realm

import android.content.Context
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import io.realm.Realm
import io.realm.RealmResults
import pl.renesans.renesans.data.Article
import pl.renesans.renesans.data.ArticlesList
import pl.renesans.renesans.data.ArticlesListRealm

class RealmDaoImpl(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val articlesRef = firestore.collection("articles")
    private lateinit var realm: Realm

    fun refreshRealmDatabase(){
        Realm.init(context)
        realm = Realm.getDefaultInstance()
        articlesRef.get().addOnSuccessListener { task ->
            for(document in task.documents){
                val articlesList = document.toObject(ArticlesList::class.java)
                if(realm.where(ArticlesListRealm::class.java)
                        .equalTo("id", articlesList?.id).findFirst() == null)
                    insertArticlesList(articlesList)

                articlesRef.document(document.id).collection(document.id).get()
                    .addOnSuccessListener { articleTask ->
                    for(articleDoc in articleTask.documents){
                        val article = document.toObject(Article::class.java)
                    }
                }
            }
        }
    }

    private fun insertArticlesList(articlesList: ArticlesList?){
        realm.beginTransaction()
        val realmArticlesList =
            realm.createObject(ArticlesListRealm::class.java, generateId())
        realmArticlesList.id = articlesList?.id
        realmArticlesList.name = articlesList?.name
        realmArticlesList.type = articlesList?.type
        realm.commitTransaction()
    }

    fun checkRealm(){
        Realm.init(context)
        realm = Realm.getDefaultInstance()
        val all: RealmResults<ArticlesListRealm> = realm
            .where<ArticlesListRealm>(ArticlesListRealm::class.java).findAll().sort("id")
        for(list in all) Log.d("MOJTAG", list.toString())
    }

    private fun generateId(): Int {
        return if (realm.where<ArticlesListRealm>(ArticlesListRealm::class.java)
                .max("id") == null) 0
        else realm.where<ArticlesListRealm>(ArticlesListRealm::class.java)
            .max("id")!!.toInt() + 1
    }
}