package io.metersphere.sql.spi;

import io.metersphere.sql.config.DriverConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.sql.Driver;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DriverEntry  implements Serializable {
    private static final long serialVersionUID = 1L;

    private DriverConfig driverConfig;

    private Driver driver;

}