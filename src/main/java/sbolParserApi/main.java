package sbolParserApi;

import org.sbolstandard.core2.*;

import java.io.IOException;

class main {
    public static void main(String[] args) throws SBOLValidationException, SBOLConversionException, IOException {

        String filePath = "./examples/sbol_files/iGEM2020/Trp_Optimization.xml";
        String prURI = "http://www.dummy.org/";
        String combinatorialDerivationURI = "http://www.dummy.org/Trp_Optimization_CombinatorialDerivation/1";
        SBOLDocument doc = SBOLReader.read(filePath);
        doc.setDefaultURIprefix(prURI);


        try{
            SBOLParser parser = new SBOLParser();
            parser.generateCsv(doc,"COMBINATORIAL_DERIVATION",combinatorialDerivationURI,88);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
