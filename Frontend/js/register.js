document.addEventListener('DOMContentLoaded', function () {
    const registerForm = document.getElementById('registerForm');

    registerForm.addEventListener('submit', async function (e) {
        // Chặn sự kiện submit mặc định của trình duyệt
        e.preventDefault();

        // Lấy dữ liệu từ input
        const firstName = document.getElementById('inputFirstName').value;
        const lastName = document.getElementById('inputLastName').value;
        const phone = document.getElementById('inputPhone').value;
        const dob = document.getElementById('inputDoB').value;
        const password = document.getElementById('inputPassword').value;
        const passwordConfirm = document.getElementById('inputPasswordConfirm').value;
        
        // Lấy giá trị từ Radio Button (MALE hoặc FEMALE)
        const gender = document.querySelector('input[name="userGender"]:checked').value;

        //Kiểm tra phone và password không được bỏ trống
        if(!phone){
            alert("Số điện thoại không được bỏ trống!");
            document.getElementById('inputPhone').focus();
            return;
        }
        if(!password){
            alert("Mật khẩu không được bỏ trống!");
            document.getElementById('inputPassword').focus();
            return;
        }
        if(!passwordConfirm){
            alert("Mật khẩu không được bỏ trống!");
            document.getElementById('inputPasswordConfirm').focus();
            return;
        }

        //Kiểm tra Mật khẩu khớp nhau
        if (password !== passwordConfirm) {
            alert("Mật khẩu xác nhận không khớp!");
            return;
        }

        //Tạo user data 
        const userData = {
            userPhone: phone,
            userPassword: password,
            userFirstname: firstName,
            userLastname: lastName,
            userGender: gender,
            userDoB: dob
        };

        try {
            
            const response = await fetch('http://localhost:8081/beefchef/users', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(userData)
            });

            const result = await response.json();

            if (response.ok) {
                // Nếu đăng ký thành công
                alert("Đăng ký thành công!");
                window.location.href = "homePage.html"; // Chuyển hướng về trang chủ
            } else {
                // Nếu Server trả về lỗi (Ví dụ: Số điện thoại đã tồn tại)
                // result.message thường là nơi chứa chuỗi "Số điện thoại đã được sử dụng!" từ Java của bạn
                alert("Lỗi: " + (result.message || "Đăng ký thất bại"));
            }
        } catch (error) {
            console.error("Error:", error);
            alert("Không thể kết nối tới server. Vui lòng thử lại sau!");
        }
    });
});