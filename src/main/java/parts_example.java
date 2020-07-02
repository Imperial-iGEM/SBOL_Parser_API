import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.sbolstandard.core2.*;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;


public class parts_example {

    public void generateCSV() throws SBOLValidationException, SBOLConversionException, IOException {
        String path = "./examples/sbol_files/parts.xml";

        SBOLDocument doc = SBOLReader.read(path);

        ComponentDefinition S4_SrpR = doc.getComponentDefinition(URI.create("http://examples.org/ComponentDefinition/S4_SrpR/1"));

        String csvName = "parts_list.csv";
//        FileWriter csvOutput = new FileWriter(csvName);
//
//        String[] HEADERS = {"Part/Linker","Well","Part concentration (ng/uL)"};
//        CSVPrinter printer = new CSVPrinter(csvOutput, CSVFormat.DEFAULT.withHeader(HEADERS));

        for(Component comp: S4_SrpR.getSortedComponents()){
//            printer.printRecord(comp.getDisplayId(),"","");
            System.out.println(comp.getDisplayId());
        }
    }
}



