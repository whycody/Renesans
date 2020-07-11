package pl.renesans.renesans.data.realm

interface RealmContract {

    interface RealmDao {

        fun onCreate()

        fun refreshRealmDatabase()

        fun checkRealm()

        fun checkRealmLists()

        fun realmDatabaseIsEmpty(): Boolean
    }

    interface RealmInterractor {

        fun downloadSuccessful()

        fun downloadFailure(connectionProblem: Boolean = false)
    }

}

