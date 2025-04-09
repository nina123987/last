package tmk.au;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import psdi.iface.mic.StructureData;
import psdi.iface.migexits.ExternalExit;
import psdi.iface.util.XMLUtils;
import psdi.mbo.MboRemote;
import psdi.mbo.MboSet;
import psdi.mbo.MboSetRemote;
import psdi.security.UserInfo;
import psdi.server.MXServer;
import psdi.util.MXException;
import psdi.util.logging.MXLogger;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
//import java.io.PrintStream;
import java.util.ArrayList;
//import java.util.HashMap;
import java.util.List;
import java.util.Map;

import psdi.util.MXException;
import psdi.util.MXSystemException;
import org.apache.axis2.AxisFault;
import org.apache.axis2.transport.http.HttpTransportProperties;
import java.rmi.RemoteException;

import psdi.iface.mic.MaxEndPointInfo;
import psdi.iface.mic.MicUtil;
import psdi.iface.router.RouterPropsInfo;
import psdi.iface.mic.MaxEndPointPropInfo;
import psdi.iface.router.RouterHandler;
import psdi.util.MXFormat;

public class PropagateTicketInfo extends ExternalExit {

	public StructureData setDataOut(StructureData irData) throws MXException, RemoteException {
		printXMLData(irData, "IR entering Publish  Chanel Propagate Ticket information");

		String nsURI = "http://response.micromuse.com/wsdl"; // namespace URI
		String typ = "http://response.micromuse.com/types";
		int pom = 0;
		String attributeValue = "";
		Element top = new Element("runPolicy", typ);
		Document jDoc = new Document(top);

		System.out.println("callDocLitImpactWebServiceListener() entered");

		String endpoint = MXServer.getMXServer().getSystemProperties().getProperty("AUSystem.endpoint");
		System.out.println("The endpoint to connect to is: " + endpoint);

		System.out.println("PropSLA***************1");
		UserInfo ui = MXServer.getMXServer().getSystemUserInfo();
		MboSetRemote parameterSet = MXServer.getMXServer().getMboSet("EXTSYSPARAMETERS", ui);
		MboSetRemote parameterSet1 = MXServer.getMXServer().getMboSet("EXTSYSPARAMETERS", ui);
		MboSetRemote parameterSet2 = MXServer.getMXServer().getMboSet("EXTSYSPARAMETERS", ui);
		MboSetRemote parameterSet3 = MXServer.getMXServer().getMboSet("EXTSYSPARAMETERS", ui);

		System.out.println("PropSLA***************1");

		Element WSListenerId_1 = new Element("WSListenerId_1");

		Element clientId = new Element("clientId");
		parameterSet.setWhere("extsys = 'AUSYSTEM' and parameter='clientid'");
		clientId.setText(parameterSet.getMbo(0).getString("VALUE"));
		System.out.println("PropSLA***************2");
		WSListenerId_1.addContent(clientId);

		System.out.println("PropSLA***************3");
		Element objectId = new Element("objectId");
		parameterSet1.setWhere("extsys = 'AUSYSTEM' and parameter='objectid'");
		objectId.setText(parameterSet1.getMbo(0).getString("VALUE"));
		System.out.println("PropSLA***************4");
		WSListenerId_1.addContent(objectId);

		top.addContent(WSListenerId_1);

		System.out.println("PropSLA***************5");
		Element String_2 = new Element("String_2");
		parameterSet2.setWhere("extsys = 'AUSYSTEM' and parameter='policyname.Tickets'");
		String_2.setText(parameterSet2.getMbo(0).getString("VALUE"));

		parameterSet.close();
		parameterSet1.close();
		parameterSet2.close();
		System.out.println("PropSLA***************6");
		top.addContent(String_2);
		System.out.println("PropSLA***************7");
		parameterSet3.setWhere("extsys = 'AUSYSTEM' and parameter='attributes.Tickets'");

		System.out.println("SetWhere parameters for ParameterSet");

		String[] Attributes = parameterSet3.getMbo(0).getString("VALUE").split(";");

		int N = Attributes.length;
		System.out.println(N + ": Attribute length");
		parameterSet3.close();

		for (int i = 0; i < N; i++) {

			Element arrayOfWSPolicyUserParameter_3 = new Element("arrayOfWSPolicyUserParameter_3");
			top.addContent(arrayOfWSPolicyUserParameter_3);

			Element desc = new Element("desc");
			desc.setText(Attributes[i]);
			arrayOfWSPolicyUserParameter_3.addContent(desc);

			Element format = new Element("format");
			format.setText(Attributes[i + 1]);
			arrayOfWSPolicyUserParameter_3.addContent(format);

			Element label = new Element("label");
			label.setText(Attributes[i]);
			arrayOfWSPolicyUserParameter_3.addContent(label);

			Element name = new Element("name");
			name.setText(Attributes[i]);
			arrayOfWSPolicyUserParameter_3.addContent(name);

			Element value = new Element("value");
			String AttributeValue = irData.getCurrentData(Attributes[i]);

			if (Attributes[i + 1].equalsIgnoreCase("Date")) {
				if ((AttributeValue != null && (!AttributeValue.isEmpty()))) {
					Date time = irData.getCurrentDataAsDate(Attributes[i]);
					String pom1 = String.valueOf((time.getTime() / 1000));
					System.out.println("SLA***************11" + Attributes[i + 1]);
					value.setText(pom1);
				} else {
					System.out.println("SLA***************12" + Attributes[i + 1]);
					value.setText("0");
				}
			} else {
				value.setText(irData.getCurrentData(Attributes[i]));

			}
			arrayOfWSPolicyUserParameter_3.addContent(value);

			i++;

		}

		Element boolean_4 = new Element("boolean_4");
		boolean_4.setText("1");
		top.addContent(boolean_4);

		System.out.println("Body Created");

		StructureData erData = new StructureData(jDoc);

		printXMLData(erData, "ER Created from JDOM");

		return erData;

	}

	private void printXMLData(StructureData struc, String title) throws MXException {
		Document doc = struc.getData();
		byte[] xmlBytes = XMLUtils.convertDocumentToBytes(doc);
		String xmlStr = new String(xmlBytes);
		integrationLogger.info("\n>>>    " + title + "     <<<<" + "\n" + xmlStr);
		integrationLogger.info("");
	}
}
