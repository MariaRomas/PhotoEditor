package com.example.photoeditor.interfaces;

public interface EditImageFragmentListener {
    void onBrightnessChanged(int brightness);
    void onSaturationChanged(float saturation);
    void onContrastChanged(float constraint);
    void onEditStarted();
    void onEditCompleted();
}
