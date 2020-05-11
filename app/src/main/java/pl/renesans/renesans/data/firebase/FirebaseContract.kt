package pl.renesans.renesans.data.firebase

import pl.renesans.renesans.data.Suggestion

interface FirebaseContract {

    interface FirebaseDao {

        fun putSuggestionToFirebase(suggestion: Suggestion)
    }

    interface FirebaseInterractor {

        fun onSuccess()

        fun onFail()
    }
}