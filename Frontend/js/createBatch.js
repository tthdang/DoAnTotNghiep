let recipeIndex = 0;
document.getElementById("createForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const batch = {
        ingredientId: parseInt(document.getElementById("ingredientId").value),
        quantityImported: parseFloat(document.getElementById("quantityImported").value),
        expiryDate: document.getElementById("expiryDate").value,
        batchPrice: parseFloat(document.getElementById("batchPrice").value)
    };

    console.log(batch);

    try {

        const response = await fetch("http://localhost:8081/beefchef/batch", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(batch)
        });

        if (!response.ok) throw new Error();

        alert("Nhập lô thành công!");

        // quay lại trang list
        window.location.href = "ingredientBatch.html";

    } catch (error) {
        alert("Tạo thất bại!");
        console.error(error);
    }
});

//load nguyen lieu
async function loadIngredients() {
    try {
        const response = await fetch("http://localhost:8081/beefchef/ingredients");
        const data = await response.json();

        const select = document.getElementById("ingredientId");

         data.result.forEach(item => {
            const option = document.createElement("option");
            option.value = item.ingredientId;
            option.textContent = `${item.ingredientName} (${item.unit})`;
            select.appendChild(option);
        });

    } catch (error) {
        console.error("Lỗi load ingredients:", error);
        alert("Không thể tải danh sách nguyên liệu!");
    }
}


document.addEventListener("DOMContentLoaded", function () {
    loadIngredients();
});