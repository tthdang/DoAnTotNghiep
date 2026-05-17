document.addEventListener("DOMContentLoaded", function () {
    const role = localStorage.getItem("role");

    if (role !== "ADMIN") {
        alert("Bạn không có quyền!");
        window.location.href = "login.html";
        return;
    }

    loadIngredient();
});

//load ingredient
let dataTableInstance = null;

async function loadIngredient() {
    const tableBody = document.getElementById("ingredientTableBody");
    
    //
    if (!tableBody) {
        console.warn("Không tìm thấy #ingredientTableBody, bỏ qua loadIngredient");
        return;
    }

    try {
        const response = await fetch("http://localhost:8081/beefchef/ingredients");
        if (!response.ok) throw new Error("Lỗi khi lấy dữ liệu");
        
        const data = await response.json();

        const list = data.result;

        tableBody.innerHTML = "";

        list.forEach(ingredient => {
            const row = `
                <tr>
                    <td>${ingredient.ingredientId}</td>
                    <td>${ingredient.ingredientName}</td>
                    <td>${ingredient.total}</td>
                    <td>${ingredient.unit}</td>
  
                    <td>
                        <button class="btn btn-warning btn-sm" onclick="editIngredient(${ingredient.ingredientId})">Sửa</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteIngredient(${ingredient.ingredientId})">Xoá</button>
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
            perPageSelect: [5, 10, 20, 50],
            labels: {
                placeholder: "Tìm kiếm tên nguyên liệu...",
                perPage: " nguyên liệu mỗi trang",
                noRows: "Không có dữ liệu",
                info: "Hiển thị {start} đến {end} của {rows} nguyên liệu"
            }
        });

    } catch (error) {
        console.error("Lỗi loadIngredient:", error);
    }
}


// ===== Xoá sản phẩm =====
async function deleteIngredient(id) {
    if (!confirm("Bạn có chắc muốn xoá không?")) return;

    try {
        const response = await fetch(`http://localhost:8081/beefchef/ingredients/${id}`, {
            method: "DELETE",
            // headers: {
            //     "Authorization": "Bearer " + localStorage.getItem("token")
            // }
        });

        if (response.ok) {
            alert("Xoá nguyên liệu thành công!");
            window.location.href = "Ingredient.html"
            loadIngredient();       
        } else {
            alert("Xoá nguyên liệu thất bại!");
        }

    } catch (error) {
        console.error("Lỗi xoá:", error);
        alert("Có lỗi khi xoá món ăn")
    }
}

// Sửa sản phẩm 
function editIngredient(id) {
    window.location.href = `updateProduct.html?id=${id}`;
}   

// setInterval(loadIngredient, 3000); //mỗi 3s
loadIngredient();

