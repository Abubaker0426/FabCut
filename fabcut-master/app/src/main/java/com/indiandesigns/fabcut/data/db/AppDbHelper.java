package com.indiandesigns.fabcut.data.db;

import com.indiandesigns.fabcut.data.db.model.DaoMaster;
import com.indiandesigns.fabcut.data.db.model.DaoSession;
import com.indiandesigns.fabcut.data.network.model.ScanBarcode;
import com.indiandesigns.fabcut.data.network.model.ScanBarcodeDao;

import org.greenrobot.greendao.query.DeleteQuery;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

/**
 * Manages the transactions with the Database
 */

@Singleton
public class AppDbHelper implements DbHelper {

    /**
     * Manages all available DAO objects for a specific schema,
     * which you can acquire using one of the getter methods
     */
    private final DaoSession mDaoSession;

    /**
     * Parameterized Constructor
     *
     * Instantiates DaoMaster with DatabaseOpenHelper
     * and retrieves a new DaoSession
     *
     * @param dbOpenHelper injected with Dagger
     */
    @Inject
    public AppDbHelper(DbOpenHelper dbOpenHelper) {
        mDaoSession = new DaoMaster(dbOpenHelper.getWritableDb()).newSession();
    }

    /**
     * Deletes the list of scan barcodes for a job session
     *
     * @return An observable of boolean which is always true
     */

    @Override
    public Observable<Boolean> deleteScanBarcodes(int jobId) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                DeleteQuery<ScanBarcode> tableDeleteQuery = mDaoSession.queryBuilder(ScanBarcode.class)
                        .buildDelete();
                tableDeleteQuery.executeDeleteWithoutDetachingEntities();
                return true;
            }
        });
    }

    /**
     * Gets the list of scan barcodes for a job session
     *
     * @return An observable of List of scan barcodes
     */

    @Override
    public Observable<List<ScanBarcode>> getScanBarcodes(int jobId) {
        return Observable.fromCallable(new Callable<List<ScanBarcode>>() {
            @Override
            public List<ScanBarcode> call() throws Exception {
                QueryBuilder<ScanBarcode> queryBuilder = mDaoSession.getScanBarcodeDao().queryBuilder()
                        .where(ScanBarcodeDao.Properties.JobId.eq(jobId));
                return queryBuilder.list();
            }
        });
    }

    /**
     * Gets the list of scan barcodes for a job session and
     * and item code
     *
     * @return An observable of List of scan barcodes
     */

    @Override
    public Observable<List<ScanBarcode>> getScanBarcodes(int jobId, String itemCode) {
        return Observable.fromCallable(new Callable<List<ScanBarcode>>() {
            @Override
            public List<ScanBarcode> call() throws Exception {
                QueryBuilder<ScanBarcode> queryBuilder = mDaoSession.getScanBarcodeDao().queryBuilder()
                        .where(ScanBarcodeDao.Properties.JobId.eq(jobId), ScanBarcodeDao.Properties.ItemCode.eq(itemCode));
                return queryBuilder.list();
            }
        });
    }

    /**
     * Inserts a new scan barcode into the barcodes table
     *
     * @param barcode The scan barcode to be inserted
     * @return Observable of true if barcode was inserted into the table
     */

    @Override
    public Observable<Long> saveScanBarcode(ScanBarcode barcode) {
        return Observable.fromCallable(new Callable<Long>() {
            @Override
            public Long call() throws Exception {
                return mDaoSession.getScanBarcodeDao().insert(barcode);
            }
        });
    }

    /**
     * Deletes a given scan barcode in the barcodes table
     *
     * @param barcode The scan barcode to be deleted
     * @return Observable of true if barcode was deleted
     */

    @Override
    public Observable<Boolean> deleteScanBarcode(ScanBarcode barcode) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mDaoSession.getScanBarcodeDao().delete(barcode);
                return true;
            }
        });
    }

    /**
     * Updates a given scan barcode in the barcodes table
     *
     * @param barcode The scan barcode to be updated
     * @return Observable of true if barcode was updated
     */

    @Override
    public Observable<Boolean> updateScanBarcode(ScanBarcode barcode) {
        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                mDaoSession.getScanBarcodeDao().update(barcode);
                return true;
            }
        });
    }

}
