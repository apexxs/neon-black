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
 * ISOUtilities.java
 *
 * ====================================================================
 */

package com.apexxs.neonblack.utilities;

import com.apexxs.neonblack.setup.Configuration;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class ISOUtilities
{
    private Map<String,String> countryCodes;
    private Map<String, String> stateCodes;

    private static ISOUtilities instance = null;

    private Configuration config = Configuration.getInstance();

    private ISOUtilities()
    {
        this.countryCodes = getCountryCodeHashMap();
        this.stateCodes = getStateCodeHashMap();
    }

    public static ISOUtilities getInstance()
    {
        if (instance == null)
        {
            instance = new ISOUtilities();
        }
        return instance;
    }

    private Map<String, String> getCountryCodeHashMap()
    {
        HashMap<String, String> codes = new HashMap<>();
        String line;
        try
        {
            File file = new File(config.getResourceDirectory() + "ISOCountryMapping.txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));


            while ((line = r.readLine()) != null)
            {
                if(StringUtils.isNotEmpty(line))
                {
                    String[] tokens = line.split(",");
                    String code = tokens[0];
                    String iso2 = tokens[1];
                    codes.put(code, iso2);
                }
            }

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return codes;
    }

    private Map<String, String> getStateCodeHashMap()
    {
        HashMap<String, String> codes = new HashMap<>();
        try
        {
            File file = new File(config.getResourceDirectory() + "ISOStateMapping.txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
            String line;

            while ((line = r.readLine()) != null)
            {
                if(StringUtils.isNotEmpty(line))
                {
                    String[] tokens = line.split(",");
                    String code = tokens[0];
                    String iso2 = tokens[1];
                    codes.put(code, iso2);
                }
            }

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
        return codes;
    }

    public Map<String, String> getCountryCodes()
    {
        return countryCodes;
    }

    public Map<String, String> getStateCodes()
    {
        return stateCodes;
    }

}
