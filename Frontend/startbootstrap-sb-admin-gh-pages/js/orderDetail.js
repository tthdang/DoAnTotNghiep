
let currentOrder = null;

async function loadOrderDetail() {
    const urlParams = new URLSearchParams(window.location.search);
    const orderId = urlParams.get('orderId');

    if (!orderId) {
        alert("Không tìm thấy Order ID trên URL!");
        return;
    }

    try {
        const res = await fetch(`http://localhost:8081/beefchef/orders/${orderId}`);
        if (!res.ok) throw new Error("Lỗi khi lấy order");

        const data = await res.json();
        currentOrder = data.result;
        console.log(currentOrder)
        renderOrder(currentOrder);

    } catch (err) {
        console.error(err);
        alert("Không tải được thông tin order!");
    }
}

function renderOrder(order) {
    // Thông tin cơ bản
    document.getElementById('tableName').textContent = order.tableName || `Bàn ${order.tableId}`;
    document.getElementById('orderId').textContent = `#${order.orderId}`;
    document.getElementById('customerName').textContent = order.userName || "Khách vãng lai";
    document.getElementById('createdAt').textContent = formatDateTime(order.createdAt);



    // Trạng thái
    const statusEl = document.getElementById('statusBadge');
    statusEl.textContent = getStatusText(order.orderStatus);
    statusEl.className = `status-badge ${getStatusClass(order.orderStatus)}`;

    // Tổng tiền
    document.getElementById('totalAmount').textContent =
        Number(order.orderTotal || 0).toLocaleString('vi-VN') + ' VNĐ';

    // Danh sách món
    renderItems(order.items || []);
}

function renderItems(items) {
    const tbody = document.getElementById('itemsBody');
    tbody.innerHTML = '';

    if (items.length === 0) {
        tbody.innerHTML = `<tr><td colspan="4" class="text-center py-4">Chưa có món nào được gọi</td></tr>`;
        return;
    }

    items.forEach(item => {
        const price = Number(item.orderItemPrice || 0);
        const qty = item.orderItemQuantity || 1;
        const total = price * qty;
        

        const statusText = getStatusOrderItemText(item.orderItemStatus);
        const statusClass = getStatusOrderItemClass(item.orderItemStatus);

        const tr = document.createElement('tr');
        tr.innerHTML = `
                    <td>${item.productName || 'Món không tên'}</td>
                    <td class="text-center">${qty}</td>
                    <td class="text-end">${price.toLocaleString('vi-VN')} đ</td>
                   <td class="text-center">
                        <span class="status-order-item ${statusClass}">
                            ${statusText}
                        </span>
                    </td>
                    <td class="text-end fw-bold">${total.toLocaleString('vi-VN')} đ</td>
                `;
        tbody.appendChild(tr);
    });
}

function getStatusText(status) {
    switch (status) {
        case 'ORDERING':
            return 'Đang gọi món';
        case 'COOKING':
            return 'Đang đợi ra món';
        case 'SERVED':
            return 'Đã ra tất cả món';
        case 'PAID':
            return 'Đã thanh toán';

        default: return status;
    }
}

function getStatusClass(status) {
    switch (status) {
        case 'ORDERING':
            return 'bg-warning text-dark';
        case 'COOKING':
            return 'bg-primary text-white';
        case 'SERVED':
            return 'bg-info text-white';
        case 'PAID':
            return 'bg-success text-white';
        default:
            return 'bg-secondary text-white';
    }
}

function getStatusOrderItemText(status) {
    switch (status) {
        case 'PENDING':
            return 'Đang đợi bếp nhận';
        case 'COOKING':
            return 'Đang chế biến';
        case 'READY':
            return 'Đã sẵn sàng ra món';
        case 'SERVED':
            return 'Đã ra món';

        default: return status;
    }
}

function getStatusOrderItemClass(status) {
    switch (status) {
        case 'PENDING':
            return 'text-warning fw-bold';
        case 'COOKING':
            return 'text-primary fw-bold';
        case 'READY':
            return 'text-info fw-bold';
        case 'SERVED':
            return 'text-success fw-bold';
        default:
            return 'text-secondary fw-bold';
    }
}

function formatDateTime(dateStr) {
    if (!dateStr) return 'N/A';
    const date = new Date(dateStr);
    return date.toLocaleString('vi-VN', {
        year: 'numeric', month: '2-digit', day: '2-digit',
        hour: '2-digit', minute: '2-digit'
    });
}

// Nút thanh toán
document.addEventListener('click', async function (e) {
    if (e.target && e.target.id === 'btnMarkPaid' && currentOrder) {
        if (confirm("Xác nhận order này đã được thanh toán?")) {
            try {
                const res = await fetch(`http://localhost:8081/beefchef/orders/${currentOrder.orderId}/paid`, {
                    method: 'PUT',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ orderStatus: 'PAID' })
                });

                if (res.ok) {
                    alert("Cập nhật thanh toán thành công!");
                    loadOrderDetail();
                
                } else {
                    alert("Cập nhật thanh toán thất bại!");
                }
            } catch (err) {
                alert("Lỗi kết nối!");
            }
        }
    }
});

window.onload = loadOrderDetail;