package org.example.client;

import org.example.client.view.eClient.OnConnectionListener;
import org.example.client.view.eClientConnector.OnEstablishListener;
import org.example.client.view.exercise.OnAssignmentListReceivedListener;
import org.example.client.view.exercise.OnAssignmentReceivedListener;
import org.example.client.view.exercise.OnSubmissionResultListener;
import org.example.client.view.exercise.ShowExerciseListener;

/**
 * Client States, nơi trung chuyển các trạng thái và callback cho code client
 */
public class ClientStates {

    // =================== Establish & Connection ===================
    public static OnEstablishListener onEstablishListener;
    public static void setOnEstablishListenerCallback(OnEstablishListener callback) {
        onEstablishListener = callback;
    }

    public static OnConnectionListener onConnectionListener;
    public static void setOnConnectionListenerCallback(OnConnectionListener callback) {
        onConnectionListener = callback;
    }

    // ===== Assignment =====
    public static OnAssignmentListReceivedListener onAssignmentListReceivedListener;
    public static void setOnAssignmentListReceivedListener(OnAssignmentListReceivedListener callback) {
        onAssignmentListReceivedListener = callback;
    }
    
    public static OnAssignmentReceivedListener onAssignmentReceivedListener;
    public static void setOnAssignmentReceivedListener(OnAssignmentReceivedListener callback) {
        onAssignmentReceivedListener = callback;
    }
    // ===== Submission result =====
    public static OnSubmissionResultListener onSubmissionResultListener;
    public static void setOnSubmissionResultListener(OnSubmissionResultListener callback) {
        onSubmissionResultListener = callback;
    }
    
    public static ShowExerciseListener onShowExerciseListener;
    public static void setOnShowExerciseListener(ShowExerciseListener callback) {
        onShowExerciseListener = callback;
    }
    
	public static void fireShowExerciseView() {
	    if (onShowExerciseListener != null)
	        onShowExerciseListener.onShowExercise();
	}
	   
	// SỬA: Implement fireSubmissionResult (fire listener để UI update khi có result từ server hoặc lỗi local)
    public static void fireSubmissionResult(boolean success, String message) {
        if (onSubmissionResultListener != null) {
            onSubmissionResultListener.onSubmissionResult(success, message);
        }
    }
}
