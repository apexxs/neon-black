/*
 * Copyright (c) 2014.
 * Apex Expert Solutions
 * http://www.apexxs.com
 *
 *  ====================================================================
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 * ====================================================================
 *
 * DetectedLocation.java
 *
 * ====================================================================
 */

package com.apexxs.neonblack.dao;


import org.apache.commons.lang.builder.HashCodeBuilder;
import org.opensextant.extractors.xcoord.GeocoordMatch;

import java.util.ArrayList;
import java.util.List;

public class DetectedLocation
{
    public String detectedBy;
    public String name;
    public String synonym;
    public String countryCode;
    public String stateCode;
    public List<Integer> startPos = new ArrayList<>();
    public List<Integer> stopPos = new ArrayList<>();
    public GeocoordMatch geocoordMatch = null;
    public List<String> hints = null;
    public List<GeoNamesEntry> geoNamesEntries = new ArrayList<>();

    public DetectedLocation() {}

    public DetectedLocation(String name, int start, int stop)
    {
       this.name = name.replace("\n", " ").trim();
       this.detectedBy = "NER";
       this.startPos.add(start);
       this.stopPos.add(stop);
    }

    public DetectedLocation(GeocoordMatch m)
    {
        this.geocoordMatch = m;
        this.name = m.getText().replace("\n", " ").trim();
        this.startPos.add(m.start);
        this.stopPos.add(m.end);
        this.detectedBy = "RGX";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DetectedLocation that = (DetectedLocation) o;

        return !(name != null ? !name.equals(that.name) : that.name != null);
    }


    @Override
    public int hashCode()
    {
        HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(name);
        return builder.toHashCode();
    }
}
