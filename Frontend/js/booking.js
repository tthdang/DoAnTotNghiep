let selectedTableId = null;
let debounceTimer = null;

function loadTables() {
    fetch("http://localhost:8081/beefchef/tables")
        .then(res => res.json())
        .then(data => {
            const tables = data.result;
            const container = document.getElementById("tableSelector");

            container.innerHTML = "";

            tables.forEach(t => {
                const btn = document.createElement("button");
                btn.type = "button";
                btn.classList.add("table-btn");

                btn.dataset.id = t.tableId;

                btn.innerHTML = `
                    <span class="table-name">${t.tableName.replace("Bàn số ", "Bàn ")}</span>
                    <span class="table-capacity">${t.tableCapacity} chỗ</span>
                `;

                btn.onclick = () => {
                    if (btn.classList.contains("unavailable")) return;

                    selectedTableId = t.tableId;

                    document.querySelectorAll("#tableSelector .table-btn")
                        .forEach(b => b.classList.remove("selected"));

                    btn.classList.add("selected");
                };

                container.appendChild(btn);
            });
        })
        .catch(() => {
            alert("Không tải được danh sách bàn!");
        });
}

function checkAvailabilityDebounced() {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(checkAvailability, 300);
}

async function checkAvailability() {
    const date = document.getElementById("bk-date").value;
    const time = document.getElementById("bk-time").value;

    if (!date || !time) return;

    const dateTime = `${date}T${time}:00`;

    try {
        const res = await fetch(
            `http://localhost:8081/beefchef/reservation/available?dateTime=${dateTime}`
        );

        const unavailableIds = await res.json();

        updateTableUI(unavailableIds);

    } catch (err) {
        console.error("Lỗi check bàn:", err);
    }
}


function updateTableUI(unavailableIds) {
    document.querySelectorAll("#tableSelector .table-btn").forEach(btn => {
        const id = parseInt(btn.dataset.id);

        btn.classList.remove("unavailable");

        if (unavailableIds.includes(id)) {
            btn.classList.add("unavailable");
            btn.disabled = true;
        } else {
            btn.disabled = false;
        }
    });

    // nếu bàn đang chọn bị trùng → reset
    if (selectedTableId && unavailableIds.includes(selectedTableId)) {
        selectedTableId = null;

        document.querySelectorAll("#tableSelector .table-btn")
            .forEach(b => b.classList.remove("selected"));

        alert("Bàn bạn chọn đã có người đặt, vui lòng chọn bàn khác!");
    }
}

async function submitBooking(event) {
    if (event) event.preventDefault();

    try {
        const name = document.getElementById("bk-name").value.trim();
        const phone = document.getElementById("bk-phone").value.trim();
        const date = document.getElementById("bk-date").value;
        const time = document.getElementById("bk-time").value;
        const guests = document.getElementById("bk-guests").value;
        const note = document.getElementById("bk-note").value;

        if (!name || !phone || !date || !time || !guests) {
            alert("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        if (!selectedTableId) {
            alert("Vui lòng chọn bàn!");
            return;
        }

        const btn = document.querySelector(
            `.table-btn[data-id="${selectedTableId}"]`
        );

        if (btn && btn.classList.contains("unavailable")) {
            alert("Bàn này đã có người đặt, vui lòng chọn bàn khác!");
            return;
        }

        const reservationTime = `${date}T${time}:00`;

        const data = {
            customerName: name,
            customerPhone: phone,
            date: reservationTime,
            numberOfPeople: parseInt(guests),
            note: note,
            tableId: selectedTableId
        };

        console.log("DATA GỬI:", data);

        const res = await fetch("http://localhost:8081/beefchef/reservation", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(data)
        });

        if (!res.ok) {
            const errText = await res.text();
            console.error("SERVER ERROR:", errText);
            throw new Error(errText);
        }

        await res.json();

        document.getElementById("bookingSuccess").style.display = "block";
        alert("Đặt bàn thành công!");

        // reset form
        document.getElementById("bookingForm").reset();
        selectedTableId = null;

        document.querySelectorAll("#tableSelector .table-btn")
            .forEach(b => b.classList.remove("selected"));

    } catch (err) {
        console.error("Lỗi:", err);
        alert(err.message || "Đặt bàn thất bại!");
    }
}


loadTables();

//check thay đổi ngày giờ
document.getElementById("bk-date").addEventListener("change", checkAvailabilityDebounced);
document.getElementById("bk-time").addEventListener("change", checkAvailabilityDebounced);