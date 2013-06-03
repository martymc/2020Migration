import au.com.bytecode.opencsv.*;

import java.io.*;


public class Main {


    public static void main(String [] args)
    {
        System.out.print("Begin...");

        try {

            int firstRow = 0;

            CSVReader reader = new CSVReader(new FileReader("/Users/mmcallis/IdeaProjects/2020Migration/inputs/OracleTable.csv"));

            StringBuilder createTable = new StringBuilder();
            StringBuilder insertData = new StringBuilder();

            String [] nextLine;
            while ((nextLine = reader.readNext()) != null) {


                if (firstRow == 0)
                {
                    //make a table script...
                    MakeTable(createTable, nextLine);
                    firstRow++;

                }
                else
                {
                    InsertData(insertData, nextLine);
                }

                // nextLine[] is an array of values from the line
                //System.out.println(nextLine[0] + nextLine[1] + "etc...");
            }

            //write out the stuff.
            FileOutputStream fos = new FileOutputStream("CreateTable.sql");
            OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
            out.write(createTable.toString());
            out.flush();
            fos.flush();

            FileOutputStream fosData = new FileOutputStream("InsertData.sql");
            OutputStreamWriter outData = new OutputStreamWriter(fosData, "UTF-8");
            outData.write(insertData.toString());
            outData.flush();
            fosData.flush();


        } catch (IOException ex)
        {
              ex.printStackTrace();
        }
        /*catch (FileNotFoundException e) {

            System.out.print("Couldn't find a CSV file to parse.");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }*/

    }

    private static void InsertData(StringBuilder sqlString, String[] nextLine)
    {
        for (int i=0; i<nextLine.length; i++ )
        {
            if (i==0)
            {
                sqlString.append("insert into oracle values( '" + nextLine[i].toString().replace("'", "''") + "'\n");
                //sqlString.append("`" + nextLine[i] + "` varchar(250)\n");
            }
            else
            {
                sqlString.append(", '" + nextLine[i].toString().replace("'", "''") + "'\n");
                //sqlString.append(",`" + nextLine[i] + "` varchar(250)\n");
            }
        }
        sqlString.append(");\n");
    }

    private static void MakeTable(StringBuilder sqlString, String[] nextLine)
    {
        sqlString.append("create table oracle (");
        for (int i=0; i<nextLine.length; i++ )
        {
            if (i==0)
            {
                sqlString.append("`" + nextLine[i] + "` varchar(250)\n");
            }
            else
            {
                sqlString.append(",`" + nextLine[i] + "` varchar(250)\n");
            }
        }
        sqlString.append(");\n");

    }



}
