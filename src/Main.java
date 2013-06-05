import au.com.bytecode.opencsv.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;


public class Main {


    public static void main(String [] args)
    {
        System.out.print("Iterating over directory " + args[0]);

        try {

            File dir = new File(args[0]);

            for (File child : dir.listFiles(getCSVFilter()))
            {
                ArrayList<Integer> fieldsAndLength = CleverSizedTableCreator(child);



                String tableName = child.getName().replace(".csv", "");
                int count = 0;

                CSVReader reader = new CSVReader(new FileReader(child));

                StringBuilder createTable = new StringBuilder();
                StringBuilder insertData = new StringBuilder();

                String [] nextLine;
                while ((nextLine = reader.readNext()) != null)
                {
                    if (count == 0)
                    {
                        //make a table script...
                        MakeTable(createTable, nextLine, tableName, fieldsAndLength);
                        count++;
                    }
                    else
                    {
                        InsertData(insertData, nextLine, tableName);
                        count++;
                    }
                }

                //write out the stuff.
                writeFile(tableName, createTable, "CreateTable.sql");
                writeFile(tableName, insertData, "InsertData.sql");

                System.out.print(count + " rows to insert!\n");
            }
        }
        catch (IOException ex)
        {
              ex.printStackTrace();
        }
    }

    private static void writeFile(String tableName, StringBuilder fileContents, String fileName) throws IOException
    {
        FileOutputStream fos = new FileOutputStream(tableName + fileName);
        OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
        out.write(fileContents.toString());
        out.flush();
        fos.flush();
    }

    private static FilenameFilter getCSVFilter()
    {
        FilenameFilter CSVFilter = new FilenameFilter() {
            @Override
            public boolean accept(File file, String name) {
                String lowercasename = name.toLowerCase();
                if (lowercasename.endsWith(".csv"))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        };

        return CSVFilter;
    }


    private static ArrayList CleverSizedTableCreator(File csvFile) throws IOException
    {
        CSVReader reader = new CSVReader(new FileReader(csvFile));
        String [] nextLine;
        ArrayList fieldsAndLength = new ArrayList();

        int count = 0;
        while ((nextLine = reader.readNext()) != null)
        {

            if (count == 0)
            {
                //populate hashmap keys
                PopulateHashMapKeys(fieldsAndLength, nextLine);

            }
            else
            {
                //populate hahmap values
                PopulateHashMapLengths(fieldsAndLength, nextLine);
            }
            count++;



        }
        return fieldsAndLength;
    }

    private static void PopulateHashMapKeys(ArrayList<Integer> fieldsAndLength, String[] nextLine)
    {
        for (int i=0; i<nextLine.length; i++ )
        {
            //fieldsAndLength.put(nextLine[i], 0);
            fieldsAndLength.add(i, 1);
        }
    }

    private static void PopulateHashMapLengths(ArrayList<Integer> fieldsAndLength, String[] nextLine)
    {
        for (int i=0; i<nextLine.length; i++ )
        {
            //Integer length = fieldsAndLength.get(nextLine[i]);
            if (fieldsAndLength.get(i) < nextLine[i].length())
            {
                //fieldsAndLength.put(nextLine[i], nextLine[i].length());
                fieldsAndLength.set(i, nextLine[i].length());
            }
        }
    }



    private static void InsertData(StringBuilder sqlString, String[] nextLine, String tableName)
    {
        for (int i=0; i<nextLine.length; i++ )
        {
            if (i==0)
            {
                sqlString.append("insert into " + tableName + " values( '" + nextLine[i].toString().replace("'", "''") + "'\n");
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

    private static void MakeTable(StringBuilder sqlString, String[] nextLine, String tableName, ArrayList<Integer> fieldsAndLength)
    {
        sqlString.append("create table " + tableName + " (");
        for (int i=0; i<nextLine.length; i++ )
        {
            if (i==0)
            {
                if (fieldsAndLength.get(i) < 254)
                {
                    sqlString.append("`" + nextLine[i] + "` varchar(" + fieldsAndLength.get(i) + ")\n");
                }
                else
                {
                    sqlString.append("`" + nextLine[i] + "` text\n");
                }

            }
            else
            {
                if (fieldsAndLength.get(i) < 254)
                {
                    sqlString.append(",`" + nextLine[i] + "` varchar(" + fieldsAndLength.get(i) + ")\n");
                }
                else
                {
                    sqlString.append(",`" + nextLine[i] + "` text\n");
                }
            }
        }
        sqlString.append(");\n");

    }



}
