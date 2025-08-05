package com.isxcode.torch.modules.datasource.source.impl;

import com.isxcode.torch.api.datasource.constants.DatasourceDriver;
import com.isxcode.torch.api.datasource.constants.DatasourceType;
import com.isxcode.torch.api.datasource.dto.ConnectInfo;
import com.isxcode.torch.api.datasource.dto.QueryColumnDto;
import com.isxcode.torch.api.datasource.dto.QueryTableDto;
import com.isxcode.torch.api.work.res.GetDataSourceDataRes;
import com.isxcode.torch.backend.api.base.exceptions.IsxAppException;
import com.isxcode.torch.backend.api.base.properties.IsxAppProperties;
import com.isxcode.torch.common.utils.aes.AesUtils;
import com.isxcode.torch.modules.datasource.service.DatabaseDriverService;
import com.isxcode.torch.modules.datasource.source.Datasource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class HanaService extends Datasource {

    public HanaService(DatabaseDriverService dataDriverService, IsxAppProperties isxAppProperties, AesUtils aesUtils) {
        super(dataDriverService, isxAppProperties, aesUtils);
    }

    @Override
    public String getDataSourceType() {
        return DatasourceType.HANA_SAP;
    }

    @Override
    public String getDriverName() {
        return DatasourceDriver.HANA_SAP_DRIVER;
    }

    @Override
    public List<QueryTableDto> queryTable(ConnectInfo connectInfo) {
        return Collections.emptyList();
    }

    @Override
    public List<QueryColumnDto> queryColumn(ConnectInfo connectInfo) {
        return Collections.emptyList();
    }

    @Override
    public Long getTableTotalSize(ConnectInfo connectInfo) {
        return 0L;
    }

    @Override
    public Long getTableTotalRows(ConnectInfo connectInfo) {
        return 0L;
    }

    @Override
    public Long getTableColumnCount(ConnectInfo connectInfo) {
        return 0L;
    }

    @Override
    public String getPageSql(String sql) throws IsxAppException {
        return "";
    }

    @Override
    public GetDataSourceDataRes getTableData(ConnectInfo connectInfo) {
        return null;
    }

    @Override
    public void refreshTableInfo(ConnectInfo connectInfo) {

    }
}
