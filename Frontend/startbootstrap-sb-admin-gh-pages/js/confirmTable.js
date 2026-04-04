document.addEventListener("DOMContentLoaded", function() {
    loadTables();
});

async function loadTables() {
    const select = document.getElementById("tableSelect");
    
    try {
        const response = await fetch("http://localhost:8081/beefchef/tables");
        
        if (!response.ok) {
            throw new Error(`Lỗi server: ${response.status}`);
        }
        
        const tables = await response.json();
        
        select.innerHTML = '<option value="">Chọn bàn</option>';
        
        const availableTables = tables.filter(table => table.tableStatus === 'AVAILABLE');
        
        if (availableTables.length === 0) {
            select.innerHTML = '<option value="">Hiện tại không có bàn trống</option>';
            return;
        }
        
        availableTables.forEach(table => {
            const option = document.createElement("option");
            option.value = table.tableName;   // hoặc table.tableId nếu backend dùng ID
            option.textContent = `Bàn ${table.tableName} - Còn trống`;
            select.appendChild(option);
        });
    } catch (error) {
        console.error("Lỗi load tables:", error);
        select.innerHTML = '<option value="">Lỗi tải danh sách bàn. Vui lòng thử lại.</option>';
    }
}

// ==================== HÀM XÁC NHẬN ====================
async function confirmOrder() {
    const tableSelect = document.getElementById("tableSelect");
    const phoneInput = document.getElementById("phone");
    
    const tableId = tableSelect.value;
    const phone = phoneInput.value.trim();

    if (!tableId) {
        alert("Vui lòng chọn bàn!");
        return;
    }
    if (!phone) {
        alert("Vui lòng nhập số điện thoại!");
        return;
    }

    // Tìm nút để disable (tránh click nhiều lần)
    const btn = document.querySelector(".btn-order");
    const originalText = btn.textContent;

    btn.disabled = true;
    btn.textContent = "Đang xử lý...";

    try {
        const response = await fetch('http://localhost:8081/beefchef/orders', {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                // Nếu backend có JWT hoặc token, thêm dòng này sau:
                // "Authorization": "Bearer " + (localStorage.getItem("token") || "")
            },
            body: JSON.stringify({
                tableId: tableId,      // hoặc tableName tùy backend
                userPhone: phone,
                items: []
            })
        });

        // Đọc response đúng cách
        let orderData;
        const contentType = response.headers.get("content-type");

        if (response.ok) {
            if (contentType && contentType.includes("application/json")) {
                orderData = await response.json();
            } else {
                // Một số trường hợp server trả text thành công
                const text = await response.text();
                console.log("Response text:", text);
                orderData = { orderId: "unknown" }; // fallback
            }
        } else {
            // Xử lý lỗi 401, 404, 400...
            let errorMsg = `Lỗi ${response.status}`;
            try {
                const errorJson = await response.json();
                errorMsg += ` - ${errorJson.message || errorJson.error || ''}`;
            } catch (e) {
                const errorText = await response.text();
                errorMsg += ` - ${errorText.substring(0, 200)}`;
            }
            throw new Error(errorMsg);
        }

        // Lưu thông tin order
        localStorage.setItem("currentOrder", JSON.stringify({
            orderId: orderData.orderId || orderData.id || "temp-order",
            tableId: tableId,
            customerPhone: phone,
            items: []
        }));

        console.log('Tạo order thành công cho bàn:', tableId);
        alert("Tạo order thành công! Chuyển đến trang gọi món...");

        setTimeout(() => {
            window.location.href = "order.html";
        }, 800);

    } catch (error) {
        console.error("Lỗi confirmOrder:", error);
        alert("Có lỗi xảy ra:\n" + error.message);
    } finally {
        // Reset nút
        btn.disabled = false;
        btn.textContent = originalText;
    }
}