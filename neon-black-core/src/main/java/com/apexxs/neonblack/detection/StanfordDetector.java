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
 * StanfordDetector.java
 *
 * ====================================================================
 *//*

package com.apexxs.neonblack.detection;


import com.apexxs.neonblack.dao.DetectedLocation;
import com.apexxs.neonblack.dao.NERCandidate;
import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.util.Triple;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StanfordDetector
{
    private AbstractSequenceClassifier<CoreLabel> classifier;

    public StanfordDetector()
    {
        try
        {
            URL url = StanfordDetector.class.getResource("/english.all.3class.distsim.crf.ser.gz");
            File file = new File(url.toURI());
            this.classifier = CRFClassifier.getClassifier(file);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public List<DetectedLocation> getLocations(String text)
    {
        List<List<CoreLabel>> sentences = this.classifier.classify(text);
        List<DetectedLocation> detections = new ArrayList<>();
        int startToken;
        int endToken;

        for (List<CoreLabel> sentence : sentences)
        {
            String origSentence = edu.stanford.nlp.util.StringUtils.joinWithOriginalWhiteSpace(sentence);
            List<NERCandidate> nerCandidates = new ArrayList<>();
            String prevEntityType = StringUtils.EMPTY;
            Triple<String, Integer, Integer> prevEntity = null;
            List<String> sentenceByToken = new ArrayList<>();
            String loc = StringUtils.EMPTY;
            NERCandidate nerc = null;
            startToken = 0;
            endToken = 0;

            for (CoreLabel token : sentence)
            {
                sentenceByToken.add(token.get(CoreAnnotations.TextAnnotation.class));
                String guessedAnswer = token.get(CoreAnnotations.AnswerAnnotation.class);
                if (guessedAnswer.equals("O"))
                {
                    if (prevEntity != null)
                    {
                        if (prevEntityType.equals("LOCATION"))
                        {
                            loc = text.substring(prevEntity.second, prevEntity.third);
                            nerc = new NERCandidate(prevEntityType, loc, prevEntity.second, prevEntity.third, startToken, endToken);
                            nerCandidates.add(nerc);
                        }
                        prevEntity = null;
                    }
                }
                else if(!guessedAnswer.equals(prevEntityType))
                {
                    if (prevEntity != null)
                    {
                        if (prevEntityType.equals("LOCATION"))
                        {
                            loc = text.substring(prevEntity.second, prevEntity.third);
                            nerc = new NERCandidate(prevEntityType, loc, prevEntity.second, prevEntity.third, startToken, endToken);
                            nerCandidates.add(nerc);
                        }
                    }
                    prevEntity = new Triple<>(guessedAnswer, token.get(CoreAnnotations.CharacterOffsetBeginAnnotation.class),
                            token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
                    startToken = Integer.parseInt((token.get(CoreAnnotations.PositionAnnotation.class)));
                    endToken = startToken;
                }
                else
                {
                    prevEntity.setThird(token.get(CoreAnnotations.CharacterOffsetEndAnnotation.class));
                    endToken = Integer.parseInt(token.get(CoreAnnotations.PositionAnnotation.class));
                }
                prevEntityType = guessedAnswer;
            }


            for (NERCandidate candidate : nerCandidates)
            {
                if (candidate.type.equals("LOCATION"))
                {
                    DetectedLocation detection = new DetectedLocation(origSentence, sentenceByToken, nerCandidates);
                    detections.add(detection);
                    break;
                }
            }
        }
        return detections;
    }
}*/


