document.addEventListener("DOMContentLoaded", function () {
    const role = localStorage.getItem("role");

    if (role !== "ADMIN") {
        alert("Bạn không có quyền!");
        window.location.href = "login.html";
        return;
    }

    loadProducts();
});

//load product
let dataTableInstance = null;

async function loadProducts() {
    const tableBody = document.getElementById("productTableBody");
    
    //
    if (!tableBody) {
        console.warn("Không tìm thấy #productTableBody, bỏ qua loadProducts");
        return;
    }

    try {
        const response = await fetch("http://localhost:8081/beefchef/products");
        if (!response.ok) throw new Error("Lỗi khi lấy dữ liệu");
        
        const data = await response.json();
        const list = data.result;

        tableBody.innerHTML = "";

        list.forEach(product => {
            const row = `
                <tr>
                    <td>${product.productId}</td>
                    <td>
                        <img src="${product.productImage}" 
                             width="60" height="60" 
                             style="object-fit: cover; border-radius: 8px;">
                    </td>
                    <td>${product.productName}</td>
                    <td>${product.productDescription || ''}</td>
                    <td>${product.categoryName || ''}</td>
                    <td>${formatPrice(product.productPrice)}</td>
                    <td>${product.productStock}</td>
                    <td>${product.productSold}</td>
                    <td>
                        <span class="badge ${product.productStatus === 'AVAILABLE' ? 'bg-success' : 'bg-danger'}">
                            ${getStatusText(product.productStatus)}
                        </span>
                    </td>
                    <td>
                        <button class="btn btn-warning btn-sm" onclick="editProduct(${product.productId})">Sửa</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteProduct(${product.productId})">Xoá</button>
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
        console.error("Lỗi loadProducts:", error);
    }
}

//Format giá tiền
function formatPrice(price) {
    return price.toLocaleString("vi-VN") + " VNĐ";
}


// ===== Xoá sản phẩm =====
async function deleteProduct(id) {
    if (!confirm("Bạn có chắc muốn xoá không?")) return;

    try {
        const response = await fetch(`http://localhost:8081/beefchef/products/${id}`, {
            method: "DELETE",
            // headers: {
            //     "Authorization": "Bearer " + localStorage.getItem("token")
            // }
        });

        if (response.ok) {
            alert("Xoá món ăn thành công!");
            loadProducts();        // Dùng await để chắc chắn
        } else {
            alert("Xoá món ăn thất bại!");
        }

    } catch (error) {
        console.error("Lỗi xoá:", error);
        alert("Có lỗi khi xoá món ăn")
    }
}

function getStatusText(status) {
    switch (status) {
        case "AVAILABLE": return "Còn hàng";
        case "OUT_OF_STOCK": return "Hết hàng";
        default: return status;
    }
}

// Sửa sản phẩm 
function editProduct(id) {
    window.location.href = `updateProduct.html?id=${id}`;
}   

