package pl.renesans.renesans.data.realm;

import io.realm.RealmConfiguration;

public class RealmUtility {

    private static final int SCHEMA_V_NOW = 6;

    public static RealmConfiguration getDefaultConfig() {
        return new RealmConfiguration.Builder()
                .schemaVersion(SCHEMA_V_NOW)
                .deleteRealmIfMigrationNeeded()
                .build();
    }
}
