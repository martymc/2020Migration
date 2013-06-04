import au.com.bytecode.opencsv.*;

import java.io.*;


public class Main {


    public static void main(String [] args)
    {
        System.out.print("Iterating over directory " + args[0]);

        try {

            File dir = new File(args[0]);

            for (File child : dir.listFiles(getCSVFilter()))
            {
                String tableName = child.getName().replace(".csv", "");
                int firstRow = 0;

                CSVReader reader = new CSVReader(new FileReader(child));

                StringBuilder createTable = new StringBuilder();
                StringBuilder insertData = new StringBuilder();

                String [] nextLine;
                while ((nextLine = reader.readNext()) != null) {


                    if (firstRow == 0)
                    {
                        //make a table script...
                        MakeTable(createTable, nextLine, tableName);
                        firstRow++;

                    }
                    else
                    {
                        InsertData(insertData, nextLine, tableName);
                    }

                    // nextLine[] is an array of values from the line
                    //System.out.println(nextLine[0] + nextLine[1] + "etc...");
                }

                //write out the stuff.
                writeFile(tableName, createTable, "CreateTable.sql");
                writeFile(tableName, insertData, "InsertData.sql");
            }
        }
        catch (IOException ex)
        {
              ex.printStackTrace();
        }
        /*catch (FileNotFoundException e) {

            System.out.print("Couldn't find a CSV file to parse.");
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }*/

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

    private static void MakeTable(StringBuilder sqlString, String[] nextLine, String tableName)
    {
        sqlString.append("create table " + tableName + " (");
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
