package pl.renesans.renesans.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import pl.renesans.renesans.data.Suggestion

class FirebaseDaoImpl(private val interractor: FirebaseContract.FirebaseInterractor? = null):
    FirebaseContract.FirebaseDao {

    private val firestore = FirebaseFirestore.getInstance()
    private val suggestRef = firestore.collection("suggestions")

    override fun putSuggestionToFirebase(suggestion: Suggestion) {
        suggestRef.document().set(suggestion).addOnCompleteListener{ task ->
            if(task.isSuccessful) interractor?.onSuccess()
            else interractor?.onFail()
        }.addOnFailureListener{ interractor?.onFail() }
    }

}