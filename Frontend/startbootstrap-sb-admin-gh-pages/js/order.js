const API_BASE = "http://localhost:8081/beefchef";
const PRODUCTS_API = `${API_BASE}/products`;
const CATEGORIES_API = `${API_BASE}/categories`;

let menuData = [];
let categories = [];
let cart = [];
//phân trang
let currentPage = 1;
const itemsPerPage = 6;
let currentFilter = 'all';

let orderStatusInterval = null;
const POLLING_INTERVAL = 3000;    // 3 giây 

// format giá
function fmt(price) {
    return price.toLocaleString('vi-VN') + 'đ';
}

// tạo slug
function createSlug(str) {
    return str
        .toLowerCase()
        .normalize("NFD")
        .replace(/[\u0300-\u036f]/g, "")
        .replace(/[^a-z0-9\s]/g, '')
        .trim()
        .replace(/\s+/g, '');
}

// lấy data
async function loadAllData() {
    try {
        // Load danh mục
        const catRes = await fetch(CATEGORIES_API);
        if (!catRes.ok) throw new Error("Lỗi load danh mục");
        categories = await catRes.json();

        // Load sản phẩm
        const prodRes = await fetch(PRODUCTS_API);
        if (!prodRes.ok) throw new Error("Lỗi load sản phẩm");

        let products = await prodRes.json();
        let list = products.result;
        list = list.filter(p => p.productStatus === 'AVAILABLE');

        menuData = list.map(p => {
            const catName = p.categoryName.trim() || "Khác";
            const slug = createSlug(catName);

            return {
                id: p.productId,
                cat: slug,
                catLabel: catName,
                name: p.productName,
                desc: p.productDescription || "Đang cập nhật mô tả...",
                price: p.productPrice,
                stock: p.productStock,
                img: p.productImage || "https://via.placeholder.com/600x400?text=No+Image"
            };
        });

        renderCategoryTabs();
        renderMenu('all');

    } catch (error) {
        console.error("Lỗi tải dữ liệu:", error);
        alert("Không thể tải thực đơn!");
    }
}

// Hiển thị danh mục 
function renderCategoryTabs() {
    const container = document.getElementById('categoryTabs');
    if (!container) {
        console.error("Không tìm thấy Danh mục");
        return;
    }

    let html = `<button class="tab-btn active" onclick="filterMenu('all', this)">Tất cả</button>`;

    categories.forEach(cat => {
        const slug = createSlug(cat.categoryName);
        html += `
            <button class="tab-btn" onclick="filterMenu('${slug}', this)">
                ${cat.categoryName}
            </button>`;
    });

    container.innerHTML = html;
}

//   hiển thị menu
function renderMenu(filter = 'all') {
    const container = document.getElementById('menuGrid');
    if (!container) return;

    currentFilter = filter;

    let filtered = menuData || [];

    if (filter !== 'all') {
        filtered = menuData.filter(item => item.cat === filter);
    }

    if (filtered.length === 0) {
        container.innerHTML = `
            <div style="grid-column: 1 / -1; text-align: center; padding: 80px 20px; color: var(--muted);">
                <h3>Chưa có món ăn nào trong danh mục này</h3>
            </div>`;
        document.getElementById('pagination').innerHTML = "";
        return;
    }

    // PHÂN TRANG
    const start = (currentPage - 1) * itemsPerPage;
    const end = start + itemsPerPage;
    const paginatedItems = filtered.slice(start, end);

    // RENDER MÓN
    container.innerHTML = paginatedItems.map(item => `
        <div class="menu-card">
            <div class="menu-card-img-wrap">
                <img class="menu-card-img" src="${item.img}" alt="${item.name}" loading="lazy"/>
            </div>
            <div class="menu-card-body">
                <div class="menu-card-cat">${item.catLabel}</div>
                <div class="menu-card-name">${item.name}</div>
                <div class="menu-card-desc">${item.desc}</div>
                <div class="menu-card-stock">Còn lại: ${item.stock}</div>
                
                <div class="menu-card-footer">
                    <span class="menu-price">${fmt(item.price)}</span>
                    <button class="add-to-order" onclick="addToCart(${item.id})">+ Chọn</button>
                </div>
            </div>
        </div>
    `).join('');

    renderPagination(filtered.length);
}

