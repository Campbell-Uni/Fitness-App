package grp2.fitness.Handlers;

import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.mobileconnectors.cognito.Record;
import com.amazonaws.mobileconnectors.cognito.exceptions.DataStorageException;

import java.util.List;

public class CognitoDatasetManager extends DefaultSyncCallback {

    public static final String DATASET = "PERSONAL_DATA";

    private Dataset dataset;
    private boolean isSynced;

    public CognitoDatasetManager(CognitoSyncManager syncManager){
        dataset = syncManager.openOrCreateDataset(DATASET);
        dataset.synchronize(this);
    }

    public void setValue(String key, String value){
        dataset.put(key, value);
        isSynced = false;
        dataset.synchronize(this);
    }

    public String getValue(String key) {
        if(!isSynced){
            dataset.synchronize(this); //TODO - handle this
        }
        return dataset.get(key);
    }

    public void removeValue(String key) {
        dataset.remove(key);
        isSynced = false;
        dataset.synchronize(this);
    }

    @Override
    public void onSuccess(Dataset dataset, List<Record> updatedRecords) {
        super.onSuccess(dataset, updatedRecords);
        isSynced = true;
    }

    @Override
    public void onFailure(DataStorageException dse) {
        super.onFailure(dse);
        isSynced = false;
    }
}
