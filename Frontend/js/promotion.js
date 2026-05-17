document.addEventListener("DOMContentLoaded", function () {
    const role = localStorage.getItem("role");

    if (role !== "ADMIN") {
        alert("Bạn không có quyền!");
        window.location.href = "login.html";
        return;
    }

    loadPromotion();
});

//load promotion
let dataTableInstance = null;

async function loadPromotion() {
    const tableBody = document.getElementById("promotionTableBody");

    //
    if (!tableBody) {
        console.warn("Không tìm thấy #promotionTableBody, bỏ qua loadPromotion");
        return;
    }

    try {
        const response = await fetch("http://localhost:8081/beefchef/promotion");
        if (!response.ok) throw new Error("Lỗi khi lấy dữ liệu");

        const data = await response.json();

        const list = data.result;

        tableBody.innerHTML = "";

        list.forEach(promotion => {
            const row = `
                <tr>
                      <td>${promotion.promotionId}</td>
                    <td>${promotion.code}</td>
                    <td>
                        <span class="badge ${promotion.promotionType === 'ORDER' ? 'bg-success' : 'bg-warning text-dark'}">
                            ${getStatusText(promotion.promotionType)}
                        </span>
                    </td>
                    <td>
                        <span class="badge ${promotion.discountType === 'FIXED' ? 'bg-success' : 'bg-warning text-dark'}">
                            ${getStatusText(promotion.discountType)}
                        </span>
                    </td>
                    <td>${promotion.discountType === 'PERCENT' ? promotion.discountValue + '%' : formatPrice(promotion.discountValue)}</td>
                    
                    <td>${formatPrice(promotion.maxDiscountValue)}</td>
                    <td>${formatPrice(promotion.minOrderValue)}</td>
                    <td>${formatDateTime(promotion.startDate)}</td>
                    <td>${formatDateTime(promotion.endDate)}</td>
                    <td>${promotion.usageLimit}</td>
                    <td>${promotion.usedCount}</td>
                    <td>
                        <span class="badge ${promotion.status === 'AVAILABLE' ? 'bg-success' : 'bg-warning text-dark'}">
                            ${getStatusText(promotion.status)}
                        </span>
                    </td>
  
                    <td>
                        <button class="btn btn-warning btn-sm" onclick="editPromotion(${promotion.promotionId})">Sửa</button>
                        <button class="btn btn-danger btn-sm" onclick="deletePromotion(${promotion.promotionId})">Xoá</button>
                    </td>
                </tr>
            `;
            tableBody.innerHTML += row;
        });

        // Xử lý DataTable
        if (dataTableInstance) {
            dataTableInstance.destroy();
        }

        dataTableInstance = new simpleDatatables.DataTable("#datatablesPromotion", {
            searchable: true,
            sortable: true,
            perPage: 10,
            perPageSelect: [5, 10, 20, 50],
            labels: {
                placeholder: "Tìm kiếm...",
                perPage: " mã khuyến mãi mỗi trang",
                noRows: "Không có dữ liệu",
                info: "Hiển thị {start} đến {end} của {rows} mã khuyến mãi"
            }
        });

    } catch (error) {
        console.error("Lỗi loadPromotion:", error);
    }
}

function formatDateTime(dateStr) {
    if (!dateStr) return 'N/A';
    const date = new Date(dateStr);
    return date.toLocaleString('vi-VN', {
        year: 'numeric', month: '2-digit', day: '2-digit'
    });
}

// ===== Xoá sản phẩm =====
async function deletePromotion(id) {
    if (!confirm("Bạn có chắc muốn xoá không?")) return;

    try {
        const response = await fetch(`http://localhost:8081/beefchef/promotion/${id}`, {
            method: "DELETE",
            // headers: {
            //     "Authorization": "Bearer " + localStorage.getItem("token")
            // }
        });

        if (response.ok) {
            alert("Xoá mã khuyến mãi thành công!");
            window.location.href = "promotion.html"
            loadPromotion();
        } else {
            alert("Xoá mã khuyến mãi thất bại!");
        }

    } catch (error) {
        console.error("Lỗi xoá:", error);
        alert("Có lỗi khi xoá mã khuyến mãi")
    }
}

// Sửa sản phẩm 
function editPromotion(id) {
    window.location.href = `updatePromotion.html?id=${id}`;
}

//format trang thai
function getStatusText(status) {
    switch (status) {
        case "ORDER": return "Hoá đơn";
        case "FIXED": return "Tiền";
        case "PERCENT": return "Phần trăm";
        case "AVAILABLE": return "Còn mã";
        case "OUT_OF_STOCK": return "Hết mã";
        default: return status;
    }
}

//format gia
function formatPrice(price) {
    return price.toLocaleString("vi-VN") + " VNĐ";
}

loadPromotion();

