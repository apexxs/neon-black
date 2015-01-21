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
 *  CardinalDetector.java
 *
 *  This class uses the Stanford Named Entity Recognizer.
 *  The Stanford Named Entity Recognizer is licensed under the
 *  GNU General Public License, version 2
 *  http://www.gnu.org/licenses/gpl-2.0.html
 *
 *  This class also uses the demonym list obtained from the Clavin NERD
 *  project by Bericho Technologies, and is also licensed under the Apache License,
 *  version 2.
 *  https://github.com/Berico-Technologies/CLAVIN-NERD
 *
 * ====================================================================
 */

package com.apexxs.neonblack.detection;


import com.apexxs.neonblack.dao.DetectedLocation;
import com.apexxs.neonblack.setup.Configuration;
import com.apexxs.neonblack.utilities.TextUtilities;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.Triple;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.*;

public class CardinalDetector implements NERDetector
{
    private AbstractSequenceClassifier<CoreMap> classifier;
    private Configuration config = Configuration.getInstance();
    private TextUtilities textUtilities = new TextUtilities();

    public CardinalDetector()
    {
        try
        {
            File modelFile = new File(config.getResourceDirectory() + config.getStanfordModel());

            this.classifier = CRFClassifier.getClassifier(modelFile);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public List<DetectedLocation> extractLocationNames(String text)
    {
        if (config.removeStopWords())
        {
            text = textUtilities.cleanStopwords(text);
        }

        text = StringUtils.normalizeSpace(text);

        Map<String, DetectedLocation> nerResults = new HashMap<>();

        RegexDetector regexExtractor = new RegexDetector();
        List<DetectedLocation> regexFinds = regexExtractor.extractLocationNames(text);
        for (DetectedLocation loc : regexFinds)
        {
            if (!nerResults.containsKey(loc.name))
            {
                nerResults.put(loc.name, loc);
            }
            else
            {
                nerResults.get(loc.name).startPos.addAll(loc.startPos);
                nerResults.get(loc.name).stopPos.addAll(loc.stopPos);
            }
        }

        List<Triple<String, Integer, Integer>> extracts = this.classifier.classifyToCharacterOffsets(text);

        if (extracts != null)
        {
            for (Triple<String, Integer, Integer> extract : extracts)
            {
                if (extract.first.equalsIgnoreCase("LOCATION"))
                {
                    String locText = text.substring(extract.second, extract.third);

                    if(config.removeDemonyms())
                    {
                        if(textUtilities.isDemonym(locText))
                        {
                            locText = StringUtils.EMPTY;
                        }
                    }

                    if(!StringUtils.isEmpty(locText))
                    {
                        if (!nerResults.containsKey(locText.toLowerCase()))
                        {
                            nerResults.put(locText.toLowerCase(), new DetectedLocation(locText, extract.second, extract.third));
                        }
                        else
                        {
                            nerResults.get(locText.toLowerCase()).startPos.add(extract.second);
                            nerResults.get(locText.toLowerCase()).stopPos.add(extract.third);
                        }
                    }
                }
            }
        }

        List<DetectedLocation> binnedLocations = new ArrayList<>(nerResults.values());
        //List<DetectedLocation> detections = new ArrayList<>();

        /*if (config.removeDemonyms())
        {
            CaseInsensitiveList demonyms = textUtilities.getDemonyms();
            for (DetectedLocation dloc : binnedLocations)
            {
                if (!demonyms.contains(dloc.name))
                {
                    detections.add(dloc);
                }
            }
        }*/

        if (config.replaceSynonyms())
        {
            for (DetectedLocation dloc : binnedLocations)
            {
                String synonym = textUtilities.getSynonym(dloc.name);
                if (!StringUtils.isEmpty(synonym))
                {
                    dloc.synonym = synonym;
                }
            }
        }

        LocationInspector locationInspector = new LocationInspector();
        return (locationInspector.inspectLocations(binnedLocations, text));

        //return detections;
    }
}
