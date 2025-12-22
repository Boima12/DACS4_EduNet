Thư mục ../common/objects chứa các lớp để tạo JSON

### ../common/objects/MemoryBox.java
- là file bên Client để lưu trữ các thông tin local của client, được dùng trong DACS4_EduNet/localStorage

### ../common/objects/messages
Các file trong thư mục này là định dạng class JSON cho tin nhắn kết nối được truyền qua lại giữa client và server.
- EstablishRequestJSON.java: Yêu cầu thiết lập kết nối lần đầu từ Client đến Server
- EstablishResponseJSON.java: Phản hồi thiết lập kết nối lần đầu từ Server đến Client
- ConnectionRequestJSON.java: Yêu cầu kết nối bình thường từ Client đến Server
- ConnectionResponseJSON.java: Phản hồi kết nối từ Server đến Client
- SystemInfoRequestJSON.java: Yêu cầu thông tin hệ thống được gửi từ Server đến Client
- SystemInfoResponseJSON.java: Phản hồi thông tin hệ thống từ Client ra lại cho Server