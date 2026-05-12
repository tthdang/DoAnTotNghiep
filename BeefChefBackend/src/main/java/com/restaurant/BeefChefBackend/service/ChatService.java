package com.restaurant.BeefChefBackend.service;

import com.restaurant.BeefChefBackend.dto.request.ChatRequest;
import com.restaurant.BeefChefBackend.entity.IngredientBatch;
import com.restaurant.BeefChefBackend.entity.Products;
import com.restaurant.BeefChefBackend.repository.ProductRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {
    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ProductRepository productRepository;

    @Autowired ProductService productService;

    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private IngredientBatchService batchService;

    public String chat(ChatRequest request) {

        String message = request.getMessage().toLowerCase();

        //chào
        if (message.contains("Chào") || message.contains("Hi") || message.contains("Hello")){
            String data = hello();
            return formatWithAI(data, request.getMessage());
        }

        // hướng dẫn gọi món
        if (message.contains("hướng dẫn") || message.contains("cách") || message.contains("các bước") || message.contains("giúp") || message.contains("help") || message.contains("gọi món")){
            String data = help();
            return formatWithAI(data, request.getMessage());
        }

        //món hôm nay có



        //Top 5 món
        if (message.contains("top") || message.contains("nổi bật") || message.contains("những món") || (message.contains("ngon nhất") && message.contains("danh sách")) || (message.contains("nên thử") && message.contains("danh sách")) ) {

            List<Products> top5 = productRepository.findTop5ByOrderByProductSoldDesc();

            String data = top5.stream()
                    .map(p -> "** " + p.getProductName() + "** ( Đã được gọi: " + p.getProductSold() + " lần). " + p.getProductDescription() + " - Giá: " + p.getProductPrice() + " VNĐ \n")
                    .collect(Collectors.joining(","));

            return formatWithAI(
                    "Top 5 món bán chạy: " + data,
                    request.getMessage()
            );
        }

        //món bán chạy nhất
        if (message.contains("bán chạy") || message.contains("nhiều nhất") || message.contains("ngon nhất")|| message.contains("nên thử")) {

            Products top = productRepository
                    .findTop5ByOrderByProductSoldDesc()
                    .stream()
                    .findFirst()
                    .orElse(null);

            if (top == null) return "Hiện chưa có dữ liệu.";

            return formatWithAI(
                    "Món bán chạy nhất là " + top.getProductName() +
                            " với " + top.getProductSold() + " lượt bán" +
                            " giá " + top.getProductPrice() + " VNĐ",
                    request.getMessage()
            );
        }

        //combo
        if (message.contains("combo") || message.contains("gợi ý") || message.contains("người")) {

            BigDecimal budget = extractBudget(message);
            int people = extractPeople(message);

            // lấy nguyên liệu sắp hết
            List<IngredientBatch> batches = batchService.getExpiringBatches(2);

            List<Products> all;

            if (!batches.isEmpty()) {
                List<Integer> ingredientIds =
                        ingredientService.getIngredientIdsFromBatches(batches);

                all = productService.getProductsByIngredients(ingredientIds);
            } else {
                all = productRepository.findAll();
            }

            // nếu không có budget thì auto theo người
            if (budget == null) {
                budget = BigDecimal.valueOf(people * 250000);
            }

            return buildCombo(all, budget, people, request.getMessage());
        }


//        return chatClient
//                .prompt(request.getMessage())
//                .call()
//                .content();

        return "Xin lỗi, mình chỉ có thể tư vấn cho bạn dựa trên menu của nhà hàng BeefChef.";
    }

    //gợi ý theo combo
    private String buildCombo(List<Products> all, BigDecimal budget, int people, String userQuestion) {

        if (all.isEmpty()) {
            return "Hiện tại chưa có dữ liệu món.";
        }

        List<Products> khaiVi = new ArrayList<>();
        List<Products> monChinh = new ArrayList<>();
        List<Products> monPhu = new ArrayList<>();
        List<Products> nuoc = new ArrayList<>();
        List<Products> dauBep = new ArrayList<>();

        for (Products p : all) {
            String cate = p.getCategory().getCategoryName().toLowerCase();
            if (cate.contains("khai vị")) khaiVi.add(p);
            else if (cate.contains("chính")) monChinh.add(p);
            else if (cate.contains("phụ")) monPhu.add(p);
            else if (cate.contains("tráng") || cate.contains("nước")) nuoc.add(p);
            else if (cate.contains("đặc biệt")) dauBep.add(p);
            else monChinh.add(p);
        }

        // Sắp xếp theo giá rẻ nhất
        List<List<Products>> lists = Arrays.asList(khaiVi, monChinh, monPhu, nuoc, dauBep);
        lists.forEach(list -> list.sort(Comparator.comparing(Products::getProductPrice)));

        List<Products> combo = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        BigDecimal targetBudget = BigDecimal.valueOf(people).multiply(BigDecimal.valueOf(250000));

        // Nếu user không chỉ định budget thì dùng target 250k/người
        if (budget == null || budget.compareTo(BigDecimal.valueOf(100000)) < 0) {
            budget = targetBudget;
        }

        // Thêm món chính
        List<Products> mainList = !monChinh.isEmpty() ? monChinh : dauBep;
        for (Products p : mainList) {
            if (combo.contains(p)) continue;
            BigDecimal newTotal = total.add(p.getProductPrice());
            if (newTotal.compareTo(budget) <= 0) {
                combo.add(p);
                total = newTotal;
            }
        }

        // Thêm các món còn lại theo thứ tự ưu tiên
        List<List<Products>> priority = Arrays.asList(nuoc, khaiVi, monPhu, monChinh, dauBep);
        for (List<Products> list : priority) {
            for (Products p : list) {
                if (combo.contains(p)) continue;
                BigDecimal newTotal = total.add(p.getProductPrice());
                if (newTotal.compareTo(budget) <= 0) {
                    combo.add(p);
                    total = newTotal;
                }
                if (total.compareTo(budget.multiply(BigDecimal.valueOf(0.9))) > 0) break; // Đủ gần target
            }
        }

        // Fallback mạnh nếu vẫn ít món
        if (combo.size() < Math.max(3, people)) {
            List<Products> remaining = all.stream()
                    .filter(p -> !combo.contains(p))
                    .sorted(Comparator.comparing(Products::getProductPrice))
                    .collect(Collectors.toList());

            for (Products p : remaining) {
                if (combo.size() >= people + 2) break;   // giới hạn tổng số món

                BigDecimal newTotal = total.add(p.getProductPrice());
                if (newTotal.compareTo(budget.multiply(BigDecimal.valueOf(1.35))) <= 0) {
                    combo.add(p);
                    total = newTotal;
                }
            }
        }

        // response
        StringBuilder data = new StringBuilder();
        data.append("Combo gợi ý cho ").append(people).append(" người (target ~").append(targetBudget).append(" VNĐ):\n");

        for (Products p : combo) {
            data.append("- **").append(p.getProductName())
                    .append("** (")
                    .append(p.getProductDescription()).append(") - ")
                    .append(p.getProductPrice()).append(" VNĐ; \n");
        }

        data.append("Tổng: ").append(total).append(" VNĐ");

        return formatWithAI(data.toString(), userQuestion);
    }

    // xác định combo tiền
    private BigDecimal extractBudget(String message) {

        // dạng 150k
        if (message.contains("k")) {
            String number = message.replaceAll("[^0-9]", "");
            if (!number.isEmpty()) {
                return new BigDecimal(number + "000");
            }
        }

        // dạng 200000
        String number = message.replaceAll("[^0-9]", "");
        if (!number.isEmpty()) {
            return new BigDecimal(number);
        }

        return null;
    }

    //tính người
    private int extractPeople(String message) {

        if (message.contains("1")) return 1;
        if (message.contains("2")) return 2;
        if (message.contains("3")) return 3;
        if (message.contains("4")) return 4;

        return 2;
    }

    //hướng dẫn gọi món
    private String help(){
        String data = """
                Mình sẽ hướng dẫn bạn cách gọi món nhé! \n
                1. Lựa món bạn muốn gọi và nhấn nút **"Chọn"**. \n
                2. Sau khi chọn món xong thì nhấn vào giỏ hàng ở bên dưới khung chat. \n
                3. Chỉnh sửa số lượng bằng các nút "+" để thêm số lượng và "-" để giảm số lượng \n
                4. Tiếp theo nhấn nút **"Xác nhận gọi món"** \n
                5. Nhấn **"Trạng thái món ăn"** ở phía bên phải hướng lên trên để xem trạng thái món ăn nhé! \n
                Nếu bạn có thắc mắc gì về menu thì cứ hỏi mình nha!\n
                Chúc bạn có bữa ăn thật ngon miệng!
                
                """;
        return data;
    }

    private String hello(){
        String data = """
                Xin chào! Mình là Beef AI 🤖 của nhà hàng, mình có thể giúp gì cho bạn?
                """;
        return data;
    }

    // dùng AI để viết lại cho tự nhiên
    private String formatWithAI(String data, String userQuestion) {

        String prompt = """
                Bạn là chatbot của nhà hàng BeefChef.
                
                Dữ liệu combo:
                %s
                
                Yêu cầu:
                - Giới thiệu combo một cách hấp dẫn
                - KHÔNG được thêm thông tin ngoài dữ liệu.
                - Nhấn mạnh đây là combo được mình 🤖
                - Có thể nói: "combo hôm nay", "combo đặc biệt"
                - KHÔNG được đề cập đến nguyên liệu hết hạn
                - KHÔNG nói lý do nội bộ
                - Làm nổi bật tên món bằng dấu **
                - Nếu là danh sách món ăn thì phải liệt kê thành từng dòng, không được trả lời thành 1 đoạn văn
                
                Câu hỏi:
                "%s"
                """.formatted(data, userQuestion);

        try {
            return chatClient.prompt(prompt).call().content();
        } catch (Exception e) {
            return data;
        }
    }
}
