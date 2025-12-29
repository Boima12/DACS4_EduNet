package org.example.client.view.exercise;

import org.example.common.objects.exercise.Assignment;

@FunctionalInterface
public interface OnAssignmentReceivedListener {
    void onAssignmentReceived(Assignment assignment);
}