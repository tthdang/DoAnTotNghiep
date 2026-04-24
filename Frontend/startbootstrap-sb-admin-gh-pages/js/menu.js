const API_BASE = "http://localhost:8081/beefchef";
const PRODUCTS_API = `${API_BASE}/products`;
const CATEGORIES_API = `${API_BASE}/categories`;

let categories = [];
let currentPage = 1;
const itemsPerPage = 6;
let currentFilter = 'all';

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
        // list = list.filter(p => p.productStatus === 'AVAILABLE');

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
                sold: p.productSold,
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

// hiển thị category
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

// hiển thị menu
function renderMenu(filter = 'all') {
    const container = document.getElementById('menuGrid');
    if (!container) return;

    currentFilter = filter;

    let filtered = menuData;

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

    // hiển thị món
    container.innerHTML = paginatedItems.map(item => `
        <div class="menu-card">
            <div class="menu-card-img-wrap">
                <img class="menu-card-img" src="${item.img}" alt="${item.name}" loading="lazy"/>
            </div>
            <div class="menu-card-body">
                <div class="menu-card-cat">${item.catLabel}</div>
                <div class="menu-card-name">${item.name}</div>
                <div class="menu-card-desc">${item.desc}</div>
                
                
                <div class="menu-card-footer">
                    <div class="menu-card-stock">Đã bán: ${item.sold}</div>
                    <span class="menu-price">${fmt(item.price)}</span>
                    
                </div>
            </div>
        </div>
    `).join('');

    renderPagination(filtered.length);
}

//phan trang
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

//top5
async function loadTop5Dishes() {
    try {
        const response = await fetch(`${API_BASE}/products/bestSeller`);
        if (!response.ok) throw new Error("Lỗi khi lấy Top 5");

        const data = await response.json();

        // Lưu dữ liệu
        signatureDishes = data.result.map(item => ({

            productId: item.productId,
            name: item.productName,
            desc: item.productDescription || "Đang cập nhật mô tả...",
            price: Math.round(item.productPrice),
            image: item.productImage || "https://via.placeholder.com/300x210?text=No+Image",
            sold: item.productSold,
            category: item.categoryName
        }));

        // Gán rank từ 1 đến 5
        signatureDishes.forEach((dish, index) => {
            dish.rank = index + 1;
        });

        renderSignatureDishes();

    } catch (error) {
        console.error("Lỗi load Top 5 dishes:", error);

        // signatureDishes = [
        //     { rank: 1, name: "Australian Ribeye Steak", desc: "Thăn ngoại Úc 300g...", price: 389000, image: "https://via.placeholder.com/300x210?text=Ribeye" },
            
        // ];
        renderSignatureDishes();
    }
}

function renderSignatureDishes() {
    const container = document.getElementById('signatureContainer');
    if (!container) return;

    container.innerHTML = '';

    signatureDishes.forEach(dish => {
        const cardHTML = `
            <div class="signature-card position-relative">
                <div class="signature-rank">${dish.rank}</div>
                <img src="${dish.image}" 
                     alt="${dish.name}" 
                     loading="lazy"
                     onerror="this.src='https://via.placeholder.com/600x400?text=No+Image'">
                <div class="signature-info">
                    <h3 class="signature-name">${dish.name}</h3>
                    <p class="signature-desc">${dish.desc}</p>
                </div>
                <div class="menu-card-footer">
                        <div class="menu-card-sold">Đã bán: ${dish.sold}</div>
                        <div class="signature-price">${dish.price.toLocaleString('vi-VN')}đ </div>
                </div>
                
            </div>
        `;
        container.innerHTML += cardHTML;
    });
}


// ==================== INIT ====================
document.addEventListener('DOMContentLoaded', () => {
    console.log("Page loaded");

    loadAllData();        
    loadTop5Dishes();     

    // Ngày đặt bàn
    const bkDate = document.getElementById('bk-date');
    if (bkDate) bkDate.min = new Date().toISOString().split('T')[0];

    // Kiểm tra đăng nhập
    const token = localStorage.getItem("token");
    const username = localStorage.getItem("username");
    if (token && username) {
        const authButtons = document.getElementById("authButtons");
        const userMenu = document.getElementById("userMenu");
        if (authButtons) authButtons.style.display = "none";
        if (userMenu) {
            userMenu.style.display = "flex";
            document.getElementById("welcomeUser").innerText = "Xin chào, " + username;
        }
    }
});

// ==================== FILTER ====================
function filterMenu(cat, el) {
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    el.classList.add('active');

    currentPage = 1;

    renderMenu(cat);
}

function logout() {
    localStorage.clear();
    window.location.href = "homePage.html";
}

