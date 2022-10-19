import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
public class main {

	static Logger log = Logger.getLogger(main.class.getName());
	
	public static void main(String[] args) {
		
		Scanner entradaEscaner = new Scanner (System.in);
		FileHandler fh;
		
		try {
			
			fh = new FileHandler("resources/log.log", true); 
			log.addHandler(fh);

		    SimpleFormatter formatter = new SimpleFormatter();  
		    fh.setFormatter(formatter); 
			log.info("Inicio de actualización de habitaciones");
			
		    
			File file = new File("resources/Habitaciones.xml");
			File fileAntiguo = new File("resources/HabitacionesOld.xml");
			
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db;
			db = dbf.newDocumentBuilder();
	        Document document = db.parse(file);
	        document.getDocumentElement().normalize();

	        NodeList nList = document.getElementsByTagName("Habitacion");

	        for (int temp = 0; temp < nList.getLength(); temp++) {
	            Node nNode = nList.item(temp);
	            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	                Element eElement = (Element) nNode;
	                String numHabitacion = eElement.getAttribute("numHabitacion");
	                String precioDia = eElement.getAttribute("preciodia");
	                String codHotel = eElement.getElementsByTagName("codHotel").item(0).getTextContent();
	                System.out.println(codHotel + "   -   Hab." + numHabitacion + "   -   Precio/dia: " + precioDia + " $");

	                Node clientesLista = document.getElementsByTagName("Estancias").item(temp);
	                NodeList clientesList = (NodeList) clientesLista;
	                for (int tempx = 0; tempx < clientesList.getLength(); tempx++) {
	                	Node xNode = clientesList.item(tempx);
	                	Boolean pagado = false;
	                	Boolean archivar = true;
	                	if (xNode.getNodeType() == Node.ELEMENT_NODE) {
	                		Element xElement = (Element) xNode;
	                		String nombreCliente = xNode.getTextContent();
	                		String fechaInicio = xElement.getAttribute("fechaInicio");
	                		String fechaFin = xElement.getAttribute("fechaFin");
	                		

	                		
	                		System.out.println(nombreCliente + ", Fecha Inicio: " + fechaInicio+ ", Fecha Fin: " + fechaFin);
	                		
	                		String entradaPagado = "";
	                		
	                		if(!xElement.getAttribute("pagado").equals("pagado")) {
		                		do{
		                			System.out.print("Pagado (si/no):");
		                			entradaPagado = entradaEscaner.nextLine();
		                			
			                		if(entradaPagado.equalsIgnoreCase("si")) {
			                			xElement.setAttribute("pagado","pagado");
			                			log.info("Realizado Pago: " + nombreCliente);
			                			pagado = true;
			                		}else {
			                			pagado = false;
			                		}
		                		}
		                		while(!entradaPagado.equalsIgnoreCase("si") && !entradaPagado.equalsIgnoreCase("no"));
	                		}else {
	                			pagado = true;
	                		}

	                		               		
	                		String entradaArchivado = "";
	                		
	                		if(pagado) {
	                			do{
	                    			System.out.println("Archivar cliente (si/no):");
	                    			entradaArchivado = entradaEscaner.nextLine();
	                    		}
	                    		while(!entradaArchivado.equalsIgnoreCase("si") && !entradaArchivado.equalsIgnoreCase("no"));
	                		}

	                		if(entradaArchivado.equalsIgnoreCase("si")) {
	                			
	                			NodeList archivados = (NodeList) eElement.getElementsByTagName("Archivados");
	                			
	                			if (archivados.getLength() > 0) {
	                				
	                				Node prueba = archivados.item(0);
	                			    
	                			    prueba.appendChild(xNode);
	                			}else {
		                			Node ns17CHDPInfoInd = document.createElement("Archivados");
		                			nNode.appendChild(ns17CHDPInfoInd);
		                			ns17CHDPInfoInd.appendChild(xNode);
	                			}
	                			
	                			log.info("Cliente Archivado");

	                		}
	                	}
	                }
	            }
	        }
	        fileAntiguo.delete();
	        FileUtils.moveFile(file, fileAntiguo);

	        TransformerFactory transformerFactory = TransformerFactory.newInstance();
	        Transformer transformer = transformerFactory.newTransformer();
	        DOMSource source = new DOMSource(document);
	        
	        StreamResult result = new StreamResult(file);

	        transformer.transform(source, result);
	        
	        log.info("Actualización completada");
		}catch (Exception e){
			// TODO Auto-generated catch block
			log.log(null, "Error-> ", e);
		}
	}
}