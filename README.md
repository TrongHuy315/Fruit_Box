# **Fruit_Box Game**

1. **Thư viện cần thiết**
    + Các thư viện chuẩn của Java (phiên bản 21+).
    + Thư viện javafx phiên bản `23.0.2_windows-x64_bin-sdk` hoặc các phiên bản khác gần nhất.

2. **Hướng dẫn** *(Chạy trên Visual Studio Code)*
    - ***Bước 1***
        + Chọn folder thích hợp ở máy tính.
        + Thực hiện lệnh `git clone https://github.com/TrongHuy315/Fruit_Box.git` để download repository về máy tính.
    - ***Bước 2***
        + Thực hiện download thư viện javafx, tại: `https://www.oracle.com/java/technologies/install-javafx-sdk.html`.
        + Tiến hành **Extract All** vào thư mục chỉ định và copy đường dẫn tới folder lib (dựa vào tài liệu hướng dẫn javafx).
    - ***Bước 3*** *(Thực hiện trong terminal, ngay tại thư mục Fruit_Box)*
        + Nếu bạn muốn chỉnh sửa và biên dịch lại từ đầu, thực hiện lệnh: `javac --module-path "path\to\your\javafx" --add-modules javafx.controls,javafx.media .\Source\Main\*.java .\Source\Menu\*.java .\Source\Rim\*.java .\Source\Components\*.java .\Source\Game\*.java .\Source\Utils\*.java .\Source\Bot\Bot_1\*.java`.
        + Để chạy ngay chương trình, thực hiện lệnh: `java --module-path "path\to\your\javafx" --add-modules javafx.controls,javafx.media Source.Main.Main`.
    - ***Bước 4***
        + Trải nghiệm trò chơi.
        + Nếu bạn cảm thấy khó khăn, hãy kích hoạt bot bằng nút "**Bot Play**" ở góc trên phải.
