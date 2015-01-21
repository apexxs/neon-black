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
 * Searcher.java
 *
 * ====================================================================
 */

package com.apexxs.neonblack.solr;


import com.apexxs.neonblack.dao.BorderData;
import com.apexxs.neonblack.dao.DetectedLocation;
import com.apexxs.neonblack.dao.GeoNamesEntry;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class Searcher
{
    public Searcher()
    {
    }

    public List<DetectedLocation> search(List<DetectedLocation> locs)
    {
        Queries queries = new Queries();

        for (DetectedLocation loc : locs)
        {
            if (loc.detectedBy.equals("NER"))
            {
                List<GeoNamesEntry> geonames = new ArrayList<>();
                String queryString = getQueryString(loc);

                String[] tokens = StringUtils.split(loc.name, " ");

                if (tokens.length == 1)
                {
                    geonames = queries.doSelectQuery(queryString);
                }
                else if (tokens.length > 1)
                {
                    geonames = queries.doNameQuery(queryString);
                }

                if (geonames.size() == 0 && loc.hints != null)
                {
                    loc.hints = null;
                    queryString = getQueryString(loc);
                    if (tokens.length == 1)
                    {
                        geonames = queries.doSelectQuery(queryString);
                    }
                    else if (tokens.length > 1)
                    {
                        geonames = queries.doNameQuery(queryString);
                    }
                }
                if (geonames.size() > 0)
                {
                    loc.geoNamesEntries.addAll(geonames);
                }
            }
            else if (loc.detectedBy.equals("RGX"))
            {
                double lat = loc.geocoordMatch.getLatitude();
                double lon = loc.geocoordMatch.getLongitude();
                String lonLat = lon + " " + lat;
                List<BorderData> results = queries.doBorderQuery(lonLat);

                GeoNamesEntry entry = new GeoNamesEntry();
                for (BorderData bd : results)
                {
                    if (StringUtils.equals(bd.shapeType, "adm0"))
                    {
                        entry.countryCode = bd.iso2;
                    }
                    else if (StringUtils.equals(bd.shapeType, "adm1"))
                    {
                        entry.admin1 = bd.name;
                    }
                    else if (StringUtils.equals(bd.shapeType, "adm2"))
                    {
                        entry.admin2 = bd.name;
                    }
                }
                entry.name = loc.name;
                entry.score = 1.0f;
                entry.setLevenshteinScore(1.0f);
                loc.geoNamesEntries.add(entry);
            }
        }
        return locs;
    }

    private String getQueryString(DetectedLocation loc)
    {
        String queryString = "allNames:";

        if (!StringUtils.isEmpty(loc.synonym))
        {
            queryString += loc.synonym;
        }
        else
        {
            queryString += loc.name;
        }
        if (loc.hints != null)
        {
            for (String hint : loc.hints)
            {
                queryString += " AND " + hint;
            }
        }
        return queryString;
    }
}