function renderPagination(totalItems) {
    const pagination = document.getElementById('pagination');
    if (!pagination) return;

    const totalPages = Math.ceil(totalItems / itemsPerPage);
    pagination.innerHTML = "";

    // Prev
    const prevBtn = document.createElement("button");
    prevBtn.innerText = "«";
    prevBtn.disabled = currentPage === 1;
    prevBtn.onclick = () => {
        currentPage--;
        renderMenu(currentFilter);
    };
    pagination.appendChild(prevBtn);

    // Page numbers
    for (let i = 1; i <= totalPages; i++) {
        const btn = document.createElement("button");
        btn.innerText = i;

        if (i === currentPage) btn.classList.add("active");

        btn.onclick = () => {
            currentPage = i;
            renderMenu(currentFilter);
        };

        pagination.appendChild(btn);
    }

    // Next
    const nextBtn = document.createElement("button");
    nextBtn.innerText = "»";
    nextBtn.disabled = currentPage === totalPages;
    nextBtn.onclick = () => {
        currentPage++;
        renderMenu(currentFilter);
    };
    pagination.appendChild(nextBtn);
}

//   FILTER  
function filterMenu(cat, el) {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    el.classList.add('active');

    currentPage = 1;


    renderMenu(cat);
}

//   INIT  
document.addEventListener('DOMContentLoaded', () => {
    loadAllData();

    const bkDate = document.getElementById('bk-date');
    if (bkDate) bkDate.min = new Date().toISOString().split('T')[0];
});


document.addEventListener("DOMContentLoaded", function () {
    const token = localStorage.getItem("token");
    const username = localStorage.getItem("username");

    if (token && username) {
        document.getElementById("authButtons").style.display = "none";
        document.getElementById("userMenu").style.display = "flex";
        document.getElementById("welcomeUser").innerText = "Xin chào, " + username;
    }
});

//thêm vào giỏ
function addToCart(productId) {
    const product = menuData.find(item => item.id === productId);

    if (!product) {
        showToast("Không tìm thấy món ăn!", "error");
        return;
    }

    //Kiểm tra còn hàng không
    if (product.stock <= 0) {
        showToast("Món này đã hết hàng!", "error");
        return;
    }

    //Kiểm tra món đã có trong giỏ chưa
    const existing = cart.find(item => item.id === productId);

    if (existing) {
        existing.quantity += 1;
    } else {
        cart.push({ ...product, quantity: 1 });
    }

    //Cập nhật giao diện giỏ hàng
    updateCartUI();

    showToast(`Đã thêm: ${product.name}`);
}

function updateCartUI() {
    const cartBadge = document.getElementById('cartBadge');
    const orderItems = document.getElementById('orderItems');
    const orderTotal = document.getElementById('orderTotal');
    const totalVal = document.getElementById('totalVal');

    // Cập nhật số trên nút giỏ hàng
    const totalQuantity = cart.reduce((sum, item) => sum + item.quantity, 0);
    cartBadge.textContent = totalQuantity;

    if (cart.length === 0) {
        orderItems.innerHTML = `<div class="order-empty"><span>🍽️</span>Chưa có món nào được chọn</div>`;
        orderTotal.style.display = 'none';
        return;
    }

    // Hiển thị danh sách món trong panel
    orderItems.innerHTML = cart.map(item => `
        <div class="order-item">
            <div class="order-item-info">
                <div class="order-item-name">${item.name}</div>
                <div class="order-item-price">${fmt(item.price)} × ${item.quantity}</div>
            </div>
            <div class="order-item-total">${fmt(item.price * item.quantity)}</div>
            
            <div class="order-item-actions">
                <button onclick="changeQuantity(${item.id}, -1)" class="qty-btn">-</button>
                <span class="qty">${item.quantity}</span>
                <button onclick="changeQuantity(${item.id}, 1)" class="qty-btn">+</button>
                <button onclick="removeFromCart(${item.id})" class="remove-btn">🗑</button>
            </div>
        </div>
    `).join('');

    // Tính tổng tiền
    const total = cart.reduce((sum, item) => sum + item.price * item.quantity, 0);
    totalVal.textContent = fmt(total);
    orderTotal.style.display = 'block';
}

// thay đổi số lượng
function changeQuantity(productId, change) {
    const item = cart.find(i => i.id === productId);
    if (!item) return;

    item.quantity += change;

    if (item.quantity < 1) {
        removeFromCart(productId);
    } else {
        updateCartUI();
    }
}

//  xoá món
function removeFromCart(productId) {
    cart = cart.filter(item => item.id !== productId);
    updateCartUI();
}

