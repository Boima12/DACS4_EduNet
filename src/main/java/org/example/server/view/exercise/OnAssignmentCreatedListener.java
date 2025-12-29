package org.example.server.view.exercise;

import org.example.common.objects.exercise.Assignment;

@FunctionalInterface
public interface OnAssignmentCreatedListener {
    void onAssignmentCreated(Assignment assignment);
}
