package com.loopers.interfaces.api.points;

public class PointsV1Dto {
    public record PointsResponse(Long points) {
    }

    public record PointsChargeRequest(Long amount) {
    }
}
