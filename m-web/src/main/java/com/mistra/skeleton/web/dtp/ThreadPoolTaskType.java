package com.mistra.skeleton.web.dtp;

/**
 * @author rui.wang
 * @ Version: 1.0
 * @ Time: 2023/5/12 19:04
 * @ Description:
 */
public enum ThreadPoolTaskType {

    IO("IO密集型"), CPU("CPU密集型"), PRIORITY("优先级"), FUTURE("Future任务");

    /**
     * 描述
     */
    private final String desc;

    public String getDesc() {
        return this.desc;
    }

    ThreadPoolTaskType(String desc) {
        this.desc = desc;
    }
}
