package com.queries.events;

import com.queries.models.Problem;
import org.springframework.context.ApplicationEvent;

/**
 * @author wanghongen
 * 2019-05-22
 */
public class ProblemEvent extends ApplicationEvent {

    public ProblemEvent(Problem source) {
        super(source);
    }

    @Override
    public Problem getSource() {
        return (Problem) super.getSource();
    }

}
