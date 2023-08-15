package com.bhowden.exoplanetworker.model;

import lombok.Data;

@Data
public class Exoplanet {
    private String name;
    private double mass;
    private double planetRadius;
    private double orbitalRadius;
    private double orbitalPeriod;
    private double eccentricity;
    private double inclination;
    private double longitudeOfNode;
    private double argumentOfPeriapsis;
    private double unixTime;
    private double distance;
    private double ra;
    private double declination;
    private double galacticLongitude;
    private double galacticLatitude;
    private int stayAlive;
}
