package org.example.client.view.exercise;

import java.util.List;

import org.example.common.objects.exercise.Assignment;

@FunctionalInterface
public interface OnAssignmentListReceivedListener {
    void onAssignmentListReceived(List<Assignment> assignments);
}