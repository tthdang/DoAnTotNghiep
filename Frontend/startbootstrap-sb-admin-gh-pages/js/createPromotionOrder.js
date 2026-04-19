let recipeIndex = 0;
document.getElementById("createForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const promotion = {
        code: document.getElementById("code").value.trim(),
        promotionType: document.getElementById("promotionType").value,
        discountValue: document.getElementById("discountValue").value,
        discountType: document.getElementById("discountType").value,
        minOrderValue: document.getElementById("minOrderValue").value,
        maxDiscountValue: document.getElementById("maxDiscountValue").value,
        startDate: document.getElementById("startDate").value,
        endDate: document.getElementById("endDate").value,
        usageLimit: document.getElementById("usageLimit").value

    };

    console.log(promotion);

    if (!code) {
        alert("Vui lòng nhập mã khuyến mãi");
        return;
    }

    if (discountType === "PERCENT" && discountValue > 100) {
        alert("Phần trăm không được vượt quá 100%");
        return;
    }

    if (startDate && endDate && new Date(startDate) >= new Date(endDate)) {
        alert("Ngày kết thúc phải sau ngày bắt đầu");
        return;
    }

    try {

        const response = await fetch("http://localhost:8081/beefchef/promotion/order", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(promotion)
        });

        if (!response.ok) throw new Error();

        alert("Tạo thành công!");

        // quay lại trang list
        window.location.href = "promotion.html";

    } catch (error) {
        alert("Tạo thất bại!");
        console.error(error);
    }
});

const discountType = document.getElementById("discountType");
const discountUnit = document.getElementById("discountUnit");
const discountValue = document.getElementById("discountValue");

discountType.addEventListener("change", function () {
    if (this.value === "PERCENT") {
        discountUnit.innerText = "%";
        // discountValue.placeholder = "10";
        discountValue.max = 100; 
    } else {
        discountUnit.innerText = "VNĐ";
        // discountValue.placeholder = "10000";
        discountValue.removeAttribute("max"); 
    }
});

document.addEventListener("DOMContentLoaded", function () {
    
    
});