document.getElementById("loginForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const userPhone = document.getElementById("inputPhone").value;
    const userPassword = document.getElementById("inputPassword").value;

    //Kiểm tra phone và password không được bỏ trống
    if(!userPhone){
        alert("Số điện thoại không được bỏ trống!");
        document.getElementById('inputPhone').focus();
        return;
    }
    if(!userPassword){
        alert("Mật khẩu không được bỏ trống!");
        document.getElementById('inputPassword').focus();
        return;
    }
    


    try {
        const response = await fetch("http://localhost:8081/beefchef/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                userPhone: userPhone,
                userPassword: userPassword
            })
        });

        if (!response.ok) throw new Error("Đăng nhập thất bại");

        const data = await response.json();

        console.log("Response:", data);

        const result = data.result;
        // Lưu token + role
        localStorage.setItem("token", result.token);
        localStorage.setItem("role", result.role);
        localStorage.setItem("username", result.userName);

        // check role
        if (result.role == "ADMIN") {
            alert("Đăng nhập thành công!");
            // chuyển sang giao diện admin
            window.location.href = "index.html";
        }

        if(result.role == "USER"){
            alert("Đăng nhập thành công!");
            // chuyển sang giao diện user
            window.location.href = "homePage.html";
        }

        if(result.role == "CHEF"){
            alert("Đăng nhập thành công!");
            // chuyển sang giao diện user
            window.location.href = "orderChef.html";
        }

        console.log("Saved username:", localStorage.getItem("username"));

    } catch (error) {
        alert("Sai tài khoản hoặc mật khẩu!");
        console.error(error);
    }
});