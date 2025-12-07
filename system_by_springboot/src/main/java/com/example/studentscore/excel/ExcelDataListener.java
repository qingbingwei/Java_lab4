package com.example.studentscore.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * 通用Excel读取监听器
 * @param <T> Excel数据类型
 */
@Slf4j
public class ExcelDataListener<T> extends AnalysisEventListener<T> {

    /**
     * 每隔BATCH_COUNT条存储数据库，然后清理list，方便内存回收
     */
    private static final int BATCH_COUNT = 500;

    /**
     * 缓存的数据
     */
    private List<T> cachedDataList = new ArrayList<>(BATCH_COUNT);

    /**
     * 数据处理器
     */
    private final Consumer<List<T>> consumer;

    /**
     * 是否批量处理
     */
    private final boolean batchProcess;

    /**
     * 所有数据
     */
    private final List<T> allDataList = new ArrayList<>();

    public ExcelDataListener(Consumer<List<T>> consumer) {
        this(consumer, true);
    }

    public ExcelDataListener(Consumer<List<T>> consumer, boolean batchProcess) {
        this.consumer = consumer;
        this.batchProcess = batchProcess;
    }

    /**
     * 每一条数据解析都会来调用
     */
    @Override
    public void invoke(T data, AnalysisContext context) {
        cachedDataList.add(data);
        allDataList.add(data);
        if (batchProcess && cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList = new ArrayList<>(BATCH_COUNT);
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (batchProcess && !cachedDataList.isEmpty()) {
            saveData();
        } else if (!batchProcess && !allDataList.isEmpty()) {
            consumer.accept(allDataList);
        }
        log.info("所有数据解析完成，共 {} 条", allDataList.size());
    }

    /**
     * 存储数据
     */
    private void saveData() {
        log.info("开始存储数据，共 {} 条", cachedDataList.size());
        consumer.accept(cachedDataList);
        log.info("存储数据完成");
    }

    /**
     * 获取所有数据
     */
    public List<T> getAllDataList() {
        return allDataList;
    }
}
