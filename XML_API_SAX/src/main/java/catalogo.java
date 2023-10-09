import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class catalogo extends DefaultHandler {

    private static final String CLASS_NAME = catalogo.class.getName();
    private final static Logger LOG = Logger.getLogger(CLASS_NAME);
    private SAXParser parser = null;
    private SAXParserFactory spf;
    private double totalSales, media, mediaResultados, sumaResult, desvEst;
    private int totalValores = 0;
    private boolean inSales;
    private ArrayList<Double> datos = new ArrayList<Double>();
    private ArrayList<Double> diferencia = new ArrayList<Double>();
    private ArrayList<Double> alCuadrado = new ArrayList<Double>();

    public catalogo() {
        super();
        spf = SAXParserFactory.newInstance();
        // verificar espacios de nombre
        spf.setNamespaceAware(true);
        // validar que el documento este bien formado (well formed)
        spf.setValidating(true);
    }

    private void process(File file) {
        try {
            // obtener un parser para verificar el documento
            parser = spf.newSAXParser();
            LOG.info("Parser object is: " + parser);
        } catch (SAXException | ParserConfigurationException e) {
            LOG.severe(e.getMessage());
            System.exit(1);
        }
        System.out.println("\nStarting parsing of " + file + "\n");
        try {
            // iniciar analisis del documento
            parser.parse(file, this);
        } catch (IOException | SAXException e) {
            LOG.severe(e.getMessage());
        }
    }

    @Override
    public void startDocument() throws SAXException {
        // al inicio del documento inicializar
        // las ventas totales
        totalSales = 0.0;
    }

    @Override
    public void endDocument() throws SAXException {
        // Se proceso todo el documento, imprimir resultado
        System.out.printf("Ventas totales: $%,8.2f\n", totalSales);
        System.out.println("----------------------------------------------------------");
        System.out.println("La media del precio es: $ " + media);
        System.out.println("La desviacion estandar del precio es: $ " + desviacionEstandar());
        //System.out.println("Total valores: " + totalValores);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // Elemento actual que se esta procesando
        if( localName.equals("PRICE") )        {
            //  <sales>: entramos al elemento
            inSales = true;
        }
    }

    @Override
    public void characters(char[] bytes, int start, int length) throws SAXException {

        if( inSales ) { // Si estamos en el elemento <sales> :
            // obtener el contenido del elemento
            String salesValue = new String(bytes, start, length);
            double val = 0.0;
            try {

                val = Double.parseDouble(salesValue);
                datos.add(val);
                totalValores++;
            } catch (NumberFormatException e) {
                LOG.severe(e.getMessage());
            }
            System.out.printf("$%,8.2f\n",val);
            totalSales = totalSales + val;
        }
        media = totalSales / totalValores;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if( localName.equals("PRICE") )        {
            // </sales>: salimos de elemento
            inSales = false;
        }
    }

    public double desviacionEstandar(){
        //Resta la media a todos los elementos y
        // eleva al cuadrado el resultado
        for (int i = 0; i < datos.size(); i++){
            diferencia.add(datos.get(i) - media);
            alCuadrado.add(Math.pow(diferencia.get(i), 2));
        }
        //media de los resultados
        for (int i = 0; i < alCuadrado.size(); i++){
            sumaResult = sumaResult + alCuadrado.get(i);
        }
        mediaResultados = sumaResult / totalValores;

        //raiz cuadrada de la media de los resultados
        desvEst = Math.sqrt(mediaResultados);

       return desvEst;
    }
    public static void main(String args[]) {
        if (args.length == 0) {
            LOG.severe("No file to process. Usage is:" + "\njava DisplayXML <filename>");
            return;
        }
        File xmlFile = new File(args[0]);
        catalogo handler = new catalogo();
        handler.process(xmlFile);
    }
}
