package com.indiandesigns.fabcut.ui.leader.jobs.edit;

import com.androidnetworking.error.ANError;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.indiandesigns.fabcut.R;
import com.indiandesigns.fabcut.data.DataManager;
import com.indiandesigns.fabcut.data.network.model.AddPartStickersRequest;
import com.indiandesigns.fabcut.data.network.model.FetchPartsDetails;
import com.indiandesigns.fabcut.data.network.model.FetchPartsResponse;
import com.indiandesigns.fabcut.data.network.model.LocationRequest;
import com.indiandesigns.fabcut.data.network.model.SizeDetail;
import com.indiandesigns.fabcut.data.network.model.SizeDetailBarcodes;
import com.indiandesigns.fabcut.ui.base.BasePresenter;
import com.indiandesigns.fabcut.ui.common.model.BundleDetail;
import com.indiandesigns.fabcut.ui.common.model.OcLay;
import com.indiandesigns.fabcut.ui.common.model.OcLayDetail;
import com.indiandesigns.fabcut.utils.AppConstants;
import com.indiandesigns.fabcut.utils.CommonUtils;
import com.indiandesigns.fabcut.utils.rx.SchedulerProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;

public class EditBundleDialogPresenter<V extends EditBundleDialogMvpView> extends BasePresenter<V> implements EditBundleDialogMvpPresenter<V> {

    private static final String TAG = "EditJobDialogPresenter";

    /**
     * Parameterized Constructor
     * <p>
     * Instantiates data operator classes
     *
     * @param dataManager         Injected with Dagger
     * @param schedulerProvider   Injected with Dagger
     * @param compositeDisposable Injected with Dagger
     */
    @Inject
    public EditBundleDialogPresenter(DataManager dataManager, SchedulerProvider schedulerProvider, CompositeDisposable compositeDisposable) {
        super(dataManager, schedulerProvider, compositeDisposable);
    }

    /**
     * Calculate the allowed quantity when splitting bundles
     * @param sizeList The list of sizes
     */
    @Override
    public int getBundleSplitAllowedQuantity(List<OcLayDetail> sizeList) {
        int returnValue = 0;
        int sizeRatio = 0;
        for (OcLayDetail ocLay:
                sizeList) {
            sizeRatio = ocLay.getRatio();
            if(sizeRatio > 0){
                returnValue = ocLay.getQuantity()/sizeRatio;
                break;
            }
        }
        return returnValue;
    }

    /**
     * Function responsible to create BundleDetail object
     * @param bundleQuantity Bundle quantity entered in UI
     * @param bundleCountry Bundle quantity selected in UI
     */
    @Override
    public BundleDetail getBundleDetail(int bundleQuantity, String bundleCountry) {
        BundleDetail bundleDetail = new BundleDetail();
        bundleDetail.setBundleQuantity(bundleQuantity);
        bundleDetail.setBundleCountry(bundleCountry);
        return bundleDetail;
    }

    /**
     * This function validates to check parts been selected to print from second bundle onwards
     * @param partsDetails List of {@link FetchPartsDetails}
     * @return boolean
     */
    @Override
    public boolean validateBundlePartsDetailList(List<FetchPartsDetails> partsDetails) {
        for (FetchPartsDetails fetchPartsDetails : partsDetails) {
            if(fetchPartsDetails.getIsSelected() == 1){
                return true;
            }
        }
        return false;
    }

    /**
     * This method will add an augmented size to the ocLayDetail model which
     * is used while printing barcodes
     *
     * @param ocLayDetailsList       The list of the OcLayDetails objects
     * @param countryAndQuantityList The list of countries with associated quantity
     * @return
     */
    @Override
    public List<OcLayDetail> addAugmentedSizeToOcLayDetailList(List<OcLayDetail> ocLayDetailsList, ArrayList<BundleDetail> countryAndQuantityList) {
        List<OcLayDetail> newOcLayDetails = new ArrayList<>();

        String charForNumber;
        for (int index = 0; index < countryAndQuantityList.size(); index++) {
            for (OcLayDetail ocLayDetail :
                    ocLayDetailsList) {
                if (ocLayDetail.getRatio() > 0) {
                    for (int i = 1; i <= ocLayDetail.getRatio(); i++) {
                        charForNumber = getCharForNumber(i);
                        BundleDetail bundleDetail = countryAndQuantityList.get(index);

                        OcLayDetail temp = new OcLayDetail();
                        temp.setRatio(1);
                        temp.setTotalQuantity(ocLayDetail.getQuantity() / ocLayDetail.getRatio());
                        temp.setBundleName(getBundleNameForNumber(index));
                        temp.setCountry(bundleDetail.getBundleCountry());
                        temp.setQuantity(bundleDetail.getBundleQuantity());
                        temp.setSize(ocLayDetail.getSize());
                        if(ocLayDetail.getRatio() > 1){ temp.setAugmentedSize(charForNumber); }
                        temp.setFitType(ocLayDetail.getFitType());
                        temp.setItemCode(ocLayDetail.getItemCode());
                        temp.setItemDesc(ocLayDetail.getItemDesc());
                        temp.setBundleNumber(index + 1 );
                        newOcLayDetails.add(temp);
                    }
                }
            }
        }
        return newOcLayDetails;
    }

