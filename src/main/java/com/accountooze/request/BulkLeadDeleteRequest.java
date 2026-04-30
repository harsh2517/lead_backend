package com.accountooze.request;

import lombok.Data;

import java.util.List;

@Data
public class BulkLeadDeleteRequest {
    private List<Integer> ids;
}
