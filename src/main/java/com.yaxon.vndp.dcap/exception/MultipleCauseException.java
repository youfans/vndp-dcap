package com.yaxon.vndp.dcap.exception;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Author: 游锋锋
 * Time: 2016-03-01 16:42
 * Copyright (C) 2016 Xiamen Yaxon Networks CO.,LTD.
 */
public class MultipleCauseException extends Throwable {
    private List<Throwable> causes = Collections.synchronizedList(new ArrayList<Throwable>());

    public MultipleCauseException(){}

    public MultipleCauseException(List<Throwable> causes) {
        if (!(causes == null || causes.isEmpty())) this.causes.addAll(causes);
    }

    public void add(Throwable cause) {
        this.causes.add(cause);
    }

    public List<Throwable> getCauses() {
        return new ArrayList<Throwable>(this.causes);
    }


    @Override
    public void printStackTrace(PrintStream s) {
        for (Throwable cause : causes) {
            cause.printStackTrace(s);
        }
    }

    @Override
    public void printStackTrace(PrintWriter w) {
        for (Throwable cause : causes) {
            cause.printStackTrace(w);
        }
    }
}
