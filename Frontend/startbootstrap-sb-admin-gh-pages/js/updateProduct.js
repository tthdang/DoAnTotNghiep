document.addEventListener("DOMContentLoaded", async function () {
    const productId = getProductId();

    if (!productId) {
        alert("Không tìm thấy ID sản phẩm!");
        return;
    }

    try {
        // Gọi song song product + categories
        const [productRes, categoryRes] = await Promise.all([
            fetch(`http://localhost:8081/beefchef/products/${productId}`),
            fetch("http://localhost:8081/beefchef/categories")
        ]);

        if (!productRes.ok) throw new Error("Không lấy được thông tin sản phẩm");

        const product = await productRes.json();
        const categories = await categoryRes.json();

        // Lấy danh mục
        const categorySelect = document.getElementById("categoryId");
        categorySelect.innerHTML = '<option value="">-- Chọn danh mục --</option>';

        categories.forEach(cat => {
            const option = document.createElement("option");
            option.value = cat.categoryId;
            option.textContent = cat.categoryName;

            if (cat.categoryId === product.category?.categoryId) {
                option.selected = true;
            }
            categorySelect.appendChild(option);
        });

        // điền dữ liệu vào form
        document.getElementById("name").value = product.productName || "";
        document.getElementById("description").value = product.productDescription || "";
        document.getElementById("price").value = product.productPrice || "";
        document.getElementById("stock").value = product.productStock || "";
        document.getElementById("image").value = product.productImage || "";

        // set trạng thái
        const statusSelect = document.getElementById("status");
        if (statusSelect) {
            statusSelect.value = product.productStatus || "AVAILABLE";
        }

    } catch (err) {
        console.error("Lỗi load dữ liệu:", err);
        alert("Không load được dữ liệu sản phẩm!");
    }
});

// Lấy id
function getProductId() {
    return new URLSearchParams(window.location.search).get("id");
}

// Xử lý chức năng Edit
const updateForm = document.getElementById("updateForm");

if (updateForm) {
    updateForm.addEventListener("submit", async function (e) {
        e.preventDefault();

        const productId = getProductId();

        const productData = {
            productName: document.getElementById("name").value.trim(),
            productDescription: document.getElementById("description").value.trim(),
            productPrice: parseFloat(document.getElementById("price").value),
            productStock: parseInt(document.getElementById("stock").value),
            productImage: document.getElementById("image").value.trim(),
            productStatus: document.getElementById("status").value,   
            category: {
                categoryId: parseInt(document.getElementById("categoryId").value)
            }
        };

        console.log("Dữ liệu gửi đi:", productData);

        try {
            const response = await fetch(`http://localhost:8081/beefchef/products/${productId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(productData)
            });

            if (response.ok) {
                alert("Cập nhật sản phẩm thành công!");
                window.location.href = "productAdmin.html";
            } else {
                const errorText = await response.text();
                throw new Error(errorText || "Lỗi server");
            }

        } catch (error) {
            console.error("Lỗi update:", error);
            alert("Cập nhật thất bại! Vui lòng thử lại.");
        }
    });
}