package org.example.server.view.exercise;

import org.example.common.objects.services.exercise.Assignment;
import org.example.common.objects.services.exercise.Submission;

@FunctionalInterface
public interface OnSubmissionReceivedListener {
    void onSubmissionReceived(Assignment assignment, Submission submission);
}
