const API_BASE = "http://localhost:8081/beefchef";
const PRODUCTS_API = `${API_BASE}/products`;
const CATEGORIES_API = `${API_BASE}/categories`;

let categories = [];

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
        products = products.filter(p => p.productStatus === 'AVAILABLE');

        menuData = products.map(p => {
            const catName = p.category?.categoryName?.trim() || "Khác";
            const slug = createSlug(catName);

            return {
                id: p.productId,
                cat: slug,
                catLabel: catName,
                name: p.productName,
                desc: p.productDescription || "Đang cập nhật mô tả...",
                price: p.productPrice,
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

// ==================== RENDER TABS ====================
function renderCategoryTabs() {
    const container = document.getElementById('categoryTabs');
    if (!container) {
        console.error("Không tìm thấy #categoryTabs");
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

// ==================== RENDER MENU ====================
function renderMenu(filter = 'all') {
    const container = document.getElementById('menuGrid');
    if (!container) return;

    let filtered = menuData;

    if (filter !== 'all') {
        filtered = menuData.filter(item => item.cat === filter);
    }

    if (filtered.length === 0) {
        container.innerHTML = `
            <div style="grid-column: 1 / -1; text-align: center; padding: 80px 20px; color: var(--muted);">
                <h3>Chưa có món ăn nào trong danh mục này</h3>
            </div>`;
        return;
    }

    container.innerHTML = filtered.map(item => `
        <div class="menu-card">
            <div class="menu-card-img-wrap">
                <img class="menu-card-img" src="${item.img}" alt="${item.name}" loading="lazy"/>
            </div>
            <div class="menu-card-body">
                <div class="menu-card-cat">${item.catLabel}</div>
                <div class="menu-card-name">${item.name}</div>
                <div class="menu-card-desc">${item.desc}</div>
                
                <div class="menu-card-footer">
                    <span class="menu-price">${fmt(item.price)}</span>
                    
                </div>
            </div>
        </div>
    `).join('');
}

// ==================== FILTER ====================
function filterMenu(cat, el) {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    el.classList.add('active');
    renderMenu(cat);
}

// ==================== INIT ====================
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

function logout() {
    localStorage.clear();
    window.location.href = "homePage.html";
}