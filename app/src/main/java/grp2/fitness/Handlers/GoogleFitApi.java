package grp2.fitness.Handlers;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;

import java.util.concurrent.TimeUnit;

public class GoogleFitApi implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status>{

    public interface GoogleFitApiCallback{
        void onConnected();
        void onConnectionSuspended();
        void onConnectionFailed();
    }

    public static final int REQUEST_KEY = 1;
    private boolean isAuthenticating = false;

    private Context context;
    private GoogleFitApiCallback callback;

    private GoogleApiClient googleApiClient;
    private OnDataPointListener listener;

    public GoogleFitApi(Context context, GoogleFitApiCallback callback){
        this.context = context;
        this.callback = callback;
    }

    private void buildApiClient(){
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        callback.onConnected();
    }

    @Override
    public void onConnectionSuspended(int i) {
        callback.onConnectionSuspended();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!isAuthenticating){
            try {
                isAuthenticating = true;
                connectionResult.startResolutionForResult((Activity) context, REQUEST_KEY);
            } catch (IntentSender.SendIntentException e) {
                Log.e("FITNESS API AUTH", e.getMessage());
            }
            callback.onConnectionFailed();
        }
    }

    public void connect(){
        if(googleApiClient == null){
            buildApiClient();
        }

        if(!googleApiClient.isConnected() && !googleApiClient.isConnecting()){
            googleApiClient.connect();
        }
    }

    public void disconnect(){
        if(listener != null){
            removeListener();
        }else if(googleApiClient != null && googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
    }

    private void removeListener(){
        Fitness.SensorsApi.remove(googleApiClient, listener).setResultCallback(this);
    }

    @Override
    public void onResult(@NonNull Status status) {
        listener = null;
        disconnect();
    }

    public void registerSensorListener(final OnDataPointListener listener, final DataType[] dataTypes){
        this.listener = listener;

        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes(dataTypes)
                .setDataSourceTypes(DataSource.TYPE_RAW)
                .build();

        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(@NonNull DataSourcesResult dataSourcesResult) {
                for( DataSource dataSource : dataSourcesResult.getDataSources() ) {
                    for(DataType dataType : dataTypes){
                        if(dataType.equals(dataSource.getDataType())){
                            SensorRequest request = new SensorRequest.Builder()
                                    .setDataSource( dataSource )
                                    .setDataType( dataType )
                                    .setSamplingRate( 3, TimeUnit.SECONDS )
                                    .build();
                            Fitness.SensorsApi.add(googleApiClient, request, listener);
                        }
                    }
                }
            }
        };

        Fitness.SensorsApi.findDataSources(googleApiClient, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);
    }

    //Getters and Setters
    public void setAuthState(boolean isAuthenticating){
        this.isAuthenticating = isAuthenticating;
    }
    public boolean getAuthState(){
        return isAuthenticating;
    }
    public void setCallback(GoogleFitApiCallback callback){
        this.callback = callback;
    }
    public GoogleApiClient getClient(){
        return googleApiClient;
    }

}
