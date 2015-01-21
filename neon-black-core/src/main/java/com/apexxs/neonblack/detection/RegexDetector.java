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
 * RegexDetector.java
 * This class uses components from the Open Sextant project which is
 * also licensed under the Apache License, Version 2.0
 * http://www.opensextant.org
 * ====================================================================
 */

package com.apexxs.neonblack.detection;

import com.apexxs.neonblack.dao.DetectedLocation;
import com.apexxs.neonblack.setup.Configuration;
import org.opensextant.extraction.TextMatch;
import org.opensextant.extractors.flexpat.TextMatchResult;
import org.opensextant.extractors.xcoord.GeocoordMatch;
import org.opensextant.extractors.xcoord.XCoord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class RegexDetector implements NERDetector
{

//    protected static String DEFAULT_XCOORD_CFG = "/geocoord_patterns.cfg";
    protected static String DEFAULT_XCOORD_CFG = "geocoord_patterns.cfg";
    public final static Logger logger = LoggerFactory.getLogger(RegexDetector.class);

    private Configuration config = Configuration.getInstance();

    public RegexDetector() {}

    public List<DetectedLocation> extractLocationNames(String text)
    {
        List<DetectedLocation> locs = new ArrayList<>();
        try
        {
//            URL url = RegexDetector.class.getResource(DEFAULT_XCOORD_CFG);
            URL url = new File(config.getResourceDirectory() + DEFAULT_XCOORD_CFG).toURI().toURL();
            XCoord xc = new XCoord(false);
            xc.configure(url);

            TextMatchResult results = xc.extract_coordinates(text, "id");
            if (!results.matches.isEmpty())
            {
                for (TextMatch textMatch : results.matches)
                {
                    GeocoordMatch geocoordMatch = (GeocoordMatch) textMatch;
                    DetectedLocation detectedLocation = new DetectedLocation(geocoordMatch);
                    locs.add(detectedLocation);
                }
            }
        }
        catch (Exception ex)
        {
            logger.error("Error in RegexDetector: " + ex.getMessage());
        }

        return locs;
    }
}
