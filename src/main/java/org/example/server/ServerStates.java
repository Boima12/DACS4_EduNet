package org.example.server;

import org.example.common.objects.services.exercise.Assignment;
import org.example.common.objects.services.exercise.Submission;
import org.example.server.model.OnServerCloseListener;
import org.example.server.model.OnServerStartListener;
import org.example.server.view.dashboard.*;
import org.example.server.view.exercise.OnAssignmentCreatedListener;
import org.example.server.view.exercise.OnSubmissionReceivedListener;
import org.example.server.view.manage.Manage;
import org.example.server.view.manage.OnSystemInfoRequestListener;
import org.example.server.view.manage.OnSystemInfoResponseListener;
import org.example.server.view.watch.OnWatchControllerShowListener;
import org.example.server.view.whiteBoard.OnWhiteBoardControllerShowListener;

/**
 * Server States, nơi trung chuyển các trạng thái và callback cho code server
 *
 */
public class ServerStates {
	
	public static int MA_LIEN_KET = 1;

    // UI states
    public static LienKetModal lkModal = null;
    public static Manage manage = null;

    /* ================= SERVER CORE LISTENERS ================= */
    public static OnServerStartListener onServerStartListener;
    public static void setOnServerStartListenerCallback(OnServerStartListener callback) {
        onServerStartListener = callback;
    }

    public static OnServerCloseListener onServerCloseListener;
    public static void setOnServerCloseListenerCallback(OnServerCloseListener callback) {
        onServerCloseListener = callback;
    }

    public static Runnable onClientsCleanUpListener;
    public static void setOnClientsCleanUpListener(Runnable callback) {
        onClientsCleanUpListener = callback;
    }
    public static void fireClientsCleanUp() {
        if (onClientsCleanUpListener != null) {
            onClientsCleanUpListener.run();
        }
    }

    /* ================= DASHBOARD LISTENERS ================= */
    public static OnClient_dashboardNewClientListener onClientDashboardNewClientListener;
    public static void setOnClient_dashboardNewClientListenerCallback(OnClient_dashboardNewClientListener callback) {
        onClientDashboardNewClientListener = callback;
    }

    public static OnClient_dashboardConnectedListener onClientDashboardConnectedListener;
    public static void setOnClient_dashboardConnectedCallback(OnClient_dashboardConnectedListener callback) {
        onClientDashboardConnectedListener = callback;
    }

    public static OnClient_dashboardDisconnectedListener onClientDashboardDisconnectedListener;
    public static void setOnClient_dashboardDisconnectedCallback(OnClient_dashboardDisconnectedListener callback) {
        onClientDashboardDisconnectedListener = callback;
    }

    /* ================= SYSTEM & NOTIFICATION ================= */
    public static OnSystemInfoRequestListener onSystemInfoRequestListener;
    public static void setOnSystemInfoRequestListenerCallback(OnSystemInfoRequestListener callback) {
        onSystemInfoRequestListener = callback;
    }

    public static OnSystemInfoResponseListener onSystemInfoResponseListener;
    public static void setOnSystemInfoResponseListenerCallback(OnSystemInfoResponseListener callback) {
        onSystemInfoResponseListener = callback;
    }

    public static OnNotificationAllRequestListener onNotificationAllRequestListener;
    public static void setOnNotificationAllRequestListenerCallback(OnNotificationAllRequestListener callback) {
        onNotificationAllRequestListener = callback;
    }

    public static OnNotificationSingleRequestListener onNotificationSingleRequestListener;
    public static void setOnNotificationSingleRequestListenerCallback(OnNotificationSingleRequestListener callback) {
        onNotificationSingleRequestListener = callback;
    }

    /* ================= TOOLS (WATCH, WHITEBOARD, LOCK) ================= */
    public static OnWatchControllerShowListener onWatchControllerShowListener;
    public static void setOnWatchControllerShowListenerCallback(OnWatchControllerShowListener callback) {
        onWatchControllerShowListener = callback;
    }

    public static OnWhiteBoardControllerShowListener onWhiteBoardControllerShowListener;
    public static void setOnWhiteBoardControllerShowListenerCallback(OnWhiteBoardControllerShowListener callback) {
        onWhiteBoardControllerShowListener = callback;
    }

    public static OnLockListener onLockListener;
    public static void setOnLockListenerCallback(OnLockListener callback) {
        onLockListener = callback;
    }

    /* ================= EXERCISE & ASSIGNMENT (SỬA ĐỔI CHÍNH) ================= */

    // 1. Hiển thị Giao diện quản lý bài tập (Giao cho CoreServer thực hiện)
    public interface OnExerciseViewShowListener { void onShow(); }
    public static OnExerciseViewShowListener onExerciseViewShowListener;
    public static void setOnExerciseViewShowListenerCallback(OnExerciseViewShowListener cb) {
        onExerciseViewShowListener = cb;
    }
    public static void fireExerciseViewShow() {
        if (onExerciseViewShowListener != null) onExerciseViewShowListener.onShow();
    }

    // 2. Giao bài mới
    public static OnAssignmentCreatedListener onAssignmentCreatedListener;
    public static void setOnAssignmentCreatedListener(OnAssignmentCreatedListener callback) {
        onAssignmentCreatedListener = callback;
    }
    public static void fireAssignmentCreated(Assignment a) {
        if (onAssignmentCreatedListener != null) onAssignmentCreatedListener.onAssignmentCreated(a);
    }

    // 3. Nhận bài nộp từ sinh viên (Để UI cập nhật bảng danh sách bài nộp)
    public static OnSubmissionReceivedListener onSubmissionReceivedListener;
    public static void setOnSubmissionReceivedListener(OnSubmissionReceivedListener callback) {
        onSubmissionReceivedListener = callback;
    }
    public static void fireSubmissionReceived(Assignment a, Submission s) {
        if (onSubmissionReceivedListener != null) onSubmissionReceivedListener.onSubmissionReceived(a, s);
    }

    // 4. Phản hồi kết quả nộp bài về cho Client (Giao cho ServerNetwork thực hiện)
    public interface OnSubmissionResultListener {
        void onResult(String clientName, boolean success, String message);
    }
    private static OnSubmissionResultListener onSubmissionResultListener;
    public static void setOnSubmissionResultListenerCallback(OnSubmissionResultListener cb) {
        onSubmissionResultListener = cb;
    }
    public static void fireSubmissionResultToClient(String clientName, boolean success, String message) {
        if (onSubmissionResultListener != null) onSubmissionResultListener.onResult(clientName, success, message);
    }
}
