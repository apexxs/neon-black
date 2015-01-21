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
 * NeonBlack.java
 *
 * ====================================================================
 */

package com.apexxs.neonblack;

import com.apexxs.neonblack.dao.DetectedLocation;
import com.apexxs.neonblack.dao.FinalLocation;
import com.apexxs.neonblack.detection.CardinalDetector;
import com.apexxs.neonblack.detection.NERDetector;
import com.apexxs.neonblack.scoring.Scorer;
import com.apexxs.neonblack.setup.Configuration;
import com.apexxs.neonblack.solr.Searcher;
import com.apexxs.neonblack.utilities.TikaTextExtractor;
import org.apache.commons.lang.StringUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class NeonBlack
{
    private NERDetector detector = new CardinalDetector();
    private Searcher searcher = new Searcher();
    private Scorer scorer = new Scorer();
    private Configuration config = Configuration.getInstance();

    public NeonBlack() {}

    public List<FinalLocation> detectLocationsFromText(String text)
    {
        List<FinalLocation> result = new ArrayList<>();

        if(StringUtils.isEmpty(text))
        {
            return result;
        }

        List<DetectedLocation> detections = this.detector.extractLocationNames(text);
        if(detections.size() > 0)
        {
            detections = this.searcher.search(detections);
            this.scorer.score(detections);

            for(DetectedLocation detection : detections)
            {
                FinalLocation fl = new FinalLocation(detection);
                result.add(fl);
            }
        }

        return result;
    }

    public List<String> detectFromText(String text)
    {
        List<String> resultStrings = new ArrayList<>();
        if(StringUtils.isEmpty(text))
        {
            return resultStrings;
        }

        List<DetectedLocation> detections = this.detector.extractLocationNames(text);
        if(detections.size() > 0)
        {
            detections = this.searcher.search(detections);
            this.scorer.score(detections);

            for(DetectedLocation detection : detections)
            {
                FinalLocation fl = new FinalLocation(detection);
                if(StringUtils.equalsIgnoreCase(this.config.getResultFormat(), "json"))
                {
                    resultStrings.add(fl.toJson());
                }
                else
                {
                    resultStrings.add(fl.prettyPrint());
                }
            }
        }

        return resultStrings;
    }

    public List<String> detectFromFile(String fname)
    {
        TikaTextExtractor tte = new TikaTextExtractor();
        String text = tte.extractFromFile(fname);
        return this.detectFromText(text);
    }

    public  List<String> detectFromInputStream(InputStream is)
    {
        TikaTextExtractor tte = new TikaTextExtractor();
        String text = tte.extractTextFromInputStream(is);
        return this.detectFromText(text);
    }
}
