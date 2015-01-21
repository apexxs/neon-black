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
 *
 *
 * ====================================================================
 */

package com.apexxs.neonblack.scoring;

import com.apexxs.neonblack.dao.DetectedLocation;
import com.apexxs.neonblack.dao.GeoNamesEntry;
import org.apache.commons.lang.StringUtils;

import java.util.List;

public class Scorer
{
    public Scorer() {}

    public List<DetectedLocation> score(List<DetectedLocation> locs)
    {
        StringScorers ss = new StringScorers();

        for(DetectedLocation loc : locs)
        {
            if(StringUtils.equals(loc.detectedBy, "RGX")) continue;

            if(loc.geoNamesEntries.size() > 0)
            {
                String detectedName;
                if(!StringUtils.isEmpty(loc.synonym))
                {
                    detectedName = loc.synonym;
                }
                else
                {
                    detectedName = loc.name;
                }

                for(GeoNamesEntry entry : loc.geoNamesEntries)
                {
                    Float levenshtein = ss.getLevenshtien(detectedName, entry.name);
                    entry.setLevenshteinScore(levenshtein);
                }
            }
        }
        return locs;
    }
}
