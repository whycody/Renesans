package pl.renesans.renesans.data.firebase

import pl.renesans.renesans.data.Suggestion
import java.io.Serializable

interface FirebaseContract {

    interface FirebaseDao {

        fun putSuggestionToFirebase(suggestion: Suggestion)
    }

    interface FirebaseInterractor: Serializable {

        fun onSuccess()

        fun onFail()
    }
}