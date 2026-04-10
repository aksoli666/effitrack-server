package com.effitrack.server.service;

import com.effitrack.server.model.Equipment;
import com.effitrack.server.model.EquipmentLog;
import com.effitrack.server.model.EquipmentStatus;
import com.effitrack.server.repository.EquipmentLogRepository;
import com.effitrack.server.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class EquipmentSimulatorService {
    private final Random random = new Random();
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private EquipmentLogRepository logRepository;

    @Scheduled(fixedDelay = Constants.SCHEDULE_DELAY_MS)
    @Transactional
    public void simulateActivity() {
        List<Equipment> allEquipment = equipmentRepository.findAll();

        for (Equipment equipment : allEquipment) {
            if (random.nextInt(Constants.PROBABILITY_BOUND) < Constants.STATUS_CHANGE_CHANCE) {
                changeToRandomStatus(equipment);
            }
        }
    }

    private void changeToRandomStatus(Equipment equipment) {
        LocalDateTime now = LocalDateTime.now();

        EquipmentLog currentLog = logRepository.findFirstByEquipmentIdAndEndTimeIsNullOrderByStartTimeDesc(equipment.getId())
                .orElse(null);

        if (currentLog != null) {
            currentLog.setEndTime(now);
            logRepository.save(currentLog);
        }

        EquipmentStatus newStatus = pickNewRandomStatus(equipment.getStatus());
        String reason = null;

        if (newStatus == EquipmentStatus.DOWNTIME) {
            reason = Constants.DOWNTIME_REASONS.get(random.nextInt(Constants.DOWNTIME_REASONS.size()));
        } else if (newStatus == EquipmentStatus.SETUP) {
            reason = Constants.SETUP_REASONS.get(random.nextInt(Constants.SETUP_REASONS.size()));
        }

        EquipmentLog newLog = new EquipmentLog();
        newLog.setEquipment(equipment);
        newLog.setStatus(newStatus);
        newLog.setReason(reason);
        newLog.setStartTime(now);
        logRepository.save(newLog);

        equipment.setStatus(newStatus);
        equipment.setLastStatusChange(now);
        equipment.setActiveAction(reason);
        equipmentRepository.save(equipment);
    }

    private EquipmentStatus pickNewRandomStatus(EquipmentStatus currentStatus) {
        EquipmentStatus[] allStatuses = EquipmentStatus.values();
        EquipmentStatus newStatus;
        do {
            newStatus = allStatuses[random.nextInt(allStatuses.length)];
        } while (newStatus == currentStatus);
        return newStatus;
    }

    private static class Constants {
        public static final long SCHEDULE_DELAY_MS = 60000L;
        public static final int PROBABILITY_BOUND = 100;
        public static final int STATUS_CHANGE_CHANCE = 20;

        public static final List<String> DOWNTIME_REASONS = List.of(
                "Відсутність живлення (перебій в електромережі)",
                "Відсутність сировини / заготовок",
                "Збій програмного забезпечення MES/ERP",
                "Аварійна зупинка оператором (E-Stop)",
                "Перегрів системи / спрацювання термозахисту",
                "Механічне заклинювання рухомих частин",
                "Відмова пневматичної системи",
                "Падіння тиску в гідравліці",
                "Помилка датчиків позиціонування",
                "Очікування прибуття ремонтного персоналу",
                "Зношення або раптова поломка робочого інструменту",
                "Невідповідність температурного режиму в цеху",
                "Помилка в програмі керування (G-code / ПЛК)",
                "Очікування підтвердження від відділу якості (ВТК)",
                "Спрацювання системи безпеки (відкриті дверцята)",
                "Втрата зв'язку з центральним сервером",
                "Критичний рівень вібрації шпинделя/приводу",
                "Відмова системи змащення напрямних",
                "Витік охолоджувальної рідини (ЗОР)",
                "Заклинювання стружкового або стрічкового конвеєра",
                "Коротке замикання в силовому щиті",
                "Асинхронна робота сервоприводів",
                "Перевантаження головного двигуна",
                "Збій системи вентиляції / витяжки",
                "Відмова оптичної лінійки (енкодера)",
                "Несанкціоноване втручання в зону роботи",
                "Пошкодження кріпильної оснастки",
                "Втрата калібрувальних даних",
                "Помилка зчитування RFID/штрихкоду партії",
                "Відсутність оператора на робочому місці"
        );

        public static final List<String> SETUP_REASONS = List.of(
                "Планове технічне обслуговування (ТО)",
                "Заміна та налаштування робочого інструменту",
                "Переналаштування під нову партію деталей",
                "Очищення робочої зони та механізмів",
                "Калібрування датчиків та нулювання осей",
                "Завантаження та тестування нової програми",
                "Виготовлення тестового (першого) зразка",
                "Перевірка геометрії та точності обладнання",
                "Заміна витратних матеріалів (мастило, фільтри)",
                "Інструктаж та зміна операторів",
                "Прогрів обладнання перед початком зміни",
                "Синхронізація з роботизованим маніпулятором",
                "Юстирування вимірювальних систем",
                "Зміна затискних кулачків або фіксаторів",
                "Налаштування швидкості конвеєрної стрічки",
                "Доливання технічних рідин",
                "Тестування системи аварійного гальмування",
                "Прив'язка нових координатних баз",
                "Перевірка системи подачі стисненого повітря",
                "Очищення оптичних сенсорів",
                "Резервне копіювання даних ПЛК"
        );
    }
}
