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
            option.value = table.tableId;   
            option.textContent = `${table.tableName} - Còn trống`;
            select.appendChild(option);
        });
    } catch (error) {
        console.error("Lỗi load tables:", error);
        select.innerHTML = '<option value="">Lỗi tải danh sách bàn. Vui lòng thử lại.</option>';
    }
}

// xác nhận số điện thoại cùng bàn
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
        alert("Vui lòng chọn bànnhập đầy đủ số điện thoại!");
        return;
    }

    const btn = document.querySelector(".btn-order");
    const originalText = btn.textContent;
    btn.disabled = true;
    btn.textContent = "Đang xử lý...";

    try {
        const response = await fetch('http://localhost:8081/beefchef/orders', {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                tableId: parseInt(tableId),
                userPhone: phone,
                items: []
            })
        });

        if (!response.ok) {
            const err = await response.text();
            throw new Error(err || `Lỗi ${response.status}`);
        }

        const data = await response.json();

        // Lưu toàn bộ thông tin order vào localStorage
        localStorage.setItem("currentOrder", JSON.stringify({
            orderId: data.result.orderId,
            tableId: data.result.tableId,
            tableName: data.result.tableName,
            userId: data.result.userId,
            userName: data.result.userName,
            orderStatus: data.result.orderStatus,
            orderTotal: data.result.orderTotal,
            shift: data.result.shift,
            createdAt: data.result.createdAt,
            items: data.result.items || []
        }));

        console.log("Order đã lưu:", data.result);

        alert(`Tạo order thành công!\nBàn: ${data.result.tableName}\nOrder ID: #${data.result.orderId}`);
        
        // Chuyển trang sau 800ms
        setTimeout(() => {
            window.location.href = "order.html";
        }, 800);

    } catch (error) {
        console.error("Lỗi:", error);
        alert("Có lỗi xảy ra: " + error.message);
        window.location.href = "404.html"
    } finally {
        btn.disabled = false;
        btn.textContent = originalText;
    }
}