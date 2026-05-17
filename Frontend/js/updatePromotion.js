document.addEventListener("DOMContentLoaded", async function () {
    const promotionId = getPromotionId();

    if (!promotionId) {
        alert("Không tìm thấy ID mã khuyến mãi!");
        return;
    }

    try {
        const promotionRes = await fetch(`http://localhost:8081/beefchef/promotion/${promotionId}`);

        if (!promotionRes.ok) throw new Error("Không lấy được thông tin mã khuyến mãi");

        const data = await promotionRes.json();
        const promotion = data.result;

        console.log("promotion:", promotion);

        // điền dữ liệu vào form
        document.getElementById("code").value = promotion.code || "";
        document.getElementById("promotionType").value = promotion.promotionType || "";
        document.getElementById("discountType").value = promotion.discountType || "";
        document.getElementById("discountValue").value = promotion.discountValue || "";
        document.getElementById("minOrderValue").value = promotion.minOrderValue || "";
        document.getElementById("maxDiscountValue").value = promotion.maxDiscountValue || "";
        document.getElementById("startDate").value = promotion.startDate || "";
        document.getElementById("endDate").value = promotion.endDate || "";

        const statusSelect = document.getElementById("status");
        if (statusSelect) {
            statusSelect.value = promotion.status || "AVAILABLE";
        }

        document.getElementById("usageLimit").value = promotion.usageLimit || "";
        document.getElementById("usedCount").value = promotion.usedCount || "";


    } catch (err) {
        console.error("Lỗi load dữ liệu:", err);
        alert("Không load được dữ liệu mã khuyến mãi!");
    }
});

// Lấy id
function getPromotionId() {
    return new URLSearchParams(window.location.search).get("id");
}

// Xử lý chức năng Edit
const updateForm = document.getElementById("updateForm");

if (updateForm) {
    updateForm.addEventListener("submit", async function (e) {
        e.preventDefault();

        const promotionId = getPromotionId();

        const promotionData = {
            code: document.getElementById("code").value.trim(),
            promotionType: document.getElementById("promotionType").value,
            discountValue: document.getElementById("discountValue").value,
            discountType: document.getElementById("discountType").value,
            minOrderValue: document.getElementById("minOrderValue").value,
            maxDiscountValue: document.getElementById("maxDiscountValue").value,
            startDate: document.getElementById("startDate").value,
            endDate: document.getElementById("endDate").value,
            status: document.getElementById("status").value,
            usageLimit: document.getElementById("usageLimit").value,
            usedCount: document.getElementById("usedCount").value || 0
        };

        console.log("Dữ liệu gửi đi:", promotionData);

        try {
            const response = await fetch(`http://localhost:8081/beefchef/promotion/${promotionId}`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(promotionData)
            });

            if (response.ok) {
                alert("Cập nhật mã khuyến mãi thành công!");
                window.location.href = "promotion.html";
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