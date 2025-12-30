package org.example.common.objects.services.exercise;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Submission implements Serializable {
    public String assignmentId;
    public String studentName;
    public String studentId;
    public String fileName;
    public byte[] fileData;
    public LocalDateTime submitTime;

    @Override
    public String toString() {
        return studentId + " - " + studentName + " | " + fileName;
    }
}