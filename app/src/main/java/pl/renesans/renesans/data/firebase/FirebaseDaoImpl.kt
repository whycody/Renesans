package pl.renesans.renesans.data.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import pl.renesans.renesans.data.*

class FirebaseDaoImpl(private val interractor: FirebaseContract.FirebaseInterractor? = null):
    FirebaseContract.FirebaseDao {

    private val firestore = FirebaseFirestore.getInstance()
    private val suggestRef = firestore.collection("suggestions")
    private val articlesRef = firestore.collection("articles")

    override fun putSuggestionToFirebase(suggestion: Suggestion) {
        suggestRef.document().set(suggestion).addOnCompleteListener{ task ->
            if(task.isSuccessful) interractor?.onSuccess()
            else interractor?.onFail()
        }.addOnFailureListener{ interractor?.onFail() }
    }

    override fun refreshArticles() {
        val article = Article()
//        updateArticle(article)
    }

    private fun updateArticle(article: Article) {
        val listId = getArticleListId(article.objectId!!)
        articlesRef.document(listId).collection(listId).document(article.objectId!!).set(article)
            .addOnSuccessListener { Log.d("MOJTAG", "Article updated successfully: ${article.objectId}")
                updateVersion()
            }.addOnFailureListener { Log.d("MOJTAG", "Couldn't update article: ${article.objectId}")}
    }

    private fun updateVersion(){
        val dbVersionRef = firestore.collection("version").document("current-db")
        dbVersionRef.get().addOnSuccessListener {
            val version = it.toObject(DatabaseVersion::class.java)
            val newVersion = DatabaseVersion(version?.version!! + 1)
            dbVersionRef.set(newVersion)
                .addOnSuccessListener { Log.d("MOJTAG", "Updated version to ${newVersion.version}") }
                .addOnFailureListener { Log.d("MOJTAG", "Couldn't update version to ${newVersion.version}")}
        }
    }

    private fun getArticleListId(objectId: String) = objectId[0].toString()

    companion object{
        const val MAIN_TEXT = "Treść główna"
        const val PROFESSIONS = "Profesje"
        const val LIVE_YEARS = "Lata życia"
        const val NATIONALITY = "Narodowość"
        const val CREATE_YEAR = "Data powstania"
        const val CREATEOR = "Twórca"
        const val EXPLORER = "Odkrywca"
        const val ART_PLACE = "Miejsce przebywania"
        const val TIME = "Okres"
        const val TIME_FRAME = "Ramy czasowe"
        const val TIME_FRAME_POLAND = "Ramy czasowe w Polsce"
        const val TIME_FRAME_EUROPE = "Ramy czasowe w Europie"
        const val TIME_FRAME_ITALY = "Ramy czasowe we Włoszech"
        const val PLACE = "Miejsce"
        const val DATE = "Data"
        const val CITY = "Miasto"
    }
}