document.getElementById("createForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const ingredient = {
        ingredientName: document.getElementById("name").value,
        unit: document.getElementById("unit").value,
        // category: {
        //     categoryId: parseInt(document.getElementById("categoryId").value)
        // }

    };

    console.log(ingredient);

    try {
        
        const response = await fetch("http://localhost:8081/beefchef/ingredients", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(ingredient)
        });

        if (!response.ok) throw new Error();

        alert("Tạo thành công!");

        // quay lại trang list
        window.location.href = "Ingredient.html";

    } catch (error) {
        alert("Tạo thất bại!");
        console.error(error);
    }
});
``