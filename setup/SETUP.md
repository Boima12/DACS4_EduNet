# Hướng dẫn cấu hình môi trường trước khi code

1. Nạp database vào MySQL <br>
+ mở ứng dụng quản lý MySQL bên bác, nạp file setup/setup.sql <br>


2. Cấu hình tài khoản MySQL <br>
+ vào file src/main/java/org/example/server/Config.java chỉnh DATABASE_USERNAME và DATABASE_PASSWORD theo tài khoản MySQL của bác <br>


3. Load các thư viện Maven vào project <br>
+ Trong Eclipse, trên Package Exlorer hoặc Explorer, nhấn chuột phải vào project rồi chọn Maven -> Update Project... -> Nhấn OK <br>
+ Trong IntelliJ IDEA, click chuột phải vào file pom.xml -> chọn Maven -> Sync Project <br>


<hr>
<br> <br> <br> <br> <br>

# Hướng dẫn chạy thử hệ thống

1. Kiểm tra dữ liệu ban đầu <br>
- nếu muốn chạy mới hoàn toàn, chắc chắn rằng:
+ trong localStorage/ không có file MemoryBox.json (này là dữ liệu cục bộ của client)
+ trong MySQL table established_clients không có records nào (đây là dữ liệu client đăng ký với server)


2. Chạy Server <br>
- chạy file src/main/java/org/example/server/InitServer.java
- nhấn nút màu đỏ góc trái màn hình để mở server


3. Chạy Client <br>
- chạy file src/main/java/org/example/client/InitClient.java
- (nếu thiết lập lần đầu) lúc này sẽ hiện ra giao diện đăng ký với server
- (nếu đã thiết lập rồi và chạy lần 2) client sẽ tự động connect với server