package com.example.apexwh.ui.returns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReturnsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ReturnsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is returns fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}