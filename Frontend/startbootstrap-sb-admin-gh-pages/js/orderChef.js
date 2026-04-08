const API_URL = "http://localhost:8081/beefchef/orderItem";

//lấy dữ liệu từ API
async function fetchOrders() {
    const [pendingRes, cookingRes, readyRes, servedRes] = await Promise.all([
        fetch(API_URL + "?status=PENDING"),
        fetch(API_URL + "?status=COOKING"),
        fetch(API_URL + "?status=READY")
    ]);

    const pending = (await pendingRes.json()).result;
    const cooking = (await cookingRes.json()).result;
    const ready = (await readyRes.json()).result;


    renderColumn("pending-list", pending);
    renderColumn("cooking-list", cooking);
    renderColumn("ready-list", ready);

}

//Hiển thị ra giao diện
function renderColumn(elementId, orders) {
    const container = document.getElementById(elementId);
    container.innerHTML = "";

    orders.forEach(item => {
        const div = document.createElement("div");
        div.className = "order-card";

        let button = "";

        if (item.orderItemStatus === "PENDING") {
            button = `<button onclick="updateStatus(${item.orderItemId}, 'COOKING')">Nhận món</button>`;
            alert("")
        } else if (item.orderItemStatus === "COOKING") {
            button = `<button onclick="updateStatus(${item.orderItemId}, 'READY')">Xong</button>`;
        } else if (item.orderItemStatus === "READY") {
            button = `<button onclick="updateStatus(${item.orderItemId}, 'SERVED')">Phục vụ</button>`;
        }

        div.innerHTML = `
            <h3>${item.tableName}</h3>
            <h4>Món ăn: ${item.productName}</h4>
            <p>Số lượng: ${item.orderItemQuantity}</p>
            ${button}
        `;

        container.appendChild(div);
    });
}

async function updateStatus(orderItemId, status) {
    await fetch(`${API_URL}/${orderItemId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ 
            orderItemStatus: status 
        })
    });

    fetchOrders(); // reload lại UI
}

//set thời gian tự động refresh trang
setInterval(fetchOrders, 3000); //mỗi 3s
fetchOrders();