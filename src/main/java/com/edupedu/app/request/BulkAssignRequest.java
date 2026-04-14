package com.edupedu.app.request;


import java.util.List;

public record BulkAssignRequest(List<Long> studentIds, Long classGroupId) {
        }