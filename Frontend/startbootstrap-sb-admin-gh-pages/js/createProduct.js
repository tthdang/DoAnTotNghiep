let recipeIndex = 0;
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
        },
        recipes: recipes

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

//load nguyen lieu
async function loadIngredients() {
    try {
        const response = await fetch("http://localhost:8081/beefchef/ingredients");
        const data = await response.json();

        window.allIngredients = data.result || [];

        // console.log(`Đã load ${window.allIngredients.length} nguyên liệu`);
        console.log(`Đã load thành công tất cả nguyên liệu`);
        addRecipeRow();

    } catch (error) {
        console.error("Lỗi load ingredients:", error);
        alert("Không thể tải danh sách nguyên liệu!");
    }
}

//xu ly khi an them nguyen lieu
function addRecipeRow() {
    if (!window.allIngredients) {
        alert("Chưa load xong danh sách nguyên liệu!");
        return;
    }

    recipeIndex++;
    const container = document.getElementById("recipeContainer");

    const row = document.createElement("div");
    row.className = "recipe-row mb-2 d-flex align-items-center gap-2";

    let optionsHTML = `<option value="">Chọn nguyên liệu</option>`;
    window.allIngredients.forEach(ing => {
        optionsHTML += `<option value="${ing.ingredientId}" data-unit="${ing.unit}">${ing.ingredientName} (${ing.unit})</option>`;
    });

    row.innerHTML = `
        <select class="ingredient-select form-select" style="width: 45%;" required>
            ${optionsHTML}
        </select>
        <input type="number" class="quantity-input form-control" style="width: 120px;" 
               placeholder="Số lượng" step="0.1" min="0.1" required>
        <input type="text" class="unit-input form-control" style="width: 80px;" 
               value="g" readonly>
        <button type="button" class="btn btn-danger btn-sm" onclick="this.parentElement.remove()">Xóa</button>
    `;

    const select = row.querySelector(".ingredient-select");
    const unitInput = row.querySelector(".unit-input");

    select.addEventListener("change", function () {
        const selectedOption = this.options[this.selectedIndex];
        if (selectedOption && selectedOption.value) {
            unitInput.value = selectedOption.getAttribute("data-unit");
        }
    });

    container.appendChild(row);
}

//lay nguyen lieu tu form luu vao 
function getRecipesFromForm() {
    const recipes = [];
    document.querySelectorAll(".recipe-row").forEach(row => {
        const ingredientId = parseInt(row.querySelector(".ingredient-select").value);
        const quantity = parseFloat(row.querySelector(".quantity-input").value);
        const unit = row.querySelector(".unit-input").value.trim();

        if (ingredientId && !isNaN(quantity) && quantity > 0) {
            recipes.push({
                ingredientId: ingredientId,
                quantityNeeded: quantity,
                unit: unit
            });
        }
    });
    return recipes;
}

document.addEventListener("DOMContentLoaded", function () {
    loadCategories();
    loadIngredients();
});