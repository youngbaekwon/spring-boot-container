package com.bps.demo.web;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

@Controller
public class CityController {
	private static final Logger logger = LoggerFactory.getLogger(CityController.class);	
	

	@RequestMapping(value="/")
	public String index(Model model) throws Exception {
		
		return "index";
	}

	@RequestMapping(value="/city")
	public String city(Model model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		model.addAttribute("userId", "123455666");
		model.addAttribute("userName", "HongGilDong");
		model.addAttribute("addr", "Seoul Korea");
		
		String remoteAddr = request.getRemoteAddr();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
		String currentDate = sdf.format(new Date());
		
		loadFile();

//		logger.info("access date : {}, remoteIp : {}, userId : {}, userName : {}, addr : {}", currentDate, remoteAddr, model.getAttribute("userId"), model.getAttribute("userName"), model.getAttribute("addr"));

		return "city";
	}
	
	private static void loadFile() throws Exception {
		String locationFilePath = "D:\\workspace\\spring-boot-demo\\logs\\demo_ga_sessions_flatten.csv";
		File locationFile = new File(locationFilePath);
		
		List<Map<String, String>> response = new LinkedList<Map<String, String>>();
	    CsvMapper mapper = new CsvMapper();
	    CsvSchema schema = CsvSchema.emptySchema().withHeader();
	    MappingIterator<Map<String, String>> iterator;
		try {
			iterator = mapper.readerFor(Map.class)
			        .with(schema)
			        .readValues(locationFile);
			while (iterator.hasNext()) {
				response.add(iterator.next());
			}
		
			for(Map<String, String> map : response) {
				//visitorId,visitChannel,visitDate,visitgetVisitTime(map.get("visitDate"));
				logger.info("visitorId : {}, visitChannel : {}, visitDate : {}, visitId : {}, country : {}, city : {}, pageviews : {}, timeOnSite : {}, productCategory : {}, productName : {}, productPrice : {}", map.get("visitorId"), map.get("visitChannel"), getVisitTime(map.get("visitDate")), map.get("visitId"), map.get("country"), map.get("city"), map.get("pageviews"), map.get("timeOnSite"), map.get("productCategory"), map.get("productName").replaceAll(",", ""), map.get("productPrice"));
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    
	}
	
	private static String getVisitTime(String visitTime) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm");
		Calendar cal = Calendar.getInstance();
		int cyear = cal.get(Calendar.YEAR);
		String vtime = sdf.format(cal.getTime());
		
		if (!StringUtils.isEmpty(visitTime) && visitTime.length() > 6) {
			String year = visitTime.substring(0,4);
			String month = visitTime.substring(5, 6);
			String day = visitTime.substring(7, 8);
			
			Random random = new Random();
			int gap = random.nextInt(4);
			if (gap < 1) gap = 1;
			
			cal.add(Calendar.YEAR,  -gap);
			cal.set(Calendar.MONTH, Integer.parseInt(month));
			cal.set(Calendar.DATE, Integer.parseInt(day));
			
			cal.set(Calendar.HOUR, random.nextInt(24));
			cal.set(Calendar.MINUTE, random.nextInt(60));
			
			vtime = sdf.format(cal.getTime());
			
		}
		
		System.out.println("return visitTime ===>> " + vtime);
		return vtime;
	}
}
