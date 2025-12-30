package org.example.client.view.exercise;

import org.example.common.objects.services.exercise.Assignment;

import java.util.List;

@FunctionalInterface
public interface OnAssignmentListReceivedListener {
    void onAssignmentListReceived(List<Assignment> assignments);
}