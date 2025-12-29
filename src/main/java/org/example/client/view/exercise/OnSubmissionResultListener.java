package org.example.client.view.exercise;

@FunctionalInterface
public interface OnSubmissionResultListener {
    void onSubmissionResult(boolean success, String message);

	//void onSubmissionResult(boolean success, String message);
}