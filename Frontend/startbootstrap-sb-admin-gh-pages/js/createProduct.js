document.getElementById("createForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const product = {
        productName: document.getElementById("name").value,
        productDescription: document.getElementById("description").value,
        productPrice: parseFloat(document.getElementById("price").value),
        productStock: parseInt(document.getElementById("stock").value),
        productImage: document.getElementById("image").value,
        productStatus: "AVAILABLE",
        category: {
            categoryId: parseInt(document.getElementById("categoryId").value)
        }

    };

    console.log(product);

    try {
        
        const response = await fetch("http://localhost:8081/beefchef/products", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(product)
        });

        if (!response.ok) throw new Error();

        alert("Tạo thành công!");

        // quay lại trang list
        window.location.href = "productAdmin.html";

    } catch (error) {
        alert("Tạo thất bại!");
        console.error(error);
    }
});

// Load Category
document.addEventListener("DOMContentLoaded", function () {
    loadCategories();
});

async function loadCategories() {
    try {
        const response = await fetch("http://localhost:8081/beefchef/categories");
        const data = await response.json();

        const select = document.getElementById("categoryId");

        data.forEach(cat => {
            const option = document.createElement("option");
            option.value = cat.categoryId;
            option.textContent = cat.categoryName;
            select.appendChild(option);
        });

    } catch (error) {
        console.error("Lỗi load category:", error);
    }
}