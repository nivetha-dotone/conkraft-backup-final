package com.wfd.dot1.cwfm.controller;

import com.wfd.dot1.cwfm.pojo.CMSRoleRights;
import com.wfd.dot1.cwfm.pojo.CmsGeneralMaster;
import com.wfd.dot1.cwfm.pojo.MasterUser;
import com.wfd.dot1.cwfm.pojo.PrincipalEmployer;
import com.wfd.dot1.cwfm.service.CommonService;
import com.wfd.dot1.cwfm.service.FileUploadService;
import com.wfd.dot1.cwfm.service.PrincipalEmployerService;
import com.wfd.dot1.cwfm.service.WorkmenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/data")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;
	
    @Autowired
	WorkmenService workmenService;
    @Autowired
	CommonService commonService;
	
	@Autowired
	PrincipalEmployerService peService;
	
	@GetMapping("/importExport")
	public String showUploadForm(HttpServletRequest request, HttpServletResponse response) {

	    HttpSession session = request.getSession(false);
	    MasterUser user = (session != null) ? (MasterUser) session.getAttribute("loginuser") : null;

	    if (user == null) {
	        // Redirect to login if session expired
	        return "redirect:/login";
	    }

	    // ✅ Step 1: Load Page Permission
	    CMSRoleRights rr = commonService.hasPageActionPermissionForRole(
	            user.getRoleId(), "/data/importExport");
	    request.setAttribute("UserPermission", rr);

	    // ✅ Step 2: Load all Principal Employers for this user
	    List<PrincipalEmployer> listDtos = peService.getAllPrincipalEmployer(user.getUserAccount());
	    request.setAttribute("PrincipalEmployers", listDtos);

	    // ✅ Step 3: Load all General Master data
	    List<CmsGeneralMaster> gmList = workmenService.getAllGeneralMaster();

	    // ✅ Step 4: Group by gmType (MASTERDATAIMPORT)
	    Map<String, List<CmsGeneralMaster>> groupedByGmType = gmList.stream()
	            .collect(Collectors.groupingBy(CmsGeneralMaster::getGmType));

	    List<CmsGeneralMaster> importOptions =
	            groupedByGmType.getOrDefault("IMPORTOPTIONS", new ArrayList<>());

	    // ✅ Step 5: Get role-based allowed import options
	    List<CmsGeneralMaster> roleBasedOptions = commonService.getImportOptionsByRole(user.getRoleId());

	    if (roleBasedOptions != null && !roleBasedOptions.isEmpty()) {

	        // 🔹 Convert to uppercase names for case-insensitive comparison
	        Set<String> allowedNames = roleBasedOptions.stream()
	                .map(gm -> gm.getGmName().trim().toUpperCase())
	                .collect(Collectors.toSet());

	        // 🔹 Special case: if "DATAIMPORTOPTIONS" exists → show all
	        //if (allowedNames.contains("DATA IMPORT")) {
	          //  System.out.println("✅ Role contains 'DataImportOptions' → showing all import options.");
	       // } else {
	            // Otherwise → filter options normally
	            importOptions = importOptions.stream()
	                    .filter(opt -> allowedNames.contains(opt.getGmName().trim().toUpperCase()))
	                    .collect(Collectors.toList());
	        //}
	    }

	    // Debug logs
	    System.out.println("User Role: " + user.getRoleName());
	    System.out.println("Visible ImportOptions count: " + importOptions.size());
	    for (CmsGeneralMaster gm : importOptions) {
	        System.out.println("Visible to role [" + user.getRoleName() + "]: " +
	                gm.getGmId() + " - " + gm.getGmName());
	    }

	    // ✅ Step 6: Set dropdown data
	    request.setAttribute("ImportOptions", importOptions);

	    return "reportsUpload/fileUpload";
	}

    @GetMapping("/getTemplateOptions")
    public ResponseEntity<String> getTemplateOptions(@RequestParam String selectedTemplate) {
        return ResponseEntity.ok("What would you like to do with this template?");
    }
    
    @GetMapping("/getTemplateInfo")
    public ResponseEntity<Map<String, Object>> getTemplateInfo(@RequestParam String templateType) {
    	 System.out.println("Received request for template: " + templateType); // Debugging
        Map<String, Object> templateInfo = new HashMap<>();

        if ("Data-General Master".equals(templateType)) {
            templateInfo.put("title", "Data - General Master Event");
            templateInfo.put("description", "Imports GeneralMaster events.");

            List<Map<String, String>> fields = new ArrayList<>();
            fields.add(Map.of("name", "GM Name", "type", "Text", "example", "B.Tech"));
            fields.add(Map.of("name", "GM Description", "type", "Text", "example", "B.Tech"));
            fields.add(Map.of("name", "GM Type", "type", "Text", "example", "Academics"));
            
            templateInfo.put("fields", fields);
        }else if("Data-Minimum Wage".equals(templateType)){
        	templateInfo.put("title", "Data - Minimum Wage Event");
            templateInfo.put("description", "Imports Minimum Wage events.");

            List<Map<String, String>> fields = new ArrayList<>();
            fields.add(Map.of("name", "Unit Code", "type", "Text", "example", "112S"));
            fields.add(Map.of("name", "State Name", "type", "Text", "example", "Uttar Pradesh"));
            fields.add(Map.of("name", "Zone Name", "type", "Text", "example", "Zone 1"));
            fields.add(Map.of("name", "Skill Name", "type", "Text", "example", "Highly Skilled"));
            fields.add(Map.of("name", "Basic", "type", "Number", "example", "1000"));
            fields.add(Map.of("name", "DA", "type", "Number", "example", "0"));
            fields.add(Map.of("name", "Other Allowances", "type", "Number", "example", "1265"));
            fields.add(Map.of("name", "From Date", "type", "Date", "example", "2024-04-09"));
            //fields.add(Map.of("name", "To Date", "type", "Date", "example", "2025-04-09"));
            
            templateInfo.put("fields", fields);
        }else if("Data-Work Order".equals(templateType)){
        	templateInfo.put("title", "Data - Workorder Event");
            templateInfo.put("description", "Imports Workorder events.");

            List<Map<String, String>> fields = new ArrayList<>();
            fields.add(Map.of("name", "Work Order Number", "type", "Number", "example", "1511234563"));
            fields.add(Map.of("name", "Item", "type", "Number", "example", "12"));
            fields.add(Map.of("name", "Line", "type", "Number", "example", "10"));
            fields.add(Map.of("name", "Line Number", "type", "Number", "example", "10"));
            fields.add(Map.of("name", "Service Code", "type", "Number", "example", "998519"));
            fields.add(Map.of("name", "Short Text", "type", "Text", "example", "MANPOWER SUPPLY SERVICES"));
            fields.add(Map.of("name", "Delivery Complition", "type", "Date", "example", "2024-04-09"));
            fields.add(Map.of("name", "Item Changed ON", "type", "Date", "example", "2024-04-09"));
            fields.add(Map.of("name", "Vendor Code", "type", "Number", "example", "225046"));
            fields.add(Map.of("name", "Vendor Name", "type", "Text", "example", "SSD MANAGEMENT SERVICES PVT LTD"));
            fields.add(Map.of("name", "Vendor Address", "type", "Text", "example", "BLOCK-A| OFFICE NO. 711| MONDEAL HIGHTS| NEAR-PANCHRATNA PARTY PLOT"));
            fields.add(Map.of("name", "Blocked Vendor", "type", "Number", "example", "0"));
            fields.add(Map.of("name", "Work Order Validitiy From", "type", "Date", "example", "2024-04-09"));
            fields.add(Map.of("name", "Work Order Validitiy To", "type", "Date", "example", "2024-04-09"));
            fields.add(Map.of("name", "Work Order Type", "type", "Text", "example", "ZAS"));
            fields.add(Map.of("name", "Plant code", "type", "Number", "example", "121"));
            fields.add(Map.of("name", "Section Code", "type", "Number", "example", "5"));
            fields.add(Map.of("name", "Department Code", "type", "Number", "example", "4"));
            fields.add(Map.of("name", "G/L Code", "type", "Number", "example", "10"));
            fields.add(Map.of("name", "Cost Center", "type", "Number", "example", "51"));
            fields.add(Map.of("name", "Nature of Job", "type", "Text", "example", "MANPOWER SUPPLY SERVICES"));
            fields.add(Map.of("name", "Rate", "type", "Number", "example", "1"));
            fields.add(Map.of("name", "Quantity", "type", "Number", "example", "1"));
            fields.add(Map.of("name", "Base Unit of Measure", "type", "Number", "example", "1"));
            fields.add(Map.of("name", "Work Order Released", "type", "Date", "example", "2024-04-09"));
            fields.add(Map.of("name", "PM Order No", "type", "Number", "example", "11"));
            fields.add(Map.of("name", "WBS Element", "type", "Number", "example", "1000"));
            fields.add(Map.of("name", "Quantity Completed", "type", "Number", "example", "1"));
            fields.add(Map.of("name", "Work Order Release Date", "type", "Date", "example", "2024-04-09"));
            fields.add(Map.of("name", "Service Entry Created Date", "type", "Date", "example", "2024-04-09"));
            fields.add(Map.of("name", "Service Entry Updated Date", "type", "Date", "example", "2024-04-09"));
            fields.add(Map.of("name", "Purchase Org Level", "type", "Text", "example", "Department"));
            fields.add(Map.of("name", "Organization", "type", "Text", "example", "Adani Wilmar Limited"));
            
            templateInfo.put("fields", fields);
        }else if("Data-Contractor".equals(templateType)){
        	templateInfo.put("title", "Data - Contractor Event");
            templateInfo.put("description", "Imports Contractor events.");

            List<Map<String, String>> fields = new ArrayList<>();
            fields.add(Map.of("name", "Work Order Number", "type", "Number", "example", "1511234563"));
            fields.add(Map.of("name", "Plant Code", "type", "Number", "example", "121"));
            fields.add(Map.of("name", "Organisation", "type", "Text", "example", "Adani Wilmar Ltd Ferozpur"));
            fields.add(Map.of("name", "Main Contractor Code", "type", "Number", "example", "225046"));
            fields.add(Map.of("name", "Contractor Code", "type", "Number", "example", "225046"));
            fields.add(Map.of("name", "Contractor Name", "type", "Text", "example", "SSD MANAGEMENT SERVICES PVT LTD"));
            fields.add(Map.of("name", "Contractor Address", "type", "Text", "example", "BLOCK-A| OFFICE NO. 711| MONDEAL HIGHTS| NEAR-PANCHRATNA PARTY PLOT| S.G HIGHWAY| AHMEDABAD|GUJARAT-380015."));
            fields.add(Map.of("name", "City", "type", "Text", "example", "Ahmedabad"));
            fields.add(Map.of("name", "Contractor Manager Name", "type", "Text", "example", "Shubham Kumar"));
            fields.add(Map.of("name", "Total Workmen Strength", "type", "Number", "example", "65"));
            fields.add(Map.of("name", "Maximum Number Of Workmen", "type", "Number", "example", "70"));
            fields.add(Map.of("name", "Labour License Number", "type", "Text", "example", "FZR00CL7283"));
            fields.add(Map.of("name", "License Valid From", "type", "Date", "example", "2025-01-01"));
            fields.add(Map.of("name", "License Valid To", "type", "Date", "example", "2026-12-31"));
            fields.add(Map.of("name", "License Coverage", "type", "Number", "example", "80"));
            fields.add(Map.of("name", "WC Number", "type", "Text", "example", "4010/389322031/00/000"));
            fields.add(Map.of("name", "WC Valid From", "type", "Date", "example", "2025-04-10"));
            fields.add(Map.of("name", "WC Valid To", "type", "Date", "example", "2026-04-16"));
            fields.add(Map.of("name", "WC Coverage", "type", "Number", "example", "80"));
            fields.add(Map.of("name", "ESIC Number", "type", "Text", "example", "4010/389322031/00/000"));
            fields.add(Map.of("name", "ESIC Valid From", "type", "Date", "example", "2025-04-17"));
            fields.add(Map.of("name", "Nature of Work", "type", "Text", "example", "Manpower supply"));
            fields.add(Map.of("name", "PF Number", "type", "Text", "example", "GJAHD2056438000"));
            fields.add(Map.of("name", "PF Apply Date", "type", "Date", "example", "2024-03-16"));
            
            templateInfo.put("fields", fields);
        }else if("Data-Principal Employer".equals(templateType)){
        	templateInfo.put("title", "Data - Principal Employer Event");
            templateInfo.put("description", "Imports PrincipalEmployer events.");

            List<Map<String, String>> fields = new ArrayList<>();
          //  fields.add(Map.of("name", "Unit ID", "type", "Number", "example", "1"));
            fields.add(Map.of("name", "Organization", "type", "Text", "example", "Dot1"));
            fields.add(Map.of("name", "Plant Code", "type", "Text", "example", "CODE001"));
            fields.add(Map.of("name", "Name", "type", "Text", "example", "Dot 1 Solutions Pvt Ltd"));
            fields.add(Map.of("name", "Address", "type", "Text", "example", "6th Floor|Aravalli Office|Shantigram|Nr. Vaishnodevi Circle"));
            fields.add(Map.of("name", "Manager Name", "type", "Text", "example", "Rama"));
            fields.add(Map.of("name", "Manager Address", "type", "Text", "example", "6th Floor|Aravalli Office|Shantigram|Nr. Vaishnodevi Circle"));
            fields.add(Map.of("name", "Bussiness Type", "type", "Text", "example", "Cement"));
            fields.add(Map.of("name", "Max Workmen", "type", "Number", "example", "15"));
            fields.add(Map.of("name", "Max Contract Workmen", "type", "Number", "example", "30"));
            fields.add(Map.of("name", "BOCW Applicability", "type", "Number", "example", "0"));
            fields.add(Map.of("name", "Is MW Applicability", "type", "Number", "example", "0"));
           // fields.add(Map.of("name", "Code", "type", "Text", "example", "CODE001"));
            fields.add(Map.of("name", "License Number", "type", "Number", "example", "LIC001"));
            fields.add(Map.of("name", "PF Code", "type", "Text", "example", "PF001"));
            fields.add(Map.of("name", "ESWC", "type", "Text", "example", "WC001"));
           // fields.add(Map.of("name", "PT REG NO", "type", "Text", "example", "PT001"));
          //  fields.add(Map.of("name", "LWF REG NO", "type", "Text", "example", "LWF001"));
            fields.add(Map.of("name", "Factory License Number", "type", "Text", "example", "FLIC001"));
           // fields.add(Map.of("name", "IS RC Applicable", "type", "Number", "example", "0"));
           // fields.add(Map.of("name", "RC Number", "type", "Number", "example", "RC001"));
           // fields.add(Map.of("name", "Attachment", "type", "File", "example", "Dot1_2025.pdf"));
            fields.add(Map.of("name", "State", "type", "Text", "example", "Assam"));
           // fields.add(Map.of("name", "IS Active", "type", "Number", "example", "1"));
          //  fields.add(Map.of("name", "Update Time", "type", "Date", "example", "12/05/2025"));
          //  fields.add(Map.of("name", "Updated By", "type", "Date", "example", "12/05/2025"));
            
            templateInfo.put("fields", fields);
        }else if("Data-Workmen Bulk Upload".equals(templateType)){
        	templateInfo.put("title", "Data - Workmen Bulk Upload Event");
            templateInfo.put("description", "Workmen Bulk Upload events.");

            List<Map<String, String>> fields = new ArrayList<>();
            fields.add(Map.of("name", "First Name", "type", "Text", "example", "Harshal"));
            fields.add(Map.of("name", "Last Name", "type", "Text", "example", "Patel"));
            fields.add(Map.of("name", "Father's Name or Husband's Name", "type", "Text", "example", "Patel"));
            fields.add(Map.of("name", "Date of Birth", "type", "Number", "example", "2002-07-11"));
            fields.add(Map.of("name", "Trade", "type", "Text", "example", "Trade 1"));
            fields.add(Map.of("name", "Skill", "type", "Text", "example", "Skilled"));
            fields.add(Map.of("name", "Nature of Work", "type", "Text", "example", "Supplier"));
            fields.add(Map.of("name", "Hazardous Area", "type", "Text", "example", "NO"));
            fields.add(Map.of("name", "Aadhar/Id proof number", "type", "Number", "example", "863462109280"));
            fields.add(Map.of("name", "Vendor Code", "type", "Number", "example", "314265"));
            fields.add(Map.of("name", "Gender", "type", "Text", "example", "Male"));
            fields.add(Map.of("name", "Date of Joining", "type", "Date", "example", "2025-07-31"));
            fields.add(Map.of("name", "Department", "type", "Text", "example", "DEP001"));
            fields.add(Map.of("name", "Area", "type", "Text", "example", "Area 1"));
            fields.add(Map.of("name", "Work Order Number", "type", "Number", "example", "461265"));
            fields.add(Map.of("name", "PF A/C Number", "type", "Text", "example", "New Joinee"));
            fields.add(Map.of("name", "Marital Status", "type", "Text", "example", "Married"));
            fields.add(Map.of("name", "Technical Technical/Non Technical", "type", "Text", "example", "Technical"));
            fields.add(Map.of("name", "Academic", "type", "Text", "example", "BTech"));
            fields.add(Map.of("name", "Blood Group", "type", "Text", "example", "O+"));
            fields.add(Map.of("name", "Accommodation", "type", "Text", "example", "NO"));
            fields.add(Map.of("name", "Bank Name Branch", "type", "Text", "example", "New Joinee"));
            fields.add(Map.of("name", "Account Number", "type", "Number", "example", "New Joinee"));
            fields.add(Map.of("name", "Mobile Number", "type", "Number", "example", "9989546476"));
            fields.add(Map.of("name", "Emergency Contact Number", "type", "Number", "example", "9989546476"));
            fields.add(Map.of("name", "Police verification Date Valid To", "type", "Date", "example", "2025-04-09"));
            fields.add(Map.of("name", "Health chekup Date", "type", "Date", "example", "2025-04-09"));
            fields.add(Map.of("name", "Access Levels", "type", "Text", "example", "L1"));
            fields.add(Map.of("name", "ESIC Number", "type", "Number", "example", "863462900031"));
            fields.add(Map.of("name", "UNIT CODE", "type", "Text", "example", "2024-04-09"));
            fields.add(Map.of("name", "Organization name", "type", "Text", "example", "Adani Wilmar Limited"));
            fields.add(Map.of("name", "EIC Number", "type", "Text", "example", "Master Leo"));
            fields.add(Map.of("name", "EC number", "type", "Number", "example", "0"));
            fields.add(Map.of("name", "UAN Number", "type", "Number", "example", "803367170921"));
            fields.add(Map.of("name", "Emergency Contact Person", "type", "Text", "example", "Laxmi"));
            fields.add(Map.of("name", "Is eligible for PF", "type", "Text", "example", "Yes"));
            fields.add(Map.of("name", "SpecializationName", "type", "Text", "example", "General"));
            fields.add(Map.of("name", "Insurance type", "type", "Text", "example", "WC"));
            fields.add(Map.of("name", "LL number", "type", "Number", "example", "89034774"));
            fields.add(Map.of("name", "Address", "type", "Text", "example", "Ardante Office One TowerA Banglore - 531001"));
            fields.add(Map.of("name", "Zone", "type", "Text", "example", "Zone1"));
            fields.add(Map.of("name", "IdMark", "type", "Text", "example", "A Mole on Right Jaw"));
            fields.add(Map.of("name", "Employee Code", "type", "Text", "example", "910012479"));
            templateInfo.put("fields", fields);
        }else if("Data-Workmen Bulk Upload Draft".equals(templateType)){
        	templateInfo.put("title", "Data - Workmen Bulk Draft Upload Event");
            templateInfo.put("description", "Workmen Bulk Draft Upload events.");

            List<Map<String, String>> fields = new ArrayList<>();
            fields.add(Map.of("name", "First Name", "type", "Text", "example", "Harshal"));
            fields.add(Map.of("name", "Last Name", "type", "Text", "example", "Patel"));
            fields.add(Map.of("name", "Father's Name or Husband's Name", "type", "Text", "example", "Patel"));
            fields.add(Map.of("name", "Date of Birth", "type", "Date", "example", "2002-07-11"));
            fields.add(Map.of("name", "Trade", "type", "Text", "example", "Trade 1"));
            fields.add(Map.of("name", "Skill", "type", "Text", "example", "Skilled"));
            fields.add(Map.of("name", "Nature of Work", "type", "Text", "example", "Supplier"));
            fields.add(Map.of("name", "Hazardous Area", "type", "Text", "example", "NO"));
            fields.add(Map.of("name", "Aadhar/Id proof number", "type", "Number", "example", "863462109280"));
            fields.add(Map.of("name", "Vendor Code", "type", "Number", "example", "314265"));
            fields.add(Map.of("name", "Gender", "type", "Text", "example", "Male"));
            fields.add(Map.of("name", "Date of Joining", "type", "Date", "example", "2025-07-31"));
            fields.add(Map.of("name", "Department", "type", "Text", "example", "DEP001"));
            fields.add(Map.of("name", "Area", "type", "Text", "example", "Area 1"));
            fields.add(Map.of("name", "Work Order Number", "type", "Number", "example", "461265"));
            fields.add(Map.of("name", "PF A/C Number", "type", "Text", "example", "New Joinee"));
            fields.add(Map.of("name", "Marital Status", "type", "Text", "example", "Married"));
            fields.add(Map.of("name", "Technical Technical/Non Technical", "type", "Text", "example", "Technical"));
            fields.add(Map.of("name", "Academic", "type", "Text", "example", "BTech"));
            fields.add(Map.of("name", "Blood Group", "type", "Text", "example", "O+"));
            fields.add(Map.of("name", "Accommodation", "type", "Text", "example", "NO"));
            fields.add(Map.of("name", "Bank Name Branch", "type", "Text", "example", "New Joinee"));
            fields.add(Map.of("name", "Account Number", "type", "Number", "example", "New Joinee"));
            fields.add(Map.of("name", "Mobile Number", "type", "Number", "example", "9989546476"));
            fields.add(Map.of("name", "Emergency Contact Number", "type", "Number", "example", "9989546476"));
            fields.add(Map.of("name", "Police verification Date Valid To", "type", "Date", "example", "2025-04-09"));
            fields.add(Map.of("name", "Health chekup Date", "type", "Date", "example", "2025-04-09"));
            fields.add(Map.of("name", "Access Levels", "type", "Text", "example", "L1"));
            fields.add(Map.of("name", "ESIC Number", "type", "Number", "example", "863462900031"));
            fields.add(Map.of("name", "UNIT CODE", "type", "Text", "example", "2024-04-09"));
            fields.add(Map.of("name", "Organization name", "type", "Text", "example", "Adani Wilmar Limited"));
            fields.add(Map.of("name", "EIC Number", "type", "Text", "example", "Master Leo"));
            fields.add(Map.of("name", "EC number", "type", "Text", "example", "4810/407050783/00/000"));
            fields.add(Map.of("name", "UAN Number", "type", "Number", "example", "803367170921"));
            fields.add(Map.of("name", "Emergency Contact Person", "type", "Text", "example", "Laxmi"));
            fields.add(Map.of("name", "Is eligible for PF", "type", "Text", "example", "Yes"));
            fields.add(Map.of("name", "SpecializationName", "type", "Text", "example", "General"));
            fields.add(Map.of("name", "Insurance type", "type", "Text", "example", "WC"));
            fields.add(Map.of("name", "LL number", "type", "Number", "example", "89034774"));
            fields.add(Map.of("name", "Address", "type", "Text", "example", "Ardante Office One TowerA Banglore"));
            fields.add(Map.of("name", "Zone", "type", "Text", "example", "Zone1"));
            fields.add(Map.of("name", "IdMark", "type", "Text", "example", "A Mole on Right Jaw"));
            
            templateInfo.put("fields", fields);
        }else if("Data-Trade Skill".equals(templateType)){
        	templateInfo.put("title", "Data - Trade Skill Event");
            templateInfo.put("description", "Imports Trade Skill events.");

            List<Map<String, String>> fields = new ArrayList<>();
            fields.add(Map.of("name", "Plant Code", "type", "Text", "example", "CODE001"));
            fields.add(Map.of("name", "Trade", "type", "Text", "example", "Operator"));
            fields.add(Map.of("name", "Skill", "type", "Text", "example", "Skilled"));
            fields.add(Map.of("name", "Workmen Count", "type", "Number", "example", "50"));
            
            templateInfo.put("fields", fields);
        }else if("Data-Department Area".equals(templateType)){
        	templateInfo.put("title", "Data - Department SubDepartment Event");
            templateInfo.put("description", "Imports Department SubDepartment events.");

            List<Map<String, String>> fields = new ArrayList<>();
            fields.add(Map.of("name", "Plant Code", "type", "Text", "example", "CODE001"));
            fields.add(Map.of("name", "Department", "type", "Text", "example", "Electrical"));
            fields.add(Map.of("name", "SubDepartment", "type", "Text", "example", "Tester"));
            
            templateInfo.put("fields", fields);
        }else if("Data-Bulk Cancel".equals(templateType)){
        	templateInfo.put("title", "Data - Bulk Cancel Event");
            templateInfo.put("description", "Imports Bulk Cancel events.");

            List<Map<String, String>> fields = new ArrayList<>();
            fields.add(Map.of("name", "Gatepass Number", "type", "Text", "example", "GP700001"));
            fields.add(Map.of("name", "Reason of Cancel", "type", "Text", "example", "Resign"));
            
            templateInfo.put("fields", fields);
        }else if("Data-Bulk Renew".equals(templateType)){
        	templateInfo.put("title", "Data - Bulk Renew Event");
            templateInfo.put("description", "Imports Bulk Renew events.");

            List<Map<String, String>> fields = new ArrayList<>();
            fields.add(Map.of("name", "Gatepass Number", "type", "Text", "example", "GP700001"));
            fields.add(Map.of("name", "Workorder Number", "type", "Number", "example", "461265"));
            fields.add(Map.of("name", "WC/ESIC Number", "type", "Text", "example", "4810/407050783/00/000"));
            fields.add(Map.of("name", "LL Number", "type", "Number", "example", "89034774"));
            
            templateInfo.put("fields", fields);
        } else  if ("Data-User".equals(templateType)) {
            templateInfo.put("title", "Data - User Event");
            templateInfo.put("description", "Imports User events.");

            List<Map<String, String>> fields = new ArrayList<>();
            fields.add(Map.of("name", "First Name", "type", "Text", "example", "John"));
            fields.add(Map.of("name", "Last Name", "type", "Text", "example", "Dev"));
            fields.add(Map.of("name", "Login Id", "type", "Text", "example", "8074356_MS"));
            fields.add(Map.of("name", "Password", "type", "Text", "example", "Mar@2026"));
            fields.add(Map.of("name", "Email Address", "type", "Text", "example", "John@gmail.com"));
            fields.add(Map.of("name", "Mobile Number", "type", "Number", "example", "9989097676"));
            fields.add(Map.of("name", "Plant Code", "type", "Text", "example", "116M"));
            fields.add(Map.of("name", "Organisation", "type", "Text", "example", "Adani Wilmar Ltd-Alwar"));
            fields.add(Map.of("name", "Department", "type", "Text", "example", "Logistics"));
            fields.add(Map.of("name", "Area", "type", "Text", "example", "Gopalpur"));
            fields.add(Map.of("name", "Role", "type", "Text", "example", "Security;HR Admin;Contractor"));
            fields.add(Map.of("name", "SAP Vendor Code", "type", "Number", "example", "198653"));
            
            templateInfo.put("fields", fields);
        }
        else  if ("Data-Intra Plant Transfer".equals(templateType)) {
            templateInfo.put("title", "Data-Intra Plant Transfer Event");
            templateInfo.put("description", "Imports Data-Intra Plant Transfer events.");

            List<Map<String, String>> fields = new ArrayList<>();
            fields.add(Map.of("name", "GatepassId", "type", "Text", "example", "GP7000021"));
            fields.add(Map.of("name", "Plant Code", "type", "Text", "example", "51YU"));
            fields.add(Map.of("name", "Contractor Code", "type", "Text", "example", "178890"));
            fields.add(Map.of("name", "Department", "type", "Text", "example", "Safety"));
            fields.add(Map.of("name", "Area", "type", "Text", "example", "Safety")); 
            fields.add(Map.of("name", "EIC Number", "type", "Text", "example", "4503061061K"));
            fields.add(Map.of("name", "Workorder", "type", "Number", "example", "4503061061"));
            fields.add(Map.of("name", "WC/ESIC", "type", "Text", "example", "345506/48/2026/204"));
            fields.add(Map.of("name", "LL Number", "type", "Text", "example", "CLRA/Licence/CLRA/KTC/2025/CLL/669"));
            fields.add(Map.of("name", "ESIC", "type", "Text", "example", "1253409832"));
            fields.add(Map.of("name", "Effective From Date", "type", "Text", "example", "2026/05/29"));
            
            templateInfo.put("fields", fields);
        }else if("Data-Safety Training".equals(templateType)){
        	templateInfo.put("title", "Data-Safety Training Event");
            templateInfo.put("description", "Imports Safety Training events.");

            List<Map<String, String>> fields = new ArrayList<>();
            fields.add(Map.of("name", "Plant Code", "type", "Text", "example", "CODE001"));
            fields.add(Map.of("name", "Department", "type", "Text", "example", "Safety"));
            fields.add(Map.of("name", "Training Type", "type", "Text", "example", "L3"));
            fields.add(Map.of("name", "Training Name", "type", "Text", "example", "HSE TRAINING"));
            
            templateInfo.put("fields", fields);
        }
        else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(templateInfo);
    }
    @GetMapping("/getTemplateData")
    public ResponseEntity<List<Map<String, String>>> getTemplateData(@RequestParam String templateType) {
    	 System.out.println(1);
        List<Map<String, String>> emptyData = new ArrayList<>(); // Returning empty data
        return ResponseEntity.ok(emptyData);
    }
    
    
    //@PostMapping("/uploadTemplate")
   // @ResponseBody
   // public ResponseEntity<Map<String, Object>> uploadTemplate(
      //      @RequestParam("file") MultipartFile file,
      //      @RequestParam("templateType") String templateType) {
       // try {
       //     Map<String, Object> result = fileUploadService.processTemplateFile(file, templateType);
        //    return ResponseEntity.ok(result);
      //  } catch (Exception e) {
         //   return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
         //           "status", "error",
         //           "message", e.getMessage()
         //   ));
      ///  }
   // }
    @PostMapping("/uploadTemplate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadTemplate(@RequestParam("file") MultipartFile file,
                                                              @RequestParam("templateType") String templateType,
                                                              HttpServletRequest request, HttpServletResponse response) {
        try {
        	 HttpSession session = request.getSession(false); // Use `false` to avoid creating a new session
 			MasterUser user = (MasterUser) (session != null ? session.getAttribute("loginuser") : null);
 			String createdBy = String.valueOf(user.getUserId()); 
 			
            Map<String, Object> result = fileUploadService.processTemplateFile(file, templateType,createdBy,user.getUserAccount());
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            
                      System.out.println(data);
            List<Map<String, Object>> successData = (List<Map<String, Object>>) data.get("successData");
            List<Map<String, Object>> errorData = (List<Map<String, Object>>) data.get("errorData");

            // Determine status and message based on result content
            if (!successData.isEmpty() && errorData.isEmpty()) {
                result.put("status", "success");
                result.put("message", "File uploaded and all rows saved successfully!");
            } else if (!successData.isEmpty() && !errorData.isEmpty()) {
                result.put("status", "partial");
                result.put("message", "File uploaded with some errors. Some rows saved.");
            } else{
                result.put("status", "error");
                result.put("message", "File upload failed. No valid rows to save.");
            }
            //else if (successData.isEmpty() && !errorData.isEmpty())  {
             //   result.put("status", "error");
             //   result.put("message", "File is empty or invalid.");
            //}

            result.put("templateType", templateType);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", "error");
            errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
            errorResponse.put("data", Map.of("successData", List.of(), "errorData", List.of()));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/downloadTemplate")
    public void downloadTemplate(@RequestParam("templateType") String templateType,
                                 HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=" + templateType + "_template.csv");

        String csvContent = fileUploadService.getTemplateCSV(templateType);
        response.getWriter().write(csvContent);
    }
    @PostMapping("/previewTemplateFile")
    @ResponseBody
    public List<Map<String, Object>> previewTemplateFile(
            @RequestParam MultipartFile file,
            @RequestParam String templateType) {

        // Parse CSV / Excel
        List<Map<String, Object>> rows = new ArrayList<>();

        // Example CSV parsing
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(file.getInputStream()))) {

            String[] headers = br.readLine().split(",");

            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Map<String, Object> row = new LinkedHashMap<>();

                for (int i = 0; i < headers.length; i++) {
                    String rawValue = values.length > i ? values[i] : "";
                    row.put(headers[i], normalizeNumber(rawValue));
                }
                rows.add(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rows; // ⚠️ NO DB SAVE
    }
    private static final Pattern SCI_NOTATION =
            Pattern.compile("[-+]?\\d+(\\.\\d+)?[eE][-+]?\\d+");

    private String normalizeNumber(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }

        value = value.trim();

        // Check scientific notation
        if (SCI_NOTATION.matcher(value).matches()) {
            return new BigDecimal(value)
                    .toPlainString()
                    .replaceAll("\\.0$", ""); // remove .0 if exists
        }

        return value;
    }

    }

    



