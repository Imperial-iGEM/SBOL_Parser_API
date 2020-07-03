import org.sbolstandard.core2.SBOLConversionException;
import org.sbolstandard.core2.SBOLDocument;
import org.sbolstandard.core2.SBOLReader;
import org.sbolstandard.core2.SBOLValidationException;

import java.io.IOException;

class main {
    public static void main(String[] args) throws SBOLValidationException, SBOLConversionException, IOException {

        String filePath = "./examples/sbol_files/BBa_T9002.xml";
        String cdPath = "http://www.async.ece.utah.edu/BBa_T9002";
        SBOLDocument doc = SBOLReader.read(filePath);

        try{
            SBOLParser parser = new SBOLParser();
            parser.generateCsv(doc,cdPath);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
