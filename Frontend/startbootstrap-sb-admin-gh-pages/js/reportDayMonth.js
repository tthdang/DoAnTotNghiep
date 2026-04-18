let dailyChart = null;
let weeklyChart = null;

function formatCurrency(value) {
  return new Intl.NumberFormat('vi-VN').format(Math.round(value || 0));
}


async function fetchDailyReport(month, year) {
  const res = await fetch(`http://localhost:8081/beefchef/report/day?month=${month}&year=${year}`);
  const json = await res.json();
  console.log("Daily API response:", json);
  return json.result || { days: [], values: [] };
}

async function fetchWeeklyReport(month, year) {
  const res = await fetch(`http://localhost:8081/beefchef/report/week?month=${month}&year=${year}`);
  const json = await res.json();
  console.log(" Weekly API response:", json);
  return json.result || { days: [], values: [] };
}

// biểu đồ ngày
function renderDailyChart(data) {
  if (dailyChart) dailyChart.destroy();

  if (!data || !data.days || data.days.length === 0) {
    console.warn("⚠️ Không có dữ liệu ngày");
    return;
  }

  dailyChart = new Chart(document.getElementById('dailyChart'), {
    type: 'line',
    data: {
      labels: data.days,
      datasets: [{
        label: 'Doanh thu theo ngày',
        data: data.values,
        borderColor: '#2563eb',
        backgroundColor: 'rgba(37, 99, 235, 0.12)',
        tension: 0.4,
        borderWidth: 4,
        pointRadius: 6,
        pointHoverRadius: 10,
        fill: true
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        },
        tooltip: {
          callbacks: {
            label: (ctx) => formatCurrency(ctx.raw) + ' VNĐ'
          }
        }
      },
      scales: {
        y: { beginAtZero: true }
      }
    }
  });
}

// biểu đồ tuần
function renderWeeklyChart(data) {
  if (weeklyChart) weeklyChart.destroy();

  if (!data || !data.days || data.days.length === 0) {
    console.warn("⚠️ Không có dữ liệu tuần");
    return;
  }

  weeklyChart = new Chart(document.getElementById('weeklyChart'), {
    type: 'bar',
    data: {
      labels: data.days,
      datasets: [{
        label: 'Doanh thu theo tuần',
        data: data.values,
        backgroundColor: '#10b981',
        borderColor: '#059669',
        borderWidth: 2,
        borderRadius: 8,
        barThickness: 55
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        },
        tooltip: {
          callbacks: {
            label: (ctx) => formatCurrency(ctx.raw) + ' VNĐ'
          }
        }
      },
      scales: {
        y: { beginAtZero: true }
      }
    }
  });
}

// ================== LOAD DATA ==================
async function loadRevenueData() {
  const month = document.getElementById('monthSelect').value;
  const year = document.getElementById('yearSelect').value;
  const btn = document.getElementById('loadBtn');

  btn.textContent = 'Đang tải...';
  btn.disabled = true;

  try {
    const [dailyData, weeklyData] = await Promise.all([
      fetchDailyReport(month, year),
      fetchWeeklyReport(month, year)
    ]);

    renderDailyChart(dailyData);
    renderWeeklyChart(weeklyData);

  } catch (err) {
    console.error(" Lỗi:", err);
    alert("Lỗi tải dữ liệu: " + err.message);
  } finally {
    btn.textContent = 'Tải dữ liệu';
    btn.disabled = false;
  }
}

//load product
let dataTableInstance = null;

async function loadProducts() {
  const tableBody = document.getElementById("productTableBody");



  try {
    const response = await fetch("http://localhost:8081/beefchef/products/bestSeller");
    if (!response.ok) throw new Error("Lỗi khi lấy dữ liệu");

    const data = await response.json();

    const products = data.result; 
    tableBody.innerHTML = "";

    products.forEach(product => {
      const row = `
                <tr>
                    <td>${product.productId}</td>
                    <td>
                        <img src="${product.productImage}" 
                             width="60" height="60" 
                             style="object-fit: cover; border-radius: 8px;">
                    </td>
                    <td>${product.productName}</td>
                    <td>${product.productDescription || ''}</td>
                    <td>${product.categoryName || ''}</td>
                    <td>${formatPrice(product.productPrice)}</td>
                    <td>${product.productStock}</td>
                    <td>${product.productSold}</td>
                    <td>
                        <span class="badge ${product.productStatus === 'AVAILABLE' ? 'bg-success' : 'bg-danger'}">
                            ${product.productStatus}
                        </span>
                    </td>
                    <td>
                        <button class="btn btn-warning btn-sm" onclick="editProduct(${product.productId})">Edit</button>
                        <button class="btn btn-danger btn-sm" onclick="deleteProduct(${product.productId})">Delete</button>
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
    console.error("Lỗi loadProducts:", error);
  }
}

//Format giá tiền
function formatPrice(price) {
  return price.toLocaleString("vi-VN") + " VNĐ";
}


// ================== INIT ==================
document.addEventListener('DOMContentLoaded', () => {
  // Khởi tạo tháng
  const monthSelect = document.getElementById('monthSelect');
  for (let m = 1; m <= 12; m++) {
    let opt = document.createElement('option');
    opt.value = m;
    opt.textContent = `Tháng ${m}`;
    if (m === new Date().getMonth() + 1) opt.selected = true;
    monthSelect.appendChild(opt);
  }

  // Khởi tạo năm
  const yearSelect = document.getElementById('yearSelect');
  const curYear = new Date().getFullYear();
  for (let y = curYear - 2; y <= curYear + 1; y++) {
    let opt = document.createElement('option');
    opt.value = y;
    opt.textContent = y;
    if (y === curYear) opt.selected = true;
    yearSelect.appendChild(opt);
  }

  document.getElementById('loadBtn').addEventListener('click', loadRevenueData);

  // Tự động tải
  setTimeout(loadRevenueData, 500);
  loadProducts();
});