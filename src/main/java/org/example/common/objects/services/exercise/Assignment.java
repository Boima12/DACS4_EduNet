package org.example.common.objects.services.exercise;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Assignment implements Serializable {
    private static final long serialVersionUID = 1L;

    public String id;
    public String title;
    public String content;
    public LocalDateTime assignedDate;
    public LocalDateTime deadline;
    public List<Submission> submissions = new ArrayList<>();

    @Override
    public String toString() {
        return title + " (" + submissions.size() + " bài nộp)";
    }
}
