package org.kevoree.registry.server.util;

import org.kevoree.modeling.api.trace.ModelTrace;
import org.kevoree.modeling.api.trace.TraceSequence;
import org.kevoree.modeling.api.util.ActionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leiko on 18/12/14.
 */
public class TraceHelper {

    private static final Logger log = LoggerFactory.getLogger(TraceHelper.class.getSimpleName());

    /**
     * Filters input traces to only keep traces related to Packages and Repositories
     * @param seq
     * @return
     */
    public static List<ModelTrace> merge(TraceSequence seq) {
        List<ModelTrace> mergeTraces = new ArrayList<ModelTrace>();
        for (ModelTrace trace : seq.getTraces()) {
            if (trace.getSrcPath().startsWith("packages") ||
                    trace.getSrcPath().startsWith("repositories") ||
                    trace.getSrcPath().startsWith("/repositories") ||
                    trace.getSrcPath().startsWith("/packages") ||
                    (trace.getRefName().equals("packages") && (trace.getSrcPath().equals("/") || trace.getSrcPath().equals("")))) {
                mergeTraces.add(trace);
            }
        }
        return mergeTraces;
    }

    /**
     * Filters input traces to delete non-REMOVE or non-REMOVE-ALL ActionType traces
     * @param seq
     * @return
     */
    public static List<ModelTrace> delete(TraceSequence seq) {
        List<ModelTrace> delTraces = new ArrayList<ModelTrace>();
        logTraces(seq.getTraces());
        for (ModelTrace trace : seq.getTraces()) {
            if (trace.getTraceType().equals(ActionType.REMOVE) || trace.getTraceType().equals(ActionType.REMOVE_ALL)) {
                delTraces.add(trace);
            }
        }
        return delTraces;
    }

    private static void logTraces(List<ModelTrace> traces) {
        log.info(">>>>>>>>>>>> LOG <<<<<<<<<<<<<");
        for (ModelTrace trace : traces) {
            log.info("{}", trace.getTraceType());
            log.info("ref: {}", trace.getRefName());
            log.info("src: {}", trace.getSrcPath());
            log.info("========================");
        }
        log.info(">>>>>>>>>>>> END <<<<<<<<<<<<<");
    }
}
