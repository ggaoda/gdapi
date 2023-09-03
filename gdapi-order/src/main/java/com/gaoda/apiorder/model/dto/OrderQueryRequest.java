package com.gaoda.apiorder.model.dto;

import com.gundam.gdapicommon.common.PageRequest;
import lombok.Data;


import java.io.Serializable;

@Data
public class OrderQueryRequest extends PageRequest implements Serializable {
    private String type;
}
