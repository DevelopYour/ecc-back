package com.seoultech.ecc.admin.datamodel;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "setting")
public class SettingEntity {
    @Id
    @Column(name = "setting_key", nullable = false)
    private String settingKey;

    @Column(name = "setting_value", nullable = false)
    private String settingValue;

    @Column(name = "description")
    private String description;
}
