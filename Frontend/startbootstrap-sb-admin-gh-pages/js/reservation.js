document.addEventListener("DOMContentLoaded", function () {
    const role = localStorage.getItem("role");

    if (role !== "ADMIN") {
        alert("Bạn không có quyền!");
        window.location.href = "login.html";
        return;
    }

    loadBooking();
});

//load booking
let dataTableInstance = null;

async function loadBooking() {
    const tableBody = document.getElementById("bookingTableBody");

    //
    if (!tableBody) {
        console.warn("Không tìm thấy #bookingTableBody, bỏ qua loadBooking");
        return;
    }

    try {
        const response = await fetch("http://localhost:8081/beefchef/reservation");
        if (!response.ok) throw new Error("Lỗi khi lấy dữ liệu");

        const data = await response.json();

        const list = data.result;

        tableBody.innerHTML = "";

        list.forEach(booking => {

            const actionButtons =
                booking.status === "PENDING"
                    ? `
        <button class="btn btn-warning btn-sm" onclick="confirmBooking(${booking.reservationId})">Chấp nhận</button>
        <button class="btn btn-danger btn-sm" onclick="cancelBooking(${booking.reservationId})">Từ chối</button>
      `
                    : `<span class="text-muted">Đã xử lý</span>`;

            const row = `
                <tr>
                    <td>${booking.reservationId}</td>
                    <td>${booking.customerName}</td>
                    <td>${booking.customerPhone}</td>
                    <td>${booking.date}</td>
                    <td>${booking.numberOfPeople}</td>
                    <td>${booking.tableName}</td>
                    <td>${booking.note}</td>
                    <td>${getStatusText(booking.status)}</td>
  
                    <td>
                        ${actionButtons}
                    </td>
                </tr>
            `;
            tableBody.innerHTML += row;
        });

        // Xử lý DataTable
        if (dataTableInstance) {
            dataTableInstance.destroy();
        }

        dataTableInstance = new simpleDatatables.DataTable("#datatablesSimple", {
            searchable: true,
            sortable: true,
            perPage: 10,
            perPageSelect: [5, 10, 20, 50]
        });



    } catch (error) {
        console.error("Lỗi loadBooking:", error);
    }
}


// ===== Xác nhận đơn =====
async function confirmBooking(id) {
    if (!confirm("Bạn có chắc muốn chấp nhận không?")) return;

    try {
        const response = await fetch(`http://localhost:8081/beefchef/reservation/${id}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: 'CONFIRMED' })
        });

        if (response.ok) {
            alert("cập nhật đơn đặt bàn thành công!");
            window.location.href = "reservation.html"
            loadBooking();
        } else {
            alert("cập nhật đơn đặt bàn thất bại!");
        }

    } catch (error) {
        console.error("Lỗi cập nhật:", error);
        alert("Có lỗi khi cập nhật đơn đặt bàn!")
    }
}

// ===== từ chối đơn =====
async function cancelBooking(id) {
    if (!confirm("Bạn có chắc muốn từ chối không?")) return;

    try {
        const response = await fetch(`http://localhost:8081/beefchef/reservation/${id}/status`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ status: 'CANCELLED' })
        });

        if (response.ok) {
            alert("cập nhật đơn đặt bàn thành công!");
            window.location.href = "reservation.html"
            loadBooking();
        } else {
            alert("cập nhật đơn đặt bàn thất bại!");
        }

    } catch (error) {
        console.error("Lỗi cập nhật:", error);
        alert("Có lỗi khi cập nhật đơn đặt bàn!")
    }
}

//format trang thai
function getStatusText(status) {
    switch (status) {
        case "PENDING": return "Chờ xác nhận";
        case "CONFIRMED": return "Đã nhận";
        case "CANCELLED": return "Từ chối nhận";
        default: return status;
    }
}

loadBooking();

