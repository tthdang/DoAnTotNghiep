document.getElementById("createForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    // const nameInput = document.getElementById("name");
    // const capacityInput = document.getElementById("capacity");

    const nameInput = document.getElementById("name").value.trim();
    let capacityInput = parseInt(document.getElementById("capacity").value);

    const table = {
        tableName: nameInput,
        tableCapacity: capacityInput,
        tableStatus: "AVAILABLE"

    };

    if(!nameInput){
        alert("Tên bàn không được bỏ trống");
        nameInput.focus();
        return;
    }

    if(isNaN(capacityInput) || capacityInput <1){
        alert("Số chỗ ngồi phải lớn hơn 0");
        capacityInput.focus();
        tableCapacity.value = 1;
        return;
    }


    console.log(table);
    try {
        
        const response = await fetch("http://localhost:8081/beefchef/tables", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(table)
        });

        if (!response.ok) throw new Error();

        alert("Tạo thành công!");

        window.location.href = "tables.html";

    } catch (error) {
        alert("Tạo thất bại!");
        console.error(error);
    }
});