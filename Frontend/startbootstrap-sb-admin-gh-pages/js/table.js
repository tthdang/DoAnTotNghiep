let allTables = [];
let orderOfTable = null;

async function loadTables() {
    try {
        const response = await fetch('http://localhost:8081/beefchef/tables'); 
        const data = await response.json();
        
        if (data.result) {
            allTables = data.result;
            renderTables(allTables);
        }
    } catch (error) {
        console.error("Lỗi khi lấy danh sách bàn:", error);
        alert("Không thể tải danh sách bàn!");
    }
}

function getStatusClass(status) {
    return status === 'OCCUPIED' ? 'occupied' : 'available';
}

function getStatusText(status) {
    return status === 'OCCUPIED' ? 'Đang có khách' : 'Trống';
}

function renderTables(tables) {
    const grid = document.getElementById('tableGrid');
    if (!grid) return;
    
    grid.innerHTML = '';

    tables.forEach(table => {
        const card = document.createElement('div');
        card.className = `table-card ${getStatusClass(table.tableStatus)}`;
        
        card.innerHTML = `
            <div class="table-name">${table.tableName}</div>
            <div class="capacity">${table.tableCapacity} chỗ ngồi</div>
            <div class="status ${table.tableStatus.toLowerCase()}">
                ${getStatusText(table.tableStatus)}
            </div>
            
            ${table.tableStatus === 'OCCUPIED' && table.orderId ? `
            <div class="order-info">
                Order: <strong>#${table.orderId}</strong>
            </div>` : ''}
        `;

        // Click chuyển trang
        card.addEventListener('click', () => handleTableClick(table));

        grid.appendChild(card);
    });
}

function filterTables() {
    const searchText = document.getElementById('searchInput').value.toLowerCase();
    const statusFilter = document.getElementById('statusFilter').value;

    const filtered = allTables.filter(table => {
        const matchName = table.tableName.toLowerCase().includes(searchText);
        const matchStatus = !statusFilter || table.tableStatus === statusFilter;
        return matchName && matchStatus;
    });

    renderTables(filtered);
}

async function handleTableClick(table) {
    if (table.tableStatus === 'AVAILABLE') {
        alert("Bàn chưa có khách sử dụng!")
        return;
    }

    if (table.tableStatus === 'OCCUPIED' && table.orderId) {
        // Chuyển sang trang chi tiết order
        window.location.href = `orderDetail.html?orderId=${table.orderId}`;
    } else {
        alert(`Bàn ${table.tableName} đang ở trạng thái: ${getStatusText(table.tableStatus)}`);
    }
}


document.addEventListener('DOMContentLoaded', () => {
    loadTables();
});
