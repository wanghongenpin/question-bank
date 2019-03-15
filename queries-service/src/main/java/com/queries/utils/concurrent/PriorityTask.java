package com.queries.utils.concurrent;

/**
 * @author wanghongen
 * 2018/5/12
 */
public class PriorityTask implements Runnable, Comparable<PriorityTask> {
    private int priority;

    public PriorityTask(int priority) {
        this.priority = priority;

    }

    @Override
    public int compareTo(PriorityTask o) {
        return Integer.compare(o.getPriority(), this.getPriority());
    }

    @Override
    public void run() {

    }

    public int getPriority() {
        return priority;
    }
}
