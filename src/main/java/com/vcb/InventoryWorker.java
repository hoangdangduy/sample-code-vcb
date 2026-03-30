package com.vcb;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.camunda.bpm.client.ExternalTaskClient;

public class InventoryWorker {
    public static void main(String[] args) {
        // 1. Kết nối tới Camunda Engine đang chạy trên Docker
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(10000) // Thời gian chờ (Long Polling)
                .build();

        System.out.println("Worker đã khởi động và đang chờ việc...");

        // 2. Đăng ký theo dõi dải tần sóng (Topic) "check-inventory"
        client.subscribe("check-inventory")
                .lockDuration(1000) // Khóa nhiệm vụ lại để worker khác không lấy trùng
                .handler((externalTask, externalTaskService) -> {

                    // Lấy ID của tiến trình đang chạy để in ra màn hình
                    String processInstanceId = externalTask.getProcessInstanceId();
                    System.out.println("Đang kiểm tra tồn kho cho tiến trình ID: " + processInstanceId);

                    Map<String, Object> variables = new HashMap<>();
                    Random random = new Random();
                    boolean inStock = random.nextBoolean();
                    System.out.println("Tình trạng tồn kho: " + (inStock ? "Còn hàng" : "Hết hàng"));
                    variables.put("inStock", inStock);
                    variables.put("order", "DON-123");

                    // 3. Báo cáo lại cho Camunda là nhiệm vụ đã hoàn tất!
                    externalTaskService.complete(externalTask, variables);
                    System.out.println("Đã báo cáo hoàn thành về Camunda!");

                })
                .open();
    }
}