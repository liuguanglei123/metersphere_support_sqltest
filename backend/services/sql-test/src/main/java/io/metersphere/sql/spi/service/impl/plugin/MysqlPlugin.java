package io.metersphere.sql.spi.service.impl.plugin;

import io.metersphere.sql.config.DBConfig;
import io.metersphere.sql.spi.service.DBManage;
import io.metersphere.sql.spi.service.MetaData;
import io.metersphere.sql.spi.service.Plugin;
import io.metersphere.sql.spi.service.impl.dbManage.MysqlDBManage;
import io.metersphere.sql.spi.service.impl.metaDataImpl.MysqlMetaData;
import io.metersphere.sql.utils.FileUtils;

public class MysqlPlugin implements Plugin {

    @Override
    public DBConfig getDBConfig() {
        return FileUtils.readJsonValue(this.getClass(),"mysql.json", DBConfig.class);
    }

    @Override
    public MetaData getMetaData() {
        return new MysqlMetaData();
    }

    @Override
    public DBManage getDBManage() {
        return new MysqlDBManage();
    }
}