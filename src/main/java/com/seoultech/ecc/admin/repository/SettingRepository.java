package com.seoultech.ecc.admin.repository;

import com.seoultech.ecc.admin.datamodel.SettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingRepository extends JpaRepository<SettingEntity, Integer> {
    SettingEntity findBySettingKey(String settingKey);

}
