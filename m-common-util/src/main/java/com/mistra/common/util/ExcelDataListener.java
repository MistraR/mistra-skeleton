package com.mistra.common.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Cell;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2022/12/7 17:10
 * @ Description:
 */
@Slf4j
public class ExcelDataListener<T> extends AnalysisEventListener<T> {

    @Getter
    LinkedHashMap<Integer, T> map = new LinkedHashMap<>();

    @Getter
    private int totalNum = 0;

    /**
     * 读取excel文件的最大行数
     */
    private final int maxRow;

    public ExcelDataListener(int maxRow) {
        this.maxRow = maxRow;
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        Map<Integer, Cell> cellMap = context.readRowHolder().getCellMap();
        if (MapUtils.isEmpty(cellMap)) {
            return;
        }
        map.put(context.readRowHolder().getRowIndex() + 1, data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
    }

    /**
     * 遇空白行停止读取
     *
     * @param context AnalysisContext
     * @return boolean
     */
    @Override
    public boolean hasNext(AnalysisContext context) {
        totalNum = map.size();
        if (totalNum > maxRow) {
            return false;
        }
        Map<Integer, Cell> cellMap = context.readRowHolder().getCellMap();
        return MapUtils.isNotEmpty(cellMap);
    }
}