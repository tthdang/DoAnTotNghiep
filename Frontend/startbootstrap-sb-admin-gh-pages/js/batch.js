document.addEventListener("DOMContentLoaded", function () {
    const role = localStorage.getItem("role");

    if (role !== "ADMIN") {
        alert("Bạn không có quyền!");
        window.location.href = "login.html";
        return;
    }

    loadBatch();
});

//load batch
let dataTableInstance = null;

async function loadBatch() {
    const tableBody = document.getElementById("batchTableBody");

    //
    if (!tableBody) {
        console.warn("Không tìm thấy #batchTableBody, bỏ qua loadBatch");
        return;
    }

    try {
        const response = await fetch("http://localhost:8081/beefchef/batch");
        if (!response.ok) throw new Error("Lỗi khi lấy dữ liệu");

        const data = await response.json();
        const list = data.result || [];

        tableBody.innerHTML = "";

        list.forEach(batch => {
            const row = `
                <tr>
                    <td>${batch.batchId}</td>
                    <td>${batch.ingredientName}</td>
                    <td>${batch.quantityImported}</td>
                    <td>${batch.quantityRemaining}</td>
                    <td>${formatDateTime(batch.importDate)}</td>
                    <td>${formatDateTime(batch.expiryDate)}</td>
                    <td>${formatPrice(batch.batchPrice)}</td>
                    <td >
                        <span style="font-size: 14px" class="badge 
                            ${batch.status === 'AVAILABLE' ? 'bg-success' :
                            batch.status === 'NEAR_EXPIRY' ? 'bg-warning text-dark' :'bg-danger'}">
                            ${getStatusText(batch.status)}
                        </span>
                    </td>
                    
                </tr>
            `;
            tableBody.innerHTML += row;
        });

        // Xử lý DataTable
        if (dataTableInstance) {
            dataTableInstance.destroy();
        }

        dataTableInstance = new simpleDatatables.DataTable("#datatablesSimple", {
            searchable: true,
            sortable: true,
            perPage: 10,
            perPageSelect: [5, 10, 20, 50]
        });

    } catch (error) {
        console.error("Lỗi loadBatch:", error);
    }
}

//Format giá tiền
function formatPrice(price) {
    return price.toLocaleString("vi-VN") + " VNĐ";
}

function getStatusText(status) {
    switch (status) {
        case "AVAILABLE": return "Còn hạn";
        case "NEAR_EXPIRY": return "Sắp hết hạn";
        case "EXPIRED": return "Đã hết hạn";
        default: return status;
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

// // ===== Xoá sản phẩm =====
// async function deleteProduct(id) {
//     if (!confirm("Bạn có chắc muốn xoá không?")) return;

//     try {
//         const response = await fetch(`http://localhost:8081/beefchef/products/${id}`, {
//             method: "DELETE",
//             // headers: {
//             //     "Authorization": "Bearer " + localStorage.getItem("token")
//             // }
//         });

//         if (response.ok) {
//             alert("Xoá món ăn thành công!");
//             loadBatch();        // Dùng await để chắc chắn
//         } else {
//             alert("Xoá món ăn thất bại!");
//         }

//     } catch (error) {
//         console.error("Lỗi xoá:", error);
//         alert("Có lỗi khi xoá món ăn")
//     }
// }



// // Sửa sản phẩm
// function editProduct(id) {
//     window.location.href = `updateProduct.html?id=${id}`;
// }   