// Hàm thông báo
function showToast(message, type = "success") {
    console.log(`[Toast Debug] Gọi showToast: "${message}" - type: ${type}`);

    let container = document.getElementById('toast-container');
    
    if (!container) {
        console.log("[Toast Debug] Tạo mới toast-container");
        container = document.createElement('div');
        container.id = 'toast-container';
        container.className = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast--${type}`;
    toast.textContent = message;


    container.appendChild(toast);
    console.log(`[Toast Debug] Đã thêm toast vào DOM. Tổng toast hiện tại: ${container.children.length}`);

    // Tự động ẩn sau 4 giây
    setTimeout(() => {
        console.log("[Toast Debug] Bắt đầu ẩn toast");
        toast.style.transition = 'opacity 0.4s ease';
        toast.style.opacity = '0';
        
        setTimeout(() => {
            if (toast.parentNode) {
                toast.parentNode.removeChild(toast);
                console.log("[Toast Debug] Đã xóa toast khỏi DOM");
            }
        }, 400);
    }, 4000);
}

// ── ORDER PANEL ──
function toggleOrderPanel() {
    document.getElementById('orderPanel').classList.toggle('open');
}

//   PLACE ORDER - SỬA THEO DTO BACKEND  
async function placeOrder() {
    if (cart.length === 0) {
        showToast("Giỏ hàng đang trống!", "error");
        return;
    }

    const currentOrderStr = localStorage.getItem("currentOrder");
    if (!currentOrderStr) {
        showToast("Không tìm thấy Order!", "error");
        return;
    }

    const currentOrder = JSON.parse(currentOrderStr);
    const orderId = currentOrder.orderId;

    // Chuẩn bị body theo đúng DTO AddItemsRequest
    const requestBody = {
        items: cart.map(item => ({
            productId: item.id,
            quantity: item.quantity
        }))
    };

    try {
        const response = await fetch(`http://localhost:8081/beefchef/orders/${orderId}`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
                // Không cần Authorization nếu bạn cho phép public
            },
            body: JSON.stringify(requestBody)
        });

        if (!response.ok) {
            const errText = await response.text();
            throw new Error(errText || "Đặt món thất bại");
        }

        const data = await response.json();

        showToast(` ${data.message || "Đã gọi món thành công!"}`, "success");

        // Reset giỏ hàng
        cart = [];
        updateCartUI();
        toggleOrderPanel();

        // Cập nhật lại order
        if (data.result) {
            localStorage.setItem("currentOrder", JSON.stringify(data.result));
        }
        loadAllData();

    } catch (error) {
        console.error("Lỗi đặt món:", error);
        showToast("Lỗi " + error.message, "error");
    }
}


//   HIỂN THỊ TRẠNG THÁI MÓN ĂN
async function showOrderStatus() {
    const currentOrderStr = localStorage.getItem("currentOrder");
    const modal = document.getElementById('orderStatusModal');
    const content = document.getElementById('orderStatusContent');

    if (!currentOrderStr) {
        content.innerHTML = `
            <div style="text-align:center; padding:50px 20px; color:#888;">
                <p>Chưa có order nào được tạo.</p>
                <p>Vui lòng xác nhận bàn trước khi xem trạng thái món ăn.</p>
            </div>`;
        modal.style.display = "flex";
        return;
    }

    let order;
    try {
        order = JSON.parse(currentOrderStr);
    } catch (e) {
        console.error("JSON parse error:", e);
        localStorage.removeItem("currentOrder"); // Xóa dữ liệu hỏng
        content.innerHTML = `<p style="color:red;">Dữ liệu order bị hỏng. Vui lòng tạo order mới.</p>`;
        modal.style.display = "flex";
        return;
    }

    const orderId = order.orderId || order.id; // Một số API trả về id thay vì orderId

    if (!orderId) {
        content.innerHTML = `<p style="color:red;">Order không có ID hợp lệ.</p>`;
        modal.style.display = "flex";
        return;
    }

    modal.style.display = "flex";

    // Load lần đầu tiên
    await loadOrderStatus(orderId, content);

    // Dừng polling cũ nếu có
    if (orderStatusInterval) {
        clearInterval(orderStatusInterval);
    }

    // Bắt đầu Polling
    orderStatusInterval = setInterval(() => {
        loadOrderStatus(orderId, content);
    }, POLLING_INTERVAL);
}

