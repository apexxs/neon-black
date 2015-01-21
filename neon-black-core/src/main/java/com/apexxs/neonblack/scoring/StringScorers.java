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
 * StringScorers.java
 *
 * ====================================================================
 */

package com.apexxs.neonblack.scoring;


import com.apexxs.neonblack.dao.DetectedLocation;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;

public class StringScorers
{
    public Float getLevenshtien(String s1, String s2)
    {
        Integer ldScore = StringUtils.getLevenshteinDistance(s1.toLowerCase(), s2.toLowerCase());
        Float ld = (float) ldScore;

        int maxLength = Math.max(s1.length(), s2.length());

        ld = 1.0f - (ld / (float) maxLength);
        return ld;
    }

    public Float getNormLev(String s1, String s2)
    {
        return (float) s1.length() / (float) s2.length();
    }

    public void getDocumentCountryContext(List<DetectedLocation> locs)
    {
        HashMap<String, Integer> contextHashMap = new HashMap<>();
        for(DetectedLocation loc : locs)
        {
             if(!StringUtils.isEmpty(loc.countryCode))
             {
                if(!contextHashMap.containsKey(loc.countryCode))
                {
                    contextHashMap.put(loc.countryCode, loc.startPos.size());
                }
                else
                {
                    contextHashMap.put(loc.countryCode, contextHashMap.get(loc.countryCode) + loc.startPos.size());
                }
             }
        }
    }
}
