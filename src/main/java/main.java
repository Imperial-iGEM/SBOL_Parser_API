import org.sbolstandard.core2.*;

import java.io.IOException;
import java.util.Scanner;

class main {
    public static void main(String[] args) throws SBOLValidationException, SBOLConversionException, IOException {

        String filePath = "./examples/sbol_files/Medium_2016Interlab.xml";
        SBOLDocument doc = SBOLReader.read(filePath);

        try{
            SBOLParser parser = new SBOLParser();
            parser.generateCsv(doc);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}