//   HỦY MÓN  
async function cancelOrderItem(orderId, orderItemId) {
    if (!confirm("Bạn có chắc chắn muốn hủy món này không?")) {
        return;
    }

    try {
        const response = await fetch(
            `http://localhost:8081/beefchef/orders/${orderId}/${orderItemId}/cancel`,
            {
                method: "PUT",
                headers: { "Content-Type": "application/json" }
            }
        );

        if (!response.ok) {
            const errText = await response.text();
            throw new Error(errText || "Không thể hủy món");
        }

        const data = await response.json();
        console.log("Response từ backend sau khi hủy: ", data);

        showToast("Đã hủy món thành công!", "success");

        // Cập nhật lại localStorage và refresh modal
        if (data.result) {
            localStorage.setItem("currentOrder", JSON.stringify(data.result));
        } else if (data) {
            localStorage.setItem("currentOrder", JSON.stringify(data));
        }

        // Tải lại modal để cập nhật giao diện
        const content = document.getElementById('orderStatusContent');
        if (content && orderId) {
            await loadOrderStatus(orderId, content);   // Load lại ngay lập tức
        }

    } catch (error) {
        console.error(error);
        showToast("Lỗi " + error.message, "error");
    }
}

// Hàm load & render trạng thái
async function loadOrderStatus(orderId, content) {
    if (!orderId || orderId === "undefined" || orderId === undefined) {
        content.innerHTML = `
            <div style="text-align:center; padding:50px 20px; color:#e74c3c;">
                <p>Không tìm thấy Order ID hợp lệ.</p>
                <small>Vui lòng tạo order (xác nhận bàn) trước khi xem trạng thái.</small>
            </div>`;
        return;
    }
    try {
        const response = await fetch(`http://localhost:8081/beefchef/orders/${orderId}`, {
            method: "GET",
            headers: { "Content-Type": "application/json" }
        });

        if (!response.ok) throw new Error("Không thể lấy dữ liệu order");

        const data = await response.json();
        const latestOrder = data.result || data;

        let html = `
            <div class="order-info">
                <strong>Bàn:</strong> ${latestOrder.tableName || '—'} &nbsp;&nbsp; 
                <strong>Order #${latestOrder.orderId}</strong><br>
                <small>Thời gian: ${new Date(latestOrder.createdAt).toLocaleString('vi-VN')}</small>
            </div>
            <hr>
        `;

        if (!latestOrder.items || latestOrder.items.length === 0) {
            html += `<p style="text-align:center; color:#888; padding:40px;">Chưa có món nào được gọi.</p>`;
        } else {
            html += latestOrder.items.map(item => `
                <div class="order-status-item">
                    <div class="item-info">
                        <div class="item-name">${item.productName}</div>
                        <div class="item-quantity">
                            ${item.orderItemQuantity} × ${fmt(item.orderItemPrice)}
                        </div>
                    </div>
                    
                    <div class="status-container">
                        <div class="status-badge status-${item.orderItemStatus.toLowerCase()}">
                            ${getStatusText(item.orderItemStatus)}
                        </div>
                        
                        ${item.orderItemStatus === 'PENDING' ? `
                        <button class="cancel-btn" 
                                onclick="cancelOrderItem(${orderId}, ${item.orderItemId})">
                            Hủy món
                        </button>` : ''}
                    </div>
                </div>
            `).join('');
        }

        content.innerHTML = html;

    } catch (error) {
        console.error(error);
        content.innerHTML = `
            <div style="text-align:center; color:#e74c3c; padding:40px;">
                <p>Không thể tải trạng thái món ăn.</p>
                <small>${error.message}</small>
            </div>`;
    }
}

function getStatusText(status) {
    switch (status) {
        case "PENDING": return "Đang chờ";
        case "COOKING": return "Đang nấu";
        case "READY": return "Sẵn sàng";
        case "SERVED": return "Đã phục vụ";
        case "CANCEL": return "Đã hủy";
        default: return status;
    }
}

function closeOrderStatusModal() {
    const modal = document.getElementById('orderStatusModal');

    // Dừng polling
    if (orderStatusInterval) {
        clearInterval(orderStatusInterval);
        orderStatusInterval = null;
    }

    modal.style.display = "none";
}

// Đóng modal khi click ra ngoài
document.addEventListener('click', function (e) {
    const modal = document.getElementById('orderStatusModal');
    if (e.target === modal) {
        closeOrderStatusModal();
    }
});