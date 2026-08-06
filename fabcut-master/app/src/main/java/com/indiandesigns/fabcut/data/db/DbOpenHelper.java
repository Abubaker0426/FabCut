package com.indiandesigns.fabcut.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.indiandesigns.fabcut.data.db.model.DaoMaster;
import com.indiandesigns.fabcut.data.db.model.DocumentDao;
import com.indiandesigns.fabcut.data.network.model.ScanBarcodeDao;
import com.indiandesigns.fabcut.di.ApplicationContext;
import com.indiandesigns.fabcut.di.DatabaseInfo;
import com.indiandesigns.fabcut.utils.AppLogger;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Instantiates DatabaseOpenHelper to provide DaoMaster
 * with greenDAO abstraction of the SQLite database
 */
@Singleton
public class DbOpenHelper extends DaoMaster.OpenHelper {

    /**
     * Parameterized Constructor
     * Initializes the DatabaseOpenHelper to work with SQLiteOpenHelper
     *
     * @param context Injected with Dagger
     * @param name Injected with Dagger
     */
    @Inject
    public DbOpenHelper(@ApplicationContext Context context, @DatabaseInfo String name) {
        super(context, name);
    }

    /**
     * Called when you upgrade the schemaVersion in build.gradle
     * Performs the migration using MigrationHelper
     *
     * @param db Database instance
     * @param oldVersion Previous version of the database
     * @param newVersion New version of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        AppLogger.d("DEBUG", "DB_OLD_VERSION : " + oldVersion + ", DB_NEW_VERSION : " + newVersion);

        MigrationHelper.migrate(db,
                ScanBarcodeDao.class,
                DocumentDao.class);
    }
}
