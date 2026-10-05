package com.company.fieldattendance.ui.provider;
import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.company.fieldattendance.data.local.SessionManager;
import com.company.fieldattendance.data.model.CeoDetailsResponse;
import com.company.fieldattendance.data.model.CeoResponse;
import com.company.fieldattendance.data.model.CreateCeoRequest;
import com.company.fieldattendance.data.repository.ProviderRepository;
import com.company.fieldattendance.utils.Resource;
import java.util.List;

public class ProviderViewModel extends AndroidViewModel {
    private ProviderRepository repository;
    private SessionManager sessionManager;
    
    public ProviderViewModel(@NonNull Application application) {
        super(application);
        repository = new ProviderRepository();
        sessionManager = new SessionManager(application);
    }
    
    public LiveData<Resource<List<CeoResponse>>> getCeos() {
        return repository.getCeos(sessionManager.getToken());
    }
    
    public LiveData<Resource<CeoDetailsResponse>> getCeoDetails(String id) {
        return repository.getCeoDetails(sessionManager.getToken(), id);
    }
    
    public LiveData<Resource<CeoResponse>> createCeo(CreateCeoRequest request) {
        return repository.createCeo(sessionManager.getToken(), request);
    }
    
    public LiveData<Resource<CeoResponse>> updateCeoStatus(String id, String status) {
        return repository.updateCeoStatus(sessionManager.getToken(), id, status);
    }
}
