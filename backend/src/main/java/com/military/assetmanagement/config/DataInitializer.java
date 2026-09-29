package com.military.assetmanagement.config;

import com.military.assetmanagement.entity.Asset;
import com.military.assetmanagement.entity.Base;
import com.military.assetmanagement.entity.EquipmentType;
import com.military.assetmanagement.entity.Role;
import com.military.assetmanagement.entity.RoleName;
import com.military.assetmanagement.entity.User;
import com.military.assetmanagement.repository.AssetRepository;
import com.military.assetmanagement.repository.BaseRepository;
import com.military.assetmanagement.repository.EquipmentTypeRepository;
import com.military.assetmanagement.repository.RoleRepository;
import com.military.assetmanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final RoleRepository roleRepository;
    private final BaseRepository baseRepository;
    private final EquipmentTypeRepository equipmentTypeRepository;
    private final UserRepository userRepository;
    private final AssetRepository assetRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(
            RoleRepository roleRepository,
            BaseRepository baseRepository,
            EquipmentTypeRepository equipmentTypeRepository,
            UserRepository userRepository,
            AssetRepository assetRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.roleRepository = roleRepository;
        this.baseRepository = baseRepository;
        this.equipmentTypeRepository = equipmentTypeRepository;
        this.userRepository = userRepository;
        this.assetRepository = assetRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Initialize Roles
        Role adminRole = initRole(RoleName.ADMIN);
        Role commanderRole = initRole(RoleName.BASE_COMMANDER);
        Role logisticsRole = initRole(RoleName.LOGISTICS_OFFICER);

        // 2. Initialize Default Bases if empty
        Base baseAlpha;
        Base baseBravo;
        if (baseRepository.count() == 0) {
            baseAlpha = baseRepository.save(Base.builder()
                    .name("FOB Alpha")
                    .location("Sector 4 - Northern Frontier")
                    .build());
            baseBravo = baseRepository.save(Base.builder()
                    .name("Fort Bravo")
                    .location("Sector 7 - Western Hub")
                    .build());
            log.info("Initialized default military bases: FOB Alpha, Fort Bravo");
        } else {
            List<Base> existingBases = baseRepository.findAll();
            baseAlpha = !existingBases.isEmpty() ? existingBases.get(0) : null;
            baseBravo = existingBases.size() > 1 ? existingBases.get(1) : baseAlpha;
        }

        // 3. Initialize Default Equipment Types if empty
        EquipmentType eqFirearms;
        EquipmentType eqComms;
        if (equipmentTypeRepository.count() == 0) {
            eqFirearms = equipmentTypeRepository.save(EquipmentType.builder()
                    .name("Small Arms & Weapons")
                    .description("Tactical assault rifles, sidearms, and specialized firearms")
                    .build());
            eqComms = equipmentTypeRepository.save(EquipmentType.builder()
                    .name("Communications & Optics")
                    .description("Radio transceiver sets, encrypted links, and night-vision optics")
                    .build());
            equipmentTypeRepository.save(EquipmentType.builder()
                    .name("Munitions & Ordnance")
                    .description("Ammunition magazines, explosive ordnance, and mortars")
                    .build());
            log.info("Initialized default equipment categories");
        } else {
            List<EquipmentType> existingTypes = equipmentTypeRepository.findAll();
            eqFirearms = !existingTypes.isEmpty() ? existingTypes.get(0) : null;
            eqComms = existingTypes.size() > 1 ? existingTypes.get(1) : eqFirearms;
        }

        // 4. Initialize Default Users if empty
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(User.builder()
                    .username("admin")
                    .email("admin@defense.mil")
                    .password(passwordEncoder.encode("admin123"))
                    .role(adminRole)
                    .base(null)
                    .enabled(true)
                    .build());
            log.info("Initialized default user: admin");
        }

        if (!userRepository.existsByUsername("commander1") && baseAlpha != null) {
            userRepository.save(User.builder()
                    .username("commander1")
                    .email("commander1@defense.mil")
                    .password(passwordEncoder.encode("commander123"))
                    .role(commanderRole)
                    .base(baseAlpha)
                    .enabled(true)
                    .build());
            log.info("Initialized default user: commander1 (Assigned to {})", baseAlpha.getName());
        }

        if (!userRepository.existsByUsername("logistics1") && baseAlpha != null) {
            userRepository.save(User.builder()
                    .username("logistics1")
                    .email("logistics1@defense.mil")
                    .password(passwordEncoder.encode("logistics123"))
                    .role(logisticsRole)
                    .base(baseAlpha)
                    .enabled(true)
                    .build());
            log.info("Initialized default user: logistics1 (Assigned to {})", baseAlpha.getName());
        }

        // 5. Initialize Seed Assets if empty
        if (assetRepository.count() == 0 && baseAlpha != null && eqFirearms != null) {
            assetRepository.save(Asset.builder()
                    .name("M4A1 Tactical Carbine")
                    .equipmentType(eqFirearms)
                    .base(baseAlpha)
                    .quantity(100)
                    .assignedQuantity(0)
                    .build());

            assetRepository.save(Asset.builder()
                    .name("PRC-152 Tactical Radio")
                    .equipmentType(eqComms != null ? eqComms : eqFirearms)
                    .base(baseAlpha)
                    .quantity(40)
                    .assignedQuantity(0)
                    .build());

            if (baseBravo != null) {
                assetRepository.save(Asset.builder()
                        .name("M4A1 Tactical Carbine")
                        .equipmentType(eqFirearms)
                        .base(baseBravo)
                        .quantity(60)
                        .assignedQuantity(0)
                        .build());
            }
            log.info("Initialized default sample assets for testing");
        }
    }

    private Role initRole(RoleName roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName)));
    }
}
