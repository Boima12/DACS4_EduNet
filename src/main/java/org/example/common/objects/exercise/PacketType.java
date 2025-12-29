package org.example.common.objects.exercise;

public enum PacketType {
    ASSIGNMENT_LIST,     // Server → Client (danh sách bài)
    SUBMISSION,          // Client → Server (nộp bài)
    SUBMISSION_RESULT,   // Server → Client (nộp thành công / thất bại)
    REQUEST_ASSIGNMENTS, // Client → Server (yêu cầu danh sách)
    ERROR                // Thông báo lỗi
}
