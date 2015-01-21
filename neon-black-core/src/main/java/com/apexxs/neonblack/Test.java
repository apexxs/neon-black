package com.apexxs.neonblack;


import com.apexxs.neonblack.solr.SolrUtilities;

import java.io.File;
import java.util.List;

public class Test
{
    public static void main(String[] args)
    {
        List<String> results;

        String test1 = "He was from Parwan Province in Afghanistan, not far from Paktia, Afghanistan."
                + " She was from Alamogordo, NM (31TCH7538006408) and has never been to Pakistan or Parwan Province.  This guy I know is from Tucson, AZ. Here's another MGRS: 15T TF 47679 43092";

        String test2 = "It was located in Roswell, NM (14TQL4893382995), outside of New York City. It was around  40:26:46N,079:56:55W.  We were in Wilmington, DE last month, never been to Texas before.";

        String test3 = "Hi, how are you today?";

        String test4 = "Winston Churchill was from England, sometimes called Great Britain or the United Kingdom (UK).  Occasionally it is abbreviated as GBR.";

        NeonBlack nb = new NeonBlack();

     /*  results = nb.detectFromText(test1);

        for(String result : results)
        {
            System.out.println(result);
        }*/

       /* results = NeonBlack.detectFromText(test2);

        for(String result : results)
        {
            System.out.println(result);
        }

        results = NeonBlack.detectFromText(test3);

        for(String result : results)
        {
            System.out.println(result);
        }
        */

       /* results = nb.detectFromText(test4);

        for(String result : results)
        {
            System.out.println(result);
        }*/

       /* results = nb.detectFromFile("d:/temp/NBTest/Comparison/iir_1.txt");
        for (String result : results)
        {
            System.out.println(result);
        }*/

        File testDir = new File("d:/temp/NBTest/");
        File[] fileList = testDir.listFiles();

        for(File f : fileList)
        {
            System.out.println("*****" + f.getName() + "*****");
            results = nb.detectFromFile(f.getAbsolutePath());

            for (String result : results)
            {
                System.out.println(result);
            }
        }
    }
}
