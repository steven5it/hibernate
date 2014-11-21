package cs378.assignment6.service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cs378.assignment6.controller.ETLController;
import cs378.assignment6.etl.Reader;

public class EavesdropReaderService implements Reader {
	protected static Log logger = LogFactory.getLog(EavesdropReaderService.class
			.getName());
	
	public Object read(Object source) throws Exception {
		ArrayList<String> links = extractLinks((URL)source);
		return links;
	}

	private ArrayList<String> extractLinks(URL source) {
		ArrayList<String> linksList = new ArrayList<String>();
		try {
			Document doc = Jsoup.connect(source.toString()).get();
			Elements links = doc.select("a[href]");
			int i = 0;
	        for (Element link : links) {
	        	if (i++ < 5)
	        		continue;
	        	linksList.add(link.attr("abs:href"));
	        }
		
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info("number of links in list is: " + linksList.size());
		return linksList;
	}
}
