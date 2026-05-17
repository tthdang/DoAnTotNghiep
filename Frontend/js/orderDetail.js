
let currentOrder = null;

function showAlert2(options) {
    if (typeof Swal !== 'undefined') {
        Swal.fire({
            confirmButtonText: 'OK',
            ...options
        });
        return;
    }

    alert(options.text || options.title || '');
}

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
        currentOrder = data.result || data;
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
    document.getElementById('userRank').textContent = order.userRank || 'Không có hạng';
    document.getElementById('createdAt').textContent = formatDateTime(order.createdAt);

    // Trạng thái
    const statusEl = document.getElementById('statusBadge');
    statusEl.textContent = getStatusText(order.orderStatus);
    statusEl.className = `status-badge ${getStatusClass(order.orderStatus)}`;

    // Hiển thị tiền
    const total = Number(order.orderTotal || 0);
    const discountAmount = Number(order.discountAmount || 0);
    const finalAmount = Number(order.finalAmount || total);
    const userRankDiscount = Number(order.userRankDiscount || 0);

    // Tổng tiền
    document.getElementById('totalAmount').textContent =
        total.toLocaleString('vi-VN') + ' VNĐ';

    // Giảm giá
    const discountEl = document.getElementById('discountAmount');
    if (discountAmount > 0) {
        discountEl.textContent = `- ${discountAmount.toLocaleString('vi-VN')} VNĐ`;
        discountEl.className = "text-success";
    } else {
        discountEl.textContent = "0 VNĐ";
        discountEl.className = "text-muted";
    }

    const discountRank = document.getElementById('userRankDiscount');
    if (userRankDiscount > 0) {
        discountRank.textContent = `(- ${userRankDiscount.toLocaleString('vi-VN')} VNĐ)`;
        discountRank.className = "text-success";
    } else {
        discountRank.textContent = "0 VNĐ";
        discountRank.className = "text-muted";
    }

    // Thành tiền
    document.getElementById('finalAmount').textContent =
        finalAmount.toLocaleString('vi-VN') + ' VNĐ';

    // Danh sách món
    renderItems(order.items || []);

    //xử lý khi chưa thanh toán
    const invoiceBtn = document.getElementById('btnDownloadInvoice');
    if (invoiceBtn) {
        invoiceBtn.style.display = (order.orderStatus === 'PAID') ? 'inline-block' : 'none';
    }
}

//hiển thị ds item
function renderItems(items) {
    const tbody = document.getElementById('itemsBody');
    tbody.innerHTML = '';

    if (items.length === 0) {
        tbody.innerHTML = `<tr><td colspan="5" class="text-center py-4">Chưa có món nào được gọi</td></tr>`;
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

// áp dụng khuyến mãi
window.addEventListener('DOMContentLoaded', () => {
    document.querySelector('.promo-btn').addEventListener('click', async () => {
        const code = document.getElementById('promo').value.trim();
        const promoInput = document.getElementById('promo');

        if (!code) {
            alert("Vui lòng nhập mã khuyến mãi!");
            return;
        }

        if (!currentOrder || !currentOrder.orderId) {
            alert("Không tìm thấy thông tin đơn hàng!");
            return;
        }

        try {
            const res = await fetch(`http://localhost:8081/beefchef/orders/${currentOrder.orderId}/applyPromotion?code=${encodeURIComponent(code)}`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' }
            });

            const data = await res.json();

            if (res.ok) {
                alert("Áp dụng mã khuyến mãi thành công!");
                promoInput.value = '';
                loadOrderDetail();
            } else {
                alert(data.message || "Áp dụng mã thất bại!");
            }
        } catch (error) {
            console.error(error);
            alert("Lỗi kết nối!");
        }
    });
})



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

                const data = await res.json();
                alert(data.message);

                if(res.ok){
                    loadOrderDetail();
                }

                // if (res.ok) {
                //     alert("Cập nhật thanh toán thành công!");
                //     loadOrderDetail();

                // } else {
                //     alert("Cập nhật thanh toán thất bại!");
                // }
            } catch (err) {
                alert("Lỗi kết nối!");
            }
        }
    }
});

// nút xuất hoá đơn
document.addEventListener('click', async function (e) {
    if (e.target && e.target.id === 'btnDownloadInvoice' && currentOrder) {

        // Chỉ cho xuất hoá đơn khi đã thanh toán
        if (currentOrder.orderStatus !== 'PAID') {
            showAlert2({
                icon: 'warning',
                title: 'Chưa thể xuất hoá đơn',
                text: 'Chỉ có thể xuất hoá đơn khi đơn hàng đã thanh toán!'
            });
            return;
        }

        const orderId = currentOrder.orderId;
        const btn = document.getElementById('btnDownloadInvoice');
        const originalText = btn.innerHTML;

        try {
            // Hiệu ứng loading
            btn.innerHTML = 'Đang tạo PDF...';
            btn.disabled = true;

            const response = await fetch(`http://localhost:8081/beefchef/invoices/${orderId}`, {
                method: 'GET',
                headers: {
                    // 'Authorization': 'Bearer ' + token
                }
            });

            if (response.status === 404) {
                showAlert2({
                    icon: 'error',
                    title: 'Không tìm thấy đơn hàng',
                    text: 'Vui lòng kiểm tra lại thông tin đơn hàng.'
                });
                return;
            }

            if (!response.ok) {
                const errorText = await response.text();
                showAlert2({
                    icon: 'error',
                    title: 'Lỗi khi tạo hoá đơn',
                    text: errorText || 'Vui lòng thử lại sau.'
                });
                return;
            }

            showAlert2({
                icon: 'success',
                title: 'Xuất hoá đơn thành công!',
                text: 'Hoá đơn PDF đã được tạo thành công.'
            });

        } catch (error) {
            console.error("Lỗi tải hoá đơn:", error);
            showAlert2({
                icon: 'error',
                title: 'Không thể tải hoá đơn',
                text: 'Có lỗi xảy ra khi tải hoá đơn. Vui lòng thử lại!'
            });
        }
        finally {
            // Khôi phục nút
            btn.innerHTML = originalText;
            btn.disabled = false;
        }
    }
});

window.onload = loadOrderDetail;
