const API = "http://localhost:8081/beefchef/report/shiftByDate";

let chart = null;

// format tiền
function formatMoney(num) {
    return Number(num).toLocaleString('vi-VN') + "đ";
}

//lấy danh sách ca
async function loadShifts() {
    try {
        const res = await fetch("http://localhost:8081/beefchef/shift");
        const data = await res.json();

        let shifts = data.result;

        // sort theo giờ
        shifts = shifts.sort((a, b) =>
            a.startTime.localeCompare(b.startTime)
        );

        const select = document.getElementById("shiftSelect");

        select.innerHTML = shifts.map(s => `
            <option value="${s.shiftId}">
                ${s.shiftName}
            </option>
        `).join('');

        // chọn ca hiện tại
        const now = new Date().toTimeString().slice(0, 8); 

        const currentShift = shifts.find(s =>
            now >= s.startTime && now <= s.endTime
        );

        if (currentShift) {
            select.value = currentShift.shiftId;
        }

    } catch (err) {
        console.error("Lỗi load ca:", err);
    }
}

// load report
async function loadReport() {
    const shiftId = document.getElementById("shiftSelect").value;
    const date = document.getElementById("datePicker").value;

    if (!date) {
        alert("Vui lòng chọn ngày!");
        return;
    }

    try {
        const res = await fetch(`${API}?shiftId=${shiftId}&date=${date}`);
        const data = await res.json();
        const r = data.result;

        document.getElementById("totalRevenue").innerText =
            formatMoney(r.totalReport);

        document.getElementById("totalOrders").innerText =
            r.totalOrders;

        document.getElementById("bestSeller").innerText =
            r.bestSeller ? r.bestSeller.productName : "Không có";

        //table
        renderTable(r.listProductReportResponses);

    } catch (err) {
        console.error(err);
        alert("Lỗi tải dữ liệu!");
    }
}

// render table
function renderTable(products) {
    const table = document.getElementById("tableBody");

    if (!products || products.length === 0) {
        table.innerHTML = `
            <tr>
                <td colspan="4" style="text-align:center;">Không có dữ liệu</td>
            </tr>
        `;
        return;
    }

    table.innerHTML = products.map(p => `
        <tr>
            <td><img src="${p.productImage}" width="60"/></td>
            <td>${p.productName}</td>
            <td>${p.quantity}</td>
            <td>${formatMoney(p.totalAmount)}</td>
        </tr>
    `).join('');
}

function getTodayVN() {
    const now = new Date();
    const offset = now.getTimezoneOffset() * 60000;
    const vnTime = new Date(now.getTime() - offset);
    return vnTime.toISOString().split("T")[0];
}

// ===== INIT =====
document.addEventListener("DOMContentLoaded", async () => {

    // set ngày hôm nay
    const today = getTodayVN();
    document.getElementById("datePicker").value = today;

    //load ca 
    await loadShifts();

    // load report
    loadReport();

    
    document.getElementById("datePicker")
        .addEventListener("change", loadReport);

    document.getElementById("shiftSelect")
        .addEventListener("change", loadReport);
});