    /**
     * This method is used to create the list of size details object.
     *
     * @param augmentedSizeList
     * @param partsList
     * @return
     */
    @Override
    public List<SizeDetail> createSizeDetailsList(List<OcLayDetail> augmentedSizeList, List<FetchPartsDetails> partsList) {
        List<SizeDetail> sizeDetailList = new ArrayList<>();
        List<FetchPartsDetails> selectedPartsDetailsList = getSelectedPartsList(partsList);

        for (OcLayDetail ocLayDetail : augmentedSizeList) {
            List<FetchPartsDetails> actualPartsDetailList = selectedPartsDetailsList;
            if(ocLayDetail.getBundleNumber() == 1){
                actualPartsDetailList = partsList;
            }
            SizeDetail sizeDetail = getSizeDetailWithBarcodeDetails(ocLayDetail, actualPartsDetailList, ocLayDetail.getBundleNumber());
            sizeDetailList.add(sizeDetail);
        }
        return sizeDetailList;
    }

    /**
     * This method creates sizeDetails along with barcode details included part unique and part name with barcode number
     *
     * @param ocLayDetail
     * @param partsList
     * @return SizeDetail
     */
    private SizeDetail getSizeDetailWithBarcodeDetails(OcLayDetail ocLayDetail, List<FetchPartsDetails> partsList, int bundleNumber) {
        String augmentedSize = null;

        //below code to split and trim country(i.e, IN | INDIA) to country code (IN) and country name (INDIA)
        String country = ocLayDetail.getCountry();
        String[] countrySplit = country.split("\\|");
        String countryCode = countrySplit[0].trim();
        String countryName = countrySplit[1].trim();

        if (ocLayDetail.getAugmentedSize() == null) {
            augmentedSize = "NA";
        } else {
            augmentedSize = ocLayDetail.getAugmentedSize();
        }

        SizeDetail sizeDetail = new SizeDetail(ocLayDetail.getItemDesc(), ocLayDetail.getItemCode(), ocLayDetail.getSize(), augmentedSize, countryCode, countryName,
                ocLayDetail.getQuantity(), bundleNumber, new ArrayList<>());

        for (int i = 0; i < partsList.size(); i++) {
            Random random = new Random();
            char[] alphabet = AppConstants.ALPHABETS.toCharArray();
            int sizeId = 10;
            String barcode = NanoIdUtils.randomNanoId(random, alphabet, sizeId);

            FetchPartsDetails fetchPartsDetails = partsList.get(i);
            SizeDetailBarcodes sizeDetailBarcodes = new SizeDetailBarcodes(fetchPartsDetails.getPartsUnique(), fetchPartsDetails.getPart(), barcode);
            sizeDetail.getBarcodeDetails().add(sizeDetailBarcodes);
        }
        return sizeDetail;
    }

    /**
     * This function is responsible to get selectParts List
     *
     * @param partsList
     * @return selected List of FetchPartsDetails
     */
    private List<FetchPartsDetails> getSelectedPartsList(List<FetchPartsDetails> partsList) {
        List<FetchPartsDetails> selectedPartsList = new ArrayList<>();

        for (FetchPartsDetails fetchPartsDetails : partsList) {
            if(fetchPartsDetails.getIsSelected() == 1){
                selectedPartsList.add(fetchPartsDetails);
            }
        }
        return selectedPartsList;
    }

    /**
     * This method will determine whether there is an augmented size or not
     *
     * @param i
     * @return
     */
    private String getCharForNumber(int i) {
        return i > 0 && i < 27 ? String.valueOf((char) (i + 64)) : null;
    }

    private String getBundleNameForNumber(int i) {
        return String.format("B%s", i + 1);
    }


    /**
     * Fetch parts for a Oc Number
     * Create PDF based on the parts data received from server
     * @param ocLay Job data
     */
    @Override
    public void fetchParts(OcLay ocLay) {
                fetchPartsFromServer(ocLay);
    }

    /**
     * Fetch parts from server and save, with job info
     *
     * @param ocLay Job Info
     */

    private void fetchPartsFromServer(OcLay ocLay) {
        getMvpView().showLoading(R.string.generating_stickers);
        LocationRequest request = new LocationRequest();
        request.setLocation(getDataManager().getLocation());

        getCompositeDisposable().add(getDataManager()
                .doFetchPartsForOCCall(ocLay.getOcNo(), request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<FetchPartsResponse>() {
                    @Override
                    public void accept(FetchPartsResponse response) throws Exception {
                        if(!isViewAttached()){
                            return;
                        }
                        getMvpView().hideLoading();
                        String fileName = CommonUtils.getFileName(ocLay);
                        getMvpView().createBarcodes(ocLay, response, fileName);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().hideLoading();
                        getMvpView().dismissDialog();
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError, R.string.fetch_parts_error);
                        }
                    }
                })
        );
    }

    /**
     * Save parts data including the generated barcodes
     * @param ocLay Job details
     * @param sizeDetails List of sizes and barcodes generated for those for parts
     * @return
     */

    @Override
    public void saveParts(OcLay ocLay, List<SizeDetail> sizeDetails, FetchPartsResponse fetchPartsResponse, String fileName) {
        AddPartStickersRequest request = new AddPartStickersRequest(
                getDataManager().getLocation(), ocLay.getFitType(), ocLay.getLay(), sizeDetails, ocLay.getJobId()
        );
            getCompositeDisposable().add(getDataManager()
                .doAddBarcodesForOCCall(ocLay.getOcNo(), request)
                .subscribeOn(getSchedulerProvider().io())
                .observeOn(getSchedulerProvider().ui())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean response) throws Exception {
                        if(!isViewAttached()){
                            return;
                        }
                        getMvpView().printBarcodes(ocLay, fetchPartsResponse, fileName);
                        getMvpView().hideLoading();
                        getMvpView().dismissDialog();

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (!isViewAttached()) {
                            return;
                        }
                        getMvpView().dismissDialog();
                        getMvpView().hideLoading();
                        if (throwable instanceof ANError) {
                            ANError anError = (ANError) throwable;
                            handleApiError(anError, R.string.save_parts_errors);
                        }
                    }
                })
        );
    }

}
