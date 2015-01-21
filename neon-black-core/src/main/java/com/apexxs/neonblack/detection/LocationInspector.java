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
 * LocationInspector.java
 *
 * ====================================================================
 */

package com.apexxs.neonblack.detection;


import com.apexxs.neonblack.dao.DetectedLocation;
import com.apexxs.neonblack.utilities.Hints;
import com.apexxs.neonblack.utilities.ISOUtilities;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class LocationInspector
{
    private ISOUtilities isoUtilities = ISOUtilities.getInstance();
    private Map<Integer, String> countryPositions = new TreeMap<>();
    private Map<Integer, String> statePositions = new TreeMap<>();

    public LocationInspector()
    {
    }

    public List<DetectedLocation> inspectLocations(List<DetectedLocation> locations, String text)
    {
        for (DetectedLocation loc : locations)
        {
            String countryCode;
            String stateCode;
            countryCode = isItACountry(loc.name);
            stateCode = isItAState(loc.name);

            if(StringUtils.equals("RGX", loc.detectedBy)) continue;

            if (!StringUtils.isEmpty(countryCode) && !StringUtils.isEmpty(stateCode))
            {
                loc.hints = new ArrayList<>();
                loc.hints.add("(countryCode:" + countryCode + " OR " + "admin1:" + stateCode + ")");
                //continue;
            }
            else
            {
                if(!StringUtils.isEmpty(countryCode))
                {
                    loc.hints = new ArrayList<>();
                    loc.hints.add("countryCode:" + countryCode);
                    loc.hints.add("featureCode:PCLI");
                    loc.countryCode = countryCode;
                    for (Integer pos : loc.startPos)
                    {
                        countryPositions.put(pos, countryCode);
                    }
                }
                else if(!StringUtils.isEmpty(stateCode))
                {
                    loc.hints = new ArrayList<>();
                    loc.hints.add(("admin1:" + stateCode));
                    loc.countryCode = "US";
                    loc.stateCode = stateCode;
                    for (Integer pos : loc.startPos)
                    {
                        statePositions.put(pos, stateCode);
                    }
                }
            }

            if (loc.hints == null)
            {
                String hint = anyHints(loc.name);
                if (!StringUtils.isEmpty(hint))
                {
                    loc.hints = new ArrayList<>();
                    loc.hints.add(hint);
                    if(StringUtils.endsWithIgnoreCase(loc.name, "province"))
                    {
                        loc.synonym = loc.name.replaceAll("(?i)province", "").trim();
                    }
                    if(StringUtils.endsWithIgnoreCase(loc.name, "district"))
                    {
                        loc.synonym = loc.name.replaceAll("(?i)district", "").trim();
                    }
                    if(StringUtils.endsWithIgnoreCase(loc.name, "village"))
                    {
                        loc.synonym = loc.name.replaceAll("(?i)village", "").trim();
                    }
                }
            }
        }
        for (DetectedLocation loc : locations)
        {
            loc = checkProximity(loc);
            loc = checkForBigramType1(loc);
            loc = checkForBigramType2(loc, text);
        }

        return locations;
    }

    private String isItACountry(String name)
    {
        String tempName;
        if(name.length() > 3)  //NOT ISO2, ISO3 or FIPS
        {
            tempName = name.toLowerCase();
        }
        else
        {
            tempName = name;
        }
        String countryCode = isoUtilities.getCountryCodes().get(tempName);
        if(!StringUtils.isEmpty(countryCode))
        {
            if(StringUtils.equals(countryCode, "AND"))
            {
                countryCode = "\\AND";
            }
        }
        return countryCode;
    }

    private String isItAState(String name)
    {
        String tempName;
        if(name.length() > 2)
        {
            tempName = name.toLowerCase();
        }
        else
        {
            tempName = name;
        }
        String stateCode = isoUtilities.getStateCodes().get(tempName);
        if(!StringUtils.isEmpty(stateCode))
        {
            if(StringUtils.equals(stateCode, "OR"))
            {
                stateCode = "\\OR";
            }
        }
        return stateCode;
    }

    private String anyHints(String name)
    {
        Hints hints = new Hints();
        return hints.containsHint(name);
    }

    private DetectedLocation checkProximity(DetectedLocation location)
    {
        if (location.hints == null && countryPositions.size() > 0)
        {
            for (Integer pos : location.stopPos)
            {
                for (Map.Entry<Integer, String> entry : countryPositions.entrySet())
                {
                    if (pos >= entry.getKey() - 3  && pos < entry.getKey())
                    {
                        location.hints = new ArrayList<>();
                        location.hints.add("countryCode:" + entry.getValue());
                        break;
                    }
                }
            }
        }
        if (location.hints == null && statePositions.size() > 0)
        {
            for (Integer pos : location.stopPos)
            {
                for (Map.Entry<Integer, String> entry : statePositions.entrySet())
                {
                    if (pos >= entry.getKey() - 3 && pos < entry.getKey())
                    {
                        location.hints = new ArrayList<>();
                        location.hints.add("admin1:" + entry.getValue());
                    }
                }
            }
        }
        return location;
    }

    private DetectedLocation checkForBigramType1(DetectedLocation loc)
    {
        String countryCode;
        String stateCode;
        if (loc.hints == null)
        {
            String[] tokens = loc.name.split(" ");
            if (tokens.length > 1)
            {
                String possBigram = tokens[tokens.length - 1];
                if ((possBigram.length() == 2 || possBigram.length() == 3) && StringUtils.isAllUpperCase(possBigram))
                {
                    countryCode = isItACountry(possBigram);
                    stateCode = isItAState(possBigram);
                    if (!StringUtils.isEmpty(countryCode) && !StringUtils.isEmpty(stateCode))
                    {
                        loc.hints = new ArrayList<>();
                        loc.hints.add("(countryCode:" + countryCode + " OR " + "admin1:" + stateCode + ")");
                    }
                    else if(!StringUtils.isEmpty(countryCode))
                    {
                        loc.hints = new ArrayList<>();
                        loc.hints.add("countryCode:" + countryCode);
                    }
                    else if(!StringUtils.isEmpty(stateCode))
                    {
                        loc.hints = new ArrayList<>();
                        loc.hints.add("admin1:" + stateCode);
                    }
                }
                if(loc.hints != null)
                {
                    loc.name = loc.name.replace(possBigram, "").trim();
                }
            }
        }
        return loc;
    }

    private DetectedLocation checkForBigramType2(DetectedLocation loc, String text)
    {
        if (loc.hints == null)
        {
            String countryCode;
            String stateCode;
            for(Integer stop : loc.stopPos)
            {
                if(text.length() >= stop + 5)
                {
                    String subString = text.substring(stop, stop + 5);
                    subString = subString.replaceAll("\\p{Punct}", "").trim();
                    String[] tokens = subString.split(" ");
                    String possBigram= tokens[0];
                    if ((possBigram.length() == 2 || possBigram.length() == 3) && StringUtils.isAllUpperCase(possBigram))
                    {
                        countryCode = isItACountry(possBigram);
                        stateCode = isItAState(possBigram);
                        if (!StringUtils.isEmpty(countryCode) && !StringUtils.isEmpty(stateCode))
                        {
                            loc.hints = new ArrayList<>();
                            loc.hints.add("(countryCode:" + countryCode + " OR " + "admin1:" + stateCode + ")");
                            //return loc;
                        }
                        else if(!StringUtils.isEmpty(countryCode))
                        {
                            loc.hints = new ArrayList<>();
                            loc.hints.add("countryCode:" + countryCode);
                        }
                        else if(!StringUtils.isEmpty(stateCode))
                        {
                            loc.hints = new ArrayList<>();
                            loc.hints.add("admin1:" + stateCode);
                        }
                    }
                }
            }
        }
        return loc;
    }
}
