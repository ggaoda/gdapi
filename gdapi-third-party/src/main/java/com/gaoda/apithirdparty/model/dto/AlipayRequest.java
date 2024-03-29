package com.gaoda.apithirdparty.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AlipayRequest implements Serializable {
    private static final long serialVersionUID = -8597630489529830444L;

    private String traceNo;
    private double totalAmount;
    private String subject;
    private String alipayTraceNo;
}