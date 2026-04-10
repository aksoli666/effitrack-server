package com.effitrack.server.service;

import com.effitrack.server.handler.EquipmentMetricsHandler;
import com.effitrack.server.model.Equipment;
import com.effitrack.server.model.User;
import com.effitrack.server.repository.EquipmentRepository;
import com.effitrack.server.repository.UserRepository;
import com.effitrack.server.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.effitrack.server.constant.StringConst.USER_NOT_FOUND_TEMPLATE_INV;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private EquipmentRepository equipmentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private EquipmentMetricsHandler metricsHandler;

    public boolean existsByTableNumber(String tableNumber) {
        return userRepository.existsByTableNumber(tableNumber);
    }

    public User createUser(User user) {
        user.setPinCode(passwordEncoder.encode(user.getPinCode()));
        return userRepository.save(user);
    }

    public List<Equipment> getUserEquipment(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            List<Equipment> equipmentList = user.get().getMonitoredEquipment();
            equipmentList.forEach(metricsHandler::calculateDynamicFields);
            return equipmentList;
        }
        return List.of();
    }

    public boolean addEquipmentToUser(Long userId, String inventoryNumber) {
        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Equipment> equipOpt = equipmentRepository.findByInventoryNumber(inventoryNumber);

        if (userOpt.isPresent() && equipOpt.isPresent()) {
            User user = userOpt.get();
            Equipment equipment = equipOpt.get();

            if (!user.getMonitoredEquipment().contains(equipment)) {
                user.getMonitoredEquipment().add(equipment);
                userRepository.save(user);
            }
            return true;
        }
        return false;
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByTableNumber(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_TEMPLATE_INV + username));
        return UserDetailsImpl.build(user);
    }
}
