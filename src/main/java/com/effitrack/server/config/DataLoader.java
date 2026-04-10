package com.effitrack.server.config;

import com.effitrack.server.constant.StringConst;
import com.effitrack.server.model.Equipment;
import com.effitrack.server.model.EquipmentLog;
import com.effitrack.server.model.EquipmentStatus;
import com.effitrack.server.model.User;
import com.effitrack.server.model.UserRole;
import com.effitrack.server.repository.EquipmentLogRepository;
import com.effitrack.server.repository.EquipmentRepository;
import com.effitrack.server.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
public class DataLoader implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private EquipmentLogRepository logRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.master.pin}")
    private String masterPinCode;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            User master = new User();
            master.setFullName(StringConst.MASTER_FULL_NAME);
            master.setTableNumber(StringConst.MASTER_TABLE_NUMBER);
            master.setProfession(StringConst.MASTER_PROFESSION);
            master.setPinCode(passwordEncoder.encode(masterPinCode));
            master.setRole(UserRole.MASTER);
            master.setShopNumber(StringConst.MASTER_SHOP_NUMBER);
            master.setEmail(StringConst.MASTER_EMAIL);

            userRepository.save(master);
        }
    }

    private void generateFakeHistory() {
        if (logRepository.count() > 0) return;
        List<Equipment> allEquipment = equipmentRepository.findAll();
        if (allEquipment.isEmpty()) return;

        Random random = new Random();
        LocalDateTime startOfShift = LocalDateTime.now()
                .withHour(Constants.SHIFT_START_HOUR)
                .withMinute(Constants.SHIFT_START_MINUTE)
                .withSecond(Constants.SHIFT_START_SECOND);

        for (Equipment eq : allEquipment) {
            LocalDateTime currentTime = startOfShift;
            int eventsCount = Constants.MIN_EVENTS_COUNT + random.nextInt(Constants.EVENTS_VARIANCE);

            for (int i = 0; i < eventsCount; i++) {
                EquipmentStatus status = EquipmentStatus.values()[random.nextInt(EquipmentStatus.values().length)];
                EquipmentLog log = new EquipmentLog();
                log.setEquipment(eq);
                log.setStatus(status);
                if (status == EquipmentStatus.DOWNTIME) {
                    log.setReason(Constants.REASON_DOWNTIME);
                } else if (status == EquipmentStatus.SETUP) {
                    log.setReason(Constants.REASON_SETUP);
                }
                log.setStartTime(currentTime);

                int durationMinutes = Constants.MIN_DURATION_MINUTES + random.nextInt(Constants.DURATION_VARIANCE_MINUTES);
                currentTime = currentTime.plusMinutes(durationMinutes);

                if (currentTime.isAfter(LocalDateTime.now())) {
                    currentTime = LocalDateTime.now();
                    log.setEndTime(currentTime);
                    logRepository.save(log);
                    break;
                }

                log.setEndTime(currentTime);
                logRepository.save(log);
            }

            eq.setLastStatusChange(currentTime);
            equipmentRepository.save(eq);
        }
    }

    private static class Constants {
        public static final int SHIFT_START_HOUR = 6;
        public static final int SHIFT_START_MINUTE = 0;
        public static final int SHIFT_START_SECOND = 0;
        public static final int MIN_EVENTS_COUNT = 3;
        public static final int EVENTS_VARIANCE = 3;
        public static final int MIN_DURATION_MINUTES = 40;
        public static final int DURATION_VARIANCE_MINUTES = 140;
        public static final String REASON_DOWNTIME = "Заміна інструменту / Технічна пауза";
        public static final String REASON_SETUP = "Калібрування перед партією";
    }
}
