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
 * FinalLocation.java
 *
 * ====================================================================
 */

package com.apexxs.neonblack.dao;


import com.apexxs.neonblack.setup.Configuration;
import com.apexxs.neonblack.solr.Queries;

import org.apache.commons.lang.StringUtils;

import org.json.simple.JSONValue;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FinalLocation
{
    private Configuration config = Configuration.getInstance();
    public String name;
    public List<Integer> startPositions;
    public String detectedBy;
    public GeoNamesEntry topCandidate;
    public List<GeoNamesEntry> geoNamesEntries;

    public FinalLocation() {}

    public FinalLocation(DetectedLocation dloc)
    {
        this.name = dloc.name;
        this.startPositions = dloc.startPos;
        this.detectedBy = dloc.detectedBy;
        this.geoNamesEntries = dloc.geoNamesEntries;
        if(this.geoNamesEntries.size() > 0)
        {
            this.topCandidate = checkTopCandidate(this.geoNamesEntries);
        }
        else
        {
            this.topCandidate = new GeoNamesEntry();
            this.topCandidate.name = dloc.name;

        }

        if(StringUtils.equals(this.detectedBy, "RGX"))
        {
            if(this.topCandidate != null)
            {
                this.topCandidate.lonLat = String.valueOf(dloc.geocoordMatch.getLongitude()) + " " + String.valueOf(dloc.geocoordMatch.getLatitude());
                Queries queries = new Queries();
                this.geoNamesEntries = queries.geoNamesWithinDistanceOf(this.topCandidate.lonLat, config.getProximalDistance());
            }
        }
    }

    private GeoNamesEntry checkTopCandidate(List<GeoNamesEntry> entries)
    {
        GeoNamesEntry topCandidate;

        for(GeoNamesEntry entry : entries)
        {
            if(entry.getLevenshteinScore() == 1.0f)
            {
                if(entries.size() != 1)
                {
                    topCandidate = entry;
                    entries.remove(entry);
                    return topCandidate;
                }
            }
        }
        if(entries.size() != 1)
        {
            topCandidate = entries.get(0);
            entries.remove(0);
        }
        else
        {
            topCandidate = entries.get(0);
        }
        return topCandidate;
    }

    public String prettyPrint()
    {
        String sep = System.getProperty("line.separator");
        StringBuilder sb = new StringBuilder();

            sb.append("Detection: " + this.name + "\t");
            sb.append("Gazetteer name: " + this.topCandidate.name + "\t");
            sb.append("LonLat: " + this.topCandidate.lonLat + "\t");
            sb.append("Country: " + this.topCandidate.countryCode + "\t");
            sb.append("Feature code: " + this.topCandidate.featureCode + "\t");
            sb.append("Admin1: " + this.topCandidate.admin1 + "\t");
            sb.append("Admin2: " + this.topCandidate.admin2 + "\t");
            sb.append("Num Occurrences: " + this.startPositions.size() + sep);

            if(this.geoNamesEntries.size() > 1)
            {
                Integer minimum = 0;
                String altLabel = StringUtils.EMPTY;

                if (StringUtils.equals(this.detectedBy, "NER"))
                {
                    if (geoNamesEntries.size() > 1)
                    {
                        minimum = Math.min(geoNamesEntries.size(), config.getNumAlternates());
                    }
                    altLabel = "Alternate:\t";
                }
                else if (StringUtils.equals(this.detectedBy, "RGX"))
                {
                    minimum = Math.min(config.getNumProximalHits(), geoNamesEntries.size());
                    altLabel = "Within " + config.getProximalDistance() + " Km\t";
                }

                for (int i = 0; i < minimum; i++)
                {
                    //GeoNamesEntry entry = this.geoNamesEntries.get(i);
                    sb.append("\t" + altLabel + " " + geoNamesEntries.get(i).toString() + sep);
                }
            }

        return  sb.toString();
    }

    public String toJson()
    {
        Map<String, Object> detectionMap = new LinkedHashMap<>();
        List<Object> alternateList = new LinkedList<>();
        try
        {
            detectionMap.put("Detection", this.name);
            detectionMap.put("Gazetteer name", this.topCandidate.name);
            detectionMap.put("LonLat", this.topCandidate.lonLat);
            detectionMap.put("Country", this.topCandidate.countryCode);
            detectionMap.put("Feature code", this.topCandidate.featureCode);
            detectionMap.put("Admin1", this.topCandidate.admin1);
            detectionMap.put("Admin2", this.topCandidate.admin2);
            detectionMap.put("Num Occurrences", this.startPositions.size());

            if (this.geoNamesEntries.size() > 1)
            {
                Integer minimum = 0;
                String altLabel = StringUtils.EMPTY;

                if (StringUtils.equals(this.detectedBy, "NER"))
                {
                    if (geoNamesEntries.size() > 1)
                    {
                        minimum = Math.min(geoNamesEntries.size(), config.getNumAlternates());
                    }
                    altLabel = "Alternates";
                }
                else if (StringUtils.equals(this.detectedBy, "RGX"))
                {
                    minimum = Math.min(config.getNumProximalHits(), geoNamesEntries.size());
                    altLabel = "Within " + config.getProximalDistance() + " Km";
                }
                for (int i = 0; i < minimum; i++)
                {
                    Map<String, String> alternateMap = new LinkedHashMap<>();
                    GeoNamesEntry entry = this.geoNamesEntries.get(i);
                    alternateMap.put("Name", entry.name);
                    alternateMap.put("LonLat", entry.lonLat);
                    alternateMap.put("Country", entry.countryCode);
                    alternateMap.put("Feature code", entry.featureCode);
                    alternateMap.put("Admin1", entry.admin1);
                    alternateMap.put("Admin2", entry.admin2);
                    alternateList.add(alternateMap);
                }
                detectionMap.put(altLabel, alternateList);
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return JSONValue.toJSONString(detectionMap);
    }
}
