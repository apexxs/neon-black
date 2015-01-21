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
 */

package com.apexxs.neonblack.utilities;


import net.didion.jwnl.data.Exc;
import org.apache.commons.lang.StringUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;

public class TikaTextExtractor
{
    public final static Logger logger = LoggerFactory.getLogger(MD5Utilities.class);
    public TikaTextExtractor() {}

    public String extractFromFile(String fname)
    {
        String text = StringUtils.EMPTY;

        try(InputStream is = new FileInputStream(fname))
        {
            text = extractTextFromInputStream(is);
        }
        catch(Exception ex)
        {
            logger.error("Error in TikaTextExtractor.extractFromFile(): " + ex.getMessage());
        }
        return text;
    }

    public String extractTextFromInputStream(InputStream is)
    {
        String text = StringUtils.EMPTY;

        try
        {
            BodyContentHandler contentHandler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            Parser parser = new AutoDetectParser();
            parser.parse(is, contentHandler, metadata, new ParseContext());
            text = contentHandler.toString();
        }
        catch (Exception ex)
        {
            logger.error("Error in TikaTextExtractor.extractFromInputStream(): " + ex.getMessage());
        }
        return text;
    }
}
