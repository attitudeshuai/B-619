package com.hotel.config;

import com.hotel.entity.Room;
import com.hotel.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 客房数据初始化
 */
@Component
@Order(2)
public class RoomDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(RoomDataInitializer.class);

    @Autowired
    private RoomRepository roomRepository;

    @Override
    public void run(String... args) {
        if (roomRepository.count() == 0) {
            logger.info("初始化客房数据...");

            List<Room> rooms = new ArrayList<>();

            // 1楼 - 标准间
            for (int i = 1; i <= 10; i++) {
                Room room = new Room();
                room.setRoomNumber("1" + String.format("%02d", i));
                room.setRoomType("标准间");
                room.setPrice(new BigDecimal("199.00"));
                room.setFloor(1);
                room.setStatus("空闲");
                room.setDescription("配备双床、独立卫浴、免费WiFi");
                rooms.add(room);
            }

            // 2楼 - 大床房
            for (int i = 1; i <= 10; i++) {
                Room room = new Room();
                room.setRoomNumber("2" + String.format("%02d", i));
                room.setRoomType("大床房");
                room.setPrice(new BigDecimal("299.00"));
                room.setFloor(2);
                room.setStatus("空闲");
                room.setDescription("配备大床、独立卫浴、免费WiFi");
                rooms.add(room);
            }

            // 3楼 - 家庭房
            for (int i = 1; i <= 8; i++) {
                Room room = new Room();
                room.setRoomNumber("3" + String.format("%02d", i));
                room.setRoomType("家庭房");
                room.setPrice(new BigDecimal("399.00"));
                room.setFloor(3);
                room.setStatus("空闲");
                room.setDescription("配备双床+沙发床、独立卫浴、免费WiFi、儿童玩具");
                rooms.add(room);
            }

            // 4楼 - 豪华套房
            for (int i = 1; i <= 6; i++) {
                Room room = new Room();
                room.setRoomNumber("4" + String.format("%02d", i));
                room.setRoomType("豪华套房");
                room.setPrice(new BigDecimal("599.00"));
                room.setFloor(4);
                room.setStatus("空闲");
                room.setDescription("配备大床、客厅、独立卫浴、免费WiFi、迷你吧");
                rooms.add(room);
            }

            roomRepository.saveAll(rooms);
            logger.info("客房数据初始化完成，共创建 {} 间客房", rooms.size());
        } else {
            logger.info("客房数据已存在，跳过初始化");
        }
    }
}
