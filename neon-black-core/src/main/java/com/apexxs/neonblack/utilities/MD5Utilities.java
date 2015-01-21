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
 * MD5Utilities.java
 *
 * ====================================================================
 */

package com.apexxs.neonblack.utilities;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.MessageDigest;

public class MD5Utilities
{
    public final static Logger logger = LoggerFactory.getLogger(MD5Utilities.class);
    public MD5Utilities() {}

    public static String getMD5Hash(String s)
    {
        String md5Hash = StringUtils.EMPTY;
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            byte[] bytes = md.digest(s.getBytes("UTF-8"));
            md5Hash = String.format("%032x", new BigInteger(1, bytes));
        }
        catch(Exception ex)
        {
            logger.error("Error generating MD5 hash from " + s + "\n" + ex.getMessage());
        }
        return md5Hash;
    }
